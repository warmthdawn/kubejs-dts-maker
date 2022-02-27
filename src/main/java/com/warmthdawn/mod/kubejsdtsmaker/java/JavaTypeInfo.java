package com.warmthdawn.mod.kubejsdtsmaker.java;

import com.warmthdawn.mod.kubejsdtsmaker.context.ResolveContext;

import java.util.*;

public class JavaTypeInfo {
    private Class<?> javaClazz;
    private Map<String, JavaInstanceMember> members;
    private Map<String, JavaStaticMember> staticMembers;
    private JavaConstructorMember constructorMember;
    private Set<String> memberKeys;

    public Map<String, JavaInstanceMember> getMembers() {
        return members;
    }

    public Class<?> getJavaClazz() {
        return javaClazz;
    }

    public Map<String, JavaStaticMember> getStaticMembers() {
        return staticMembers;
    }

    public JavaConstructorMember getConstructorMember() {
        return constructorMember;
    }

    public JavaTypeInfo(Class<?> javaClazz, Map<String, JavaInstanceMember> members, Map<String, JavaStaticMember> staticMembers, JavaConstructorMember constructorMember, Set<String> memberKeys) {
        this.javaClazz = javaClazz;
        this.members = members;
        this.staticMembers = staticMembers;
        this.constructorMember = constructorMember;
        this.memberKeys = memberKeys;
        memberKeys.addAll(members.keySet());
    }

    public JavaInstanceMember findMember(String name) {
        return members.get(name);
    }


    private Map<String, List<JavaInstanceMember>> parentMembers = null;

    public Set<String> getMemberKeys() {
        return memberKeys;
    }

    public List<JavaInstanceMember> findInheritedMembers(ResolveContext context, String name) {
        if (parentMembers == null) {
            parentMembers = new HashMap<>();
        }

        if (!parentMembers.containsKey(name)) {
            Set<JavaInstanceMember> parentMember = new HashSet<>();
            JavaTypeInfo superclassInfo = context.get(javaClazz.getSuperclass());
            if (superclassInfo != null) {
                for (Map.Entry<String, JavaInstanceMember> entry : superclassInfo.members.entrySet()) {

                    JavaInstanceMember member = superclassInfo.findMember(name);
                    if (member != null) {
                        parentMember.add(member);
                    } else {
                        parentMember.addAll(superclassInfo.findInheritedMembers(context, name));
                    }
                }
            }
            for (Class<?> anInterface : javaClazz.getInterfaces()) {
                JavaTypeInfo interfaceInfo = context.get(anInterface);
                if (interfaceInfo != null) {
                    JavaInstanceMember member = interfaceInfo.findMember(name);
                    if (member != null) {
                        parentMember.add(member);
                    } else {
                        parentMember.addAll(interfaceInfo.findInheritedMembers(context, name));
                    }
                }
            }

            parentMembers.put(name, new ArrayList<>(parentMember));
        }

        return parentMembers.get(name);

    }


}
