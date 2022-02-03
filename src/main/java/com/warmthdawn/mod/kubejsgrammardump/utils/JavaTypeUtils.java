package com.warmthdawn.mod.kubejsgrammardump.utils;

import com.warmthdawn.mod.kubejsgrammardump.typescript.IClassMember;
import com.warmthdawn.mod.kubejsgrammardump.typescript.function.JSConstructor;
import com.warmthdawn.mod.kubejsgrammardump.typescript.function.JSFunction;
import com.warmthdawn.mod.kubejsgrammardump.typescript.namespace.Namespace;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.JavaClass;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.JavaClassBuilder;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.JavaClassProto;
import com.warmthdawn.mod.kubejsgrammardump.typescript.value.Property;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JavaTypeUtils {
    private static List<Property> combine(List<Property> jsFields, List<Property> beans) {
        HashMap<String, Property> result = new HashMap<>();
        for (Property bean : beans) {
            result.put(bean.getName(), bean);
        }
        for (Property jsField : jsFields) {
            result.put(jsField.getName(), jsField);
        }
        return new ArrayList<>(result.values());
    }

    public static JavaClass resolveClass(Class<?> clazz, List<JavaClass> extendFrom, Namespace namespace) {

        List<Method> methods = JavaMemberUtils.getMethods(clazz, false);
        List<JSFunction> functions = JavaMemberUtils.getMethods(methods);
        List<Field> fields = JavaMemberUtils.getFields(clazz, false);
        List<Property> jsFields = JavaMemberUtils.getFields(fields);
        List<Property> beans = JavaMemberUtils.getBeans(methods);
        List<Property> properties = combine(jsFields, beans);
        List<IClassMember> extraMembers = JavaResolveUtils.resolveExtraMembers(clazz, false);
        return new JavaClassBuilder().setNamespace(namespace).setName(clazz.getSimpleName()).setFunctions(functions).setProperties(properties).setExtraMembers(extraMembers).setExtendFrom(extendFrom).createJavaClass();
    }

    public static JavaClassProto resolveProto(Class<?> clazz, Namespace namespace) {
        List<Method> methods = JavaMemberUtils.getMethods(clazz, true);
        List<JSFunction> functions = JavaMemberUtils.getMethods(methods);
        List<Constructor<?>> constructors = JavaMemberUtils.getConstructors(clazz);
        List<JSConstructor> ctors = JavaMemberUtils.getCtors(constructors);
        List<Field> fields = JavaMemberUtils.getFields(clazz, true);
        List<Property> jsFields = JavaMemberUtils.getFields(fields);
        List<Property> beans = JavaMemberUtils.getBeans(methods);
        List<Property> properties = combine(jsFields, beans);
        return new JavaClassProto(namespace, clazz.getSimpleName(), functions, properties, ctors);
    }
}
