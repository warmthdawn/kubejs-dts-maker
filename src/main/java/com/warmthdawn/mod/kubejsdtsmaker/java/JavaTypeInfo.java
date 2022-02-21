package com.warmthdawn.mod.kubejsdtsmaker.java;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class JavaTypeInfo {
    private Class<?> javaClazz;
    private Type superClazz;
    private List<Type> interfaces;
    private Map<String, JavaInstanceMember> members;
    private Map<String, JavaInstanceMember> staticMembers;
    private Map<String, JavaTypeInfo> innerType;

    public JavaTypeInfo(Class<?> javaClazz, Type superClazz, List<Type> interfaces, Map<String, JavaInstanceMember> members, Map<String, JavaInstanceMember> staticMembers, Map<String, JavaTypeInfo> innerType) {
        this.javaClazz = javaClazz;
        this.superClazz = superClazz;
        this.interfaces = interfaces;
        this.members = members;
        this.staticMembers = staticMembers;
        this.innerType = innerType;
    }


    public JavaInstanceMember findMember(String name) {
        return members.get(name);
    }


}
