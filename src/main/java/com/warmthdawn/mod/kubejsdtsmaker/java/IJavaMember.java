package com.warmthdawn.mod.kubejsdtsmaker.java;

import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.util.List;

public interface IJavaMember {
    String getName();

    Field getField();

    List<Executable> getMethods();

    void addMethod(Executable method);

    void addField(Field field);
}
