package com.warmthdawn.mod.kubejsdtsmaker.java;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.ref.Reference;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JavaInstanceMember implements IJavaMember {
    private static final Logger logger = LogManager.getLogger();

    private final String name;
    private Field selfField;
    private Field parentField;
    private boolean fieldConflict = false;

    private List<Executable> selfMethods;
    private List<Executable> parentMethods;

    public JavaInstanceMember(String name) {
        this.name = name;
    }

    public boolean isFieldConflict() {
        return fieldConflict;
    }


    private Field actualField;
    private List<Executable> actualMethods;
    private boolean resolved = false;


    @Override
    public String getName() {
        return name;
    }

    public Field getField() {
        if (!resolved) {
            throw new IllegalStateException("Member not resolved!");
        }
        return actualField;
    }


    public List<Executable> getMethods() {
        if (!resolved) {
            throw new IllegalStateException("Member not resolved!");
        }
        return actualMethods;
    }

    public void addMethod(Executable method) {
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

    public void addParent(JavaInstanceMember parent) {
        Field field = parent.getField();
        if (field != null) {
            if (parentField != null) {
                logger.fatal("Duplicate extended field {} in class {}", name, field.getDeclaringClass().getName());
            }
            parentField = field;
        }
        List<Executable> methods = parent.getMethods();
        if (methods != null && !methods.isEmpty()) {
            if (parentMethods == null) {
                parentMethods = new ArrayList<>();
            }
            parentMethods.addAll(methods);
        }
    }


    /**
     * 解析方法重写
     */
    public void resolveOverride() {
        //字段：要求类型兼容
        if (parentField != null) {
            if (parentField.getType().isAssignableFrom(selfField.getType())) {
                actualField = selfField;
            } else {
                actualField = parentField;
                fieldConflict = true;
                logger.error("Field {} in class {} is conflict to superclass", name, selfField.getDeclaringClass().getName());
            }
        }
        //方法：剔除子类方法签名完全一致的方法，剔除父类重写的方法
        if(parentMethods != null) {

        }

        resolved = true;
    }
}
