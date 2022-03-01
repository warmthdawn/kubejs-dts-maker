package com.warmthdawn.mod.kubejsdtsmaker.java;

import com.warmthdawn.mod.kubejsdtsmaker.context.ResolveContext;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TypeReference;

import java.lang.reflect.Type;
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


    private boolean hasParents;
    private boolean hasMembers;

    public boolean hasParents() {
        return hasParents;
    }

    public boolean hasMembers() {
        return hasMembers;
    }
    public boolean isEmpty() {
        return !hasMembers && !hasParents;
    }


    public void finalizeResolve(ResolveContext context) {
        //parents
        int parentsNumber = 0;
        int membersNumber = 0;
        if (javaClazz.getSuperclass() != Object.class && context.canReference(javaClazz.getSuperclass())) {
            parentsNumber++;
        }
        for (Class<?> anInterface : javaClazz.getInterfaces()) {
            if (context.canReference(anInterface)) {
                parentsNumber++;
            }
        }
        if (members != null) {
            for (JavaInstanceMember value : members.values()) {
                if (value.getType() != 0) {
                    membersNumber++;
                }
            }
        }
        hasParents = parentsNumber != 0;
        hasMembers = membersNumber != 0;


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
                JavaInstanceMember member = superclassInfo.findMember(name);
                if (member != null) {
                    parentMember.add(member);
                } else {
                    parentMember.addAll(superclassInfo.findInheritedMembers(context, name));
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
