package com.warmthdawn.mod.kubejsdtsmaker.java;

import com.warmthdawn.mod.kubejsdtsmaker.context.ResolveBlacklist;
import com.warmthdawn.mod.kubejsdtsmaker.util.GenericUtils;
import com.warmthdawn.mod.kubejsdtsmaker.util.MethodTypeUtils;
import com.warmthdawn.mod.kubejsdtsmaker.util.PropertySignature;
import com.warmthdawn.mod.kubejsdtsmaker.util.MethodSignature;
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

    public int getType() {
        //0: no field and method
        //1: method without field
        //2: readonly field without method
        //3: writable field without method
        //4: readonly field and method
        //5: writable field and method
        boolean noField = getField() == null;
        boolean noMethod = getMethods() == null || getMethods().isEmpty();

        if (noField && noMethod) {
            return 0;
        }
        if (noField) {
            return 1;
        }
        if (noMethod) {
            if (getField().isReadonly()) {
                return 2;
            }
            return 3;
        }
        if (getField().isReadonly()) {
            return 4;
        }
        return 5;
    }


    /**
     * 解析方法重写
     *
     * @return
     */
    public boolean resolveOverride(ResolveBlacklist blacklist, Class<?> clazz, List<JavaInstanceMember> parentMembers) {
        boolean hideMembers = true;
        //check parent conflict
        int currentType = -1;
        for (JavaInstanceMember parentMember : parentMembers) {
            if (currentType == -1) {
                currentType = parentMember.getType();
            } else if (currentType != parentMember.getType()) {
                hideMembers = false;
            }
        }

        PropertySignature parentField = null;
        boolean readonly = true;
        for (JavaInstanceMember parentMember : parentMembers) {
            PropertySignature it = parentMember.getField();
            if (it == null) {
                continue;
            }
            if (!it.isReadonly()) {
                readonly = false;
            }
            if (parentField == null) {
                parentField = it;
            } else {
                //可能冲突了
                if (GenericUtils.isAssignable(parentField, it, clazz)) {
                    hideMembers = false;
                } else if (GenericUtils.isAssignable(it, parentField, clazz)) {
                    parentField = it;
                    hideMembers = false;
                } else {
                    fieldConflict = true;
                    logger.error("Field {} in class {} is ambiguous between parents", name, selfField.getDeclaringClass().getName());
                }
            }
        }

        if (parentField != null) {
            if (selfField == null) {
                actualField = parentField;
            } else if (GenericUtils.isAssignable(selfField, parentField, clazz)) {
                actualField = new PropertySignature(name, selfField);
                hideMembers = false;
            } else {
                actualField = parentField;
                fieldConflict = true;
                logger.error("Field {} in class {} is conflict to superclass", name, selfField.getDeclaringClass().getName());
            }
        }


        if (actualField == null && selfField != null) {
            actualField = new PropertySignature(name, selfField);
            hideMembers = false;
        }

        if (actualField == null && parentMembers.isEmpty()) {
            actualField = this.bean;
            if (actualField != null) {
                hideMembers = false;
            }
        }

        if (actualField != null && !readonly && actualField.isReadonly()) {
            actualField = actualField.withoutReadonly();
            hideMembers = false;
        }

        List<MethodSignature> evaluatedMethods = new ArrayList<>();
        //方法解析
        //1.如果只有没继承的成员
        if (parentMembers.size() == 0) {
            if (selfMethods != null) {
                for (Method selfMethod : selfMethods) {
                    evaluatedMethods.add(new MethodSignature(selfMethod));
                    hideMembers = false;
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
                        if (methods.size() != parentMethods.size()) {
                            hideMembers = false;
                        }
                        iter:
                        for (MethodSignature method : methods) {
                            //遍历所有已经存在的方法
                            for (MethodSignature next : parentMethods) {
                                if (MethodTypeUtils.areSignatureSame(next, method, clazz)) {
                                    continue iter;
                                }
                            }
                            hideMembers = false;
                            parentMethods.add(new MethodSignature(method, clazz));
                        }
                    }
                }
            }

            if (selfMethods != null) {
                for (Method selfMethod : selfMethods) {
                    MethodSignature signature = new MethodSignature(selfMethod);
                    evaluatedMethods.add(signature);

                    boolean flag = false;
                    Iterator<MethodSignature> iterator = parentMethods.iterator();
                    while (iterator.hasNext()) {
                        MethodSignature next = iterator.next();
                        //如果方法参数匹配（重写）
                        if (signature.isOverridden(next)) {
                            //这个方法不用写在子类了
                            iterator.remove();
                            //如果方法完全相同，忽略
                            if (MethodTypeUtils.areSignatureSame(signature, next, clazz)) {
                                flag = true;
                            }
                        }
                    }
                    if (!flag) {
                        hideMembers = false;
                    }
                }
            }
            for (MethodSignature parentMethod : parentMethods) {
                evaluatedMethods.add(new MethodSignature(parentMethod, clazz));
            }
        }


        evaluatedMethods.removeIf(it -> it.isBlacklisted(blacklist));

        this.actualMethods = evaluatedMethods;

        resolved = true;

        return !hideMembers;
    }
}
