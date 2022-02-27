package com.warmthdawn.mod.kubejsdtsmaker.java;

import com.warmthdawn.mod.kubejsdtsmaker.util.PropertySignature;
import com.warmthdawn.mod.kubejsdtsmaker.util.MethodSignature;
import com.warmthdawn.mod.kubejsdtsmaker.util.OverrideUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.*;
import java.util.*;

public class JavaInstanceMember {
    private static final Logger logger = LogManager.getLogger();

    private final String name;
    private Field selfField;
    private PropertySignature bean;
    private boolean fieldConflict = false;

    private List<Method> selfMethods;

    public JavaInstanceMember(String name) {
        this.name = name;
    }

    public boolean isFieldConflict() {
        return fieldConflict;
    }


    private PropertySignature actualField;
    private List<MethodSignature> actualMethods;
    private boolean resolved = false;


    public String getName() {
        return name;
    }

    public PropertySignature getField() {
        if (!resolved) {
            throw new IllegalStateException("Member not resolved!");
        }
        return actualField;
    }

    public PropertySignature getBean() {
        return bean;
    }


    public List<MethodSignature> getMethods() {
        if (!resolved) {
            throw new IllegalStateException("Member not resolved!");
        }
        return actualMethods;
    }

    public void addMethod(Method method) {
        if (selfMethods == null) {
            selfMethods = new ArrayList<>();
        }
        selfMethods.add(method);
    }

    public void addField(Field field) {
        if (this.selfField != null) {
            logger.fatal("Duplicate field {} in class {}", name, field.getDeclaringClass().getName());
        }
        this.selfField = field;
    }

    public void addBean(PropertySignature bean) {
        if (this.bean != null) {
            logger.fatal("Duplicate bean property {}", name);
        }
        this.bean = bean;
    }

    /**
     * 解析方法重写
     *
     * @return
     */
    public boolean resolveOverride(Class<?> clazz, List<JavaInstanceMember> parentMembers) {
        boolean emptyMember = true;
        for (JavaInstanceMember parentMember : parentMembers) {
            PropertySignature parentField = parentMember.getField();
            //字段：要求类型兼容
            if (parentField != null) {
                if (selfField == null) {
                    actualField = parentField;
                } else if (TypeUtils.isAssignable(selfField.getGenericType(), parentField.getType())) {
                    actualField = new PropertySignature(name, selfField);
                    emptyMember = false;
                } else {
                    actualField = parentField;
                    fieldConflict = true;
                    logger.error("Field {} in class {} is conflict to superclass", name, selfField.getDeclaringClass().getName());
                }
                //理论上不可能有多个字段的
                break;
            }
        }
        if (actualField == null && selfField != null) {
            actualField = new PropertySignature(name, selfField);
            emptyMember = false;
        }

        if (actualField == null) {
            actualField = this.bean;
            if (actualField != null) {
                emptyMember = false;
            }
        }

        List<MethodSignature> evaluatedMethods = new ArrayList<>();
        //方法解析
        //1.如果只有没继承的成员
        if (parentMembers.size() == 0) {
            if (selfMethods != null) {
                for (Method selfMethod : selfMethods) {
                    evaluatedMethods.add(new MethodSignature(selfMethod));
                    emptyMember = false;
                }
            }

        } else {
            JavaInstanceMember firstMember = parentMembers.get(0);
            List<MethodSignature> parentMethods = new ArrayList<>(firstMember.getMethods());
            //有继承的成员，无论如何都要计算出方法实际上应该表现出来的成员
            //2.如果只有一个父成员
            if (parentMembers.size() > 1) {
                for (int i = 1; i < parentMembers.size(); i++) {
                    JavaInstanceMember parentMember = parentMembers.get(i);
                    List<MethodSignature> methods = parentMember.getMethods();
                    if (methods != null) {
                        iter:
                        for (MethodSignature method : methods) {
                            //遍历所有已经存在的方法
                            Iterator<MethodSignature> iterator = parentMethods.iterator();
                            while (iterator.hasNext()) {
                                MethodSignature next = iterator.next();
                                if (OverrideUtils.areSignatureCovariant(next, method)) {
                                    continue iter;
                                } else if (OverrideUtils.areSignatureCovariant(method, next)) {
                                    iterator.remove();
                                } else {
                                    //出现不兼容的类型了，子类必须写
                                    emptyMember = false;
                                }
                            }
                            parentMethods.add(new MethodSignature(method, clazz));
                        }
                    }
                }
            }

            if (selfMethods != null) {
                for (Method selfMethod : selfMethods) {
                    MethodSignature signature = new MethodSignature(selfMethod);
                    evaluatedMethods.add(signature);
                    Iterator<MethodSignature> iterator = parentMethods.iterator();
                    boolean hasOverrides = false;
                    while (iterator.hasNext()) {
                        MethodSignature next = iterator.next();
                        //如果方法参数匹配（重写）
                        if (OverrideUtils.areParametersCovariant(signature, next)) {
                            //这个方法不用写在子类了
                            iterator.remove();
                            hasOverrides = true;
                            //如果方法完全相同，忽略
                            if (!OverrideUtils.areSignatureSame(signature, next)) {
                                emptyMember = false;
                            }
                        }
                    }
                    if (!hasOverrides) {
                        emptyMember = false;
                    }
                }
            }
            evaluatedMethods.addAll(parentMethods);
        }

        this.actualMethods = evaluatedMethods;

        resolved = true;

        return !emptyMember;
    }
}
