package com.warmthdawn.mod.kubejsdtsmaker.java;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JavaStaticMember implements IJavaMember {
    private static final Logger logger = LogManager.getLogger();

    private String name;
    private Field field;
    private List<Executable> methods;

    public JavaStaticMember(String name) {
        this.name = name;
    }


    public void addMethod(Executable method) {
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

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public List<Executable> getMethods() {
        return methods;
    }
}
