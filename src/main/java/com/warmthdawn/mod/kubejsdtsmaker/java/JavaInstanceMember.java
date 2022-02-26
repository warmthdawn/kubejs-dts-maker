package com.warmthdawn.mod.kubejsdtsmaker.java;

import com.warmthdawn.mod.kubejsdtsmaker.util.PropertySignature;
import com.warmthdawn.mod.kubejsdtsmaker.util.MethodSignature;
import com.warmthdawn.mod.kubejsdtsmaker.util.OverrideUtils;
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
    public boolean resolveOverride(Class<?> clazz, Collection<JavaInstanceMember> parentMembers) {
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
        List<MethodSignature> parentMethods = new ArrayList<>();
        for (JavaInstanceMember parentMember : parentMembers) {
            List<MethodSignature> methods = parentMember.getMethods();
            //方法：剔除子类方法签名完全一致的方法，剔除父类重写的方法
            if (methods != null) {
                iter:
                for (MethodSignature method : methods) {
                    for (MethodSignature parentMethod : parentMethods) {
                        if (OverrideUtils.areSignatureSame(parentMethod, method)) {
                            continue iter;
                        }
                    }
                    parentMethods.add(new MethodSignature(method, clazz));
                }
            }
        }

        if (selfMethods != null && !parentMethods.isEmpty()) {
            for (Method selfMethod : selfMethods) {
                MethodSignature signature = new MethodSignature(selfMethod);
                Iterator<MethodSignature> iterator = parentMethods.iterator();
                boolean ignoreMethod = false;
                while (iterator.hasNext()) {
                    MethodSignature next = iterator.next();
                    //如果方法参数匹配（重写）
                    if (OverrideUtils.areParametersCovariant(signature, next)) {
                        //这个方法不用写在子类了
                        iterator.remove();
                        //如果方法完全相同，忽略
                        if (OverrideUtils.areSignatureSame(signature, next)) {
                            ignoreMethod = true;
                        }
                    }
                }
                if (!ignoreMethod) {
                    evaluatedMethods.add(signature);
                    emptyMember = false;
                }
            }
            evaluatedMethods.addAll(parentMethods);
        } else if (selfMethods != null) {
            for (Method selfMethod : selfMethods) {
                evaluatedMethods.add(new MethodSignature(selfMethod));
                emptyMember = false;
            }
        } else {
            evaluatedMethods.addAll(parentMethods);
        }

        if (evaluatedMethods.size() != 0) {
            this.actualMethods = evaluatedMethods;
        }

        resolved = true;

        return !emptyMember;
    }
}
