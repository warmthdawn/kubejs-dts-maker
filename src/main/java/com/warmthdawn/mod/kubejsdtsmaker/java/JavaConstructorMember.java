package com.warmthdawn.mod.kubejsdtsmaker.java;

import java.lang.reflect.Constructor;
import java.util.List;

public class JavaConstructorMember {
    private List<Constructor<?>> constructors;

    public List<Constructor<?>> getConstructors() {
        return constructors;
    }

    public JavaConstructorMember(List<Constructor<?>> constructors) {
        this.constructors = constructors;
    }
}
