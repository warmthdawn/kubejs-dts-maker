package com.warmthdawn.mod.kubejsdtsmaker.resolver;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.warmthdawn.mod.kubejsdtsmaker.context.ResolveContext;
import com.warmthdawn.mod.kubejsdtsmaker.java.IJavaMember;
import com.warmthdawn.mod.kubejsdtsmaker.java.JavaInstanceMember;
import com.warmthdawn.mod.kubejsdtsmaker.java.JavaStaticMember;
import com.warmthdawn.mod.kubejsdtsmaker.java.JavaTypeInfo;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.*;
import java.util.*;

public class JavaClassResolver {

    @Nonnull
    private final ResolveContext context;

    public JavaClassResolver(@Nonnull ResolveContext context) {
        this.context = context;
    }

    public JavaTypeInfo resolve(@Nullable Class<?> clazz) {
        if (clazz == null || clazz.isArray()) {
            return null;
        }
        if (context.isResolved(clazz)) {
            return context.get(clazz);
        }
        List<Class<?>> parents = new ArrayList<>();
        if (!clazz.isInterface()) {
            Class<?> superclass = clazz.getSuperclass();
            JavaTypeInfo superClassInfo = resolve(superclass);
            if (superClassInfo != null) {
                parents.add(superclass);
            }
        }
        for (Class<?> clazzInterface : clazz.getInterfaces()) {
            JavaTypeInfo interfaceInfo = resolveInterface(clazzInterface);
            if (interfaceInfo != null) {
                parents.add(clazzInterface);
            }
        }

        return doResolveClass(clazz, parents);
    }

    private JavaTypeInfo resolveInterface(Class<?> clazz) {
        List<JavaTypeInfo> parents = new ArrayList<>();
        for (Class<?> clazzInterface : clazz.getInterfaces()) {
            JavaTypeInfo interfaceInfo = resolveInterface(clazzInterface);
            parents.add(interfaceInfo);
        }

        return doResolveInterface(clazz, parents);
    }


    private void doResolve(Class<?> clazz) {

    }

    private String remapMemberName(Member member) {
        if (member instanceof AnnotatedElement) {
            if (((AnnotatedElement) member).isAnnotationPresent(HideFromJS.class)) {
                return null;
            }
            RemapForJS remap = ((AnnotatedElement) member).getAnnotation(RemapForJS.class);
            if (remap != null) {
                return remap.value();
            }
        }
        return member.getName();
    }

    private JavaTypeInfo doResolveClass(Class<?> clazz, List<Class<?>> parents) {

        //收集成员
        Map<String, JavaInstanceMember> members = new HashMap<>();
        Map<String, JavaStaticMember> staticMembers = new HashMap<>();
        JavaInstanceMember constructorMember = null;
        Method[] methods = clazz.getDeclaredMethods();
        Field[] fields = clazz.getDeclaredFields();
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Method method : methods) {

            int modifiers = method.getModifiers();
            if (!Modifier.isPublic(modifiers)) {
                continue;
            }
            String name = remapMemberName(method);
            if (name == null) {
                continue;
            }
            IJavaMember member;
            if (Modifier.isStatic(modifiers)) {
                member = staticMembers.computeIfAbsent(name, JavaStaticMember::new);
            } else {
                member = members.computeIfAbsent(name, JavaInstanceMember::new);
            }
            member.addMethod(method);
        }
        for (Field field : fields) {
            String name = remapMemberName(field);
            JavaInstanceMember member = members.computeIfAbsent(name, JavaInstanceMember::new);
            member.addField(field);
        }

        for (Constructor<?> constructor : constructors) {
            String name = remapMemberName(constructor);
            if (name == null) {
                continue;
            }
            if (constructorMember == null) {
                constructorMember = new JavaInstanceMember(name);
            }
            constructorMember.addMethod(constructor);
        }

        //解析方法重写
        for (Map.Entry<String, JavaInstanceMember> entry : members.entrySet()) {
            String name = entry.getKey();

        }



        return null;
    }

    private JavaTypeInfo doResolveInterface(Class<?> clazz, List<JavaTypeInfo> parents) {
        return null;
    }
}
