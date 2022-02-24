package com.warmthdawn.mod.kubejsdtsmaker.java;

import com.warmthdawn.mod.kubejsdtsmaker.util.PropertySignature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class JavaStaticMember {
    private static final Logger logger = LogManager.getLogger();

    private String name;
    private Field field;
    private List<Method> methods;
    private PropertySignature bean;

    public JavaStaticMember(String name) {
        this.name = name;
    }


    public void addMethod(Method method) {
        if (methods == null) {
            methods = new ArrayList<>();
        }
        methods.add(method);
    }

    public void addField(Field field) {
        if (this.field != null) {
            logger.fatal("Duplicate field {} in class {}", name, field.getDeclaringClass().getName());
        }
        this.field = field;
    }

    public void addBean(PropertySignature bean) {
        if (this.bean != null) {
            logger.fatal("Duplicate bean property {}", name);
        }
        this.bean = bean;
    }

    public String getName() {
        return name;
    }

    public Field getField() {
        return field;
    }

    public PropertySignature getBean() {
        return bean;
    }

    public List<Method> getMethods() {
        return methods;
    }
}
