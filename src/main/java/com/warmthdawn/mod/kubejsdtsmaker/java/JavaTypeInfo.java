package com.warmthdawn.mod.kubejsdtsmaker.java;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.warmthdawn.mod.kubejsdtsmaker.context.ResolveContext;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class JavaTypeInfo {
    private Class<?> javaClazz;
    private Map<String, JavaInstanceMember> members;
    private Map<String, JavaStaticMember> staticMembers;
    private JavaConstructorMember constructorMember;


    public JavaTypeInfo(Class<?> javaClazz, Map<String, JavaInstanceMember> members, Map<String, JavaStaticMember> staticMembers, JavaConstructorMember constructorMember) {
        this.javaClazz = javaClazz;
        this.members = members;
        this.staticMembers = staticMembers;
        this.constructorMember = constructorMember;
    }

    public JavaInstanceMember findMember(String name) {
        return members.get(name);
    }


    private Multimap<String, JavaInstanceMember> parentMembers = null;

    public Collection<JavaInstanceMember> findInheritedMembers(ResolveContext context, String name) {
        if (parentMembers == null) {
            parentMembers = HashMultimap.create();
            JavaTypeInfo superclassInfo = context.get(javaClazz.getSuperclass());
            if (superclassInfo != null) {
                JavaInstanceMember member = superclassInfo.findMember(name);
                if (member != null) {
                    parentMembers.put(name, member);
                } else {
                    parentMembers.putAll(name, superclassInfo.findInheritedMembers(context, name));
                }
            }
            for (Class<?> anInterface : javaClazz.getInterfaces()) {
                JavaTypeInfo interfaceInfo = context.get(anInterface);
                if (interfaceInfo != null) {
                    JavaInstanceMember member = interfaceInfo.findMember(name);
                    if (member != null) {
                        parentMembers.put(name, member);
                    } else {
                        parentMembers.putAll(name, interfaceInfo.findInheritedMembers(context, name));
                    }
                }
            }
        }
        Collection<JavaInstanceMember> result = parentMembers.get(name);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;

    }


}
