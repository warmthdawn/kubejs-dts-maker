package com.warmthdawn.mod.kubejsgrammardump.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.warmthdawn.mod.kubejsgrammardump.typescript.function.FunctionParameter;
import com.warmthdawn.mod.kubejsgrammardump.typescript.function.JSConstructor;
import com.warmthdawn.mod.kubejsgrammardump.typescript.function.JSFunction;
import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.GenericVariable;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.JavaClass;
import com.warmthdawn.mod.kubejsgrammardump.typescript.value.Property;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class JavaMemberUtils {

    public static List<GenericVariable> getGenericVariables(GenericDeclaration declaration) {
        return Arrays.stream(declaration.getTypeParameters()).map(it -> {
            Type[] bounds = it.getBounds();
            IType[] boundsResult = new IType[bounds.length];
            for (int i = 0; i < bounds.length; i++) {
                boundsResult[i] = Utils.getClassGenericType(bounds[i]);
            }
            return new GenericVariable(boundsResult, it.getName());
        }).collect(Collectors.toList());
    }

    public static List<JSFunction> getMethods(List<Method> methods) {
        List<JSFunction> res = new ArrayList<>();

        for (Method method : methods) {
            String name = method.getName();
            RemapForJS remap = method.getAnnotation(RemapForJS.class);
            if (remap != null) {
                name = remap.value();
            }
            IType result = Utils.getClassType(method.getReturnType());
            Parameter[] parameters = method.getParameters();
            JSFunction jsFunction = new JSFunction(name, result, getParameters(parameters));
            jsFunction.setGenericVariables(getGenericVariables(method));
            res.add(jsFunction);
        }

        return res;
    }

    private static FunctionParameter[] getParameters(Parameter[] parameters) {
        FunctionParameter[] params = new FunctionParameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            IType type = Utils.getClassGenericType(parameter.getParameterizedType());
            params[i] = new FunctionParameter(type, parameter.getName(), parameter.isVarArgs());
        }
        return params;
    }

    public static List<JSConstructor> getCtors(List<Constructor<?>> constructors, JavaClass relevantClass) {
        List<JSConstructor> res = new ArrayList<>();

        for (Constructor<?> ctor : constructors) {
            IType owner = Utils.getClassType(ctor.getDeclaringClass());
            Parameter[] parameters = ctor.getParameters();
            JSConstructor jsConstructor = new JSConstructor(owner, getParameters(parameters));
            List<GenericVariable> genericVariables = getGenericVariables(ctor);
            genericVariables.addAll(relevantClass.getVariables());
            if(genericVariables.size() > 0) {
                jsConstructor.setGenericVariables(genericVariables);
            }
            jsConstructor.setRelevantClass(relevantClass);
            res.add(jsConstructor);
        }

        return res;
    }

    public static List<Property> getFields(List<Field> fields) {
        List<Property> res = new ArrayList<>();

        for (Field field : fields) {
            String name = field.getName();
            RemapForJS remap = field.getAnnotation(RemapForJS.class);

            if (remap != null) {
                name = remap.value();
            }

            boolean readonly = Modifier.isFinal(field.getModifiers());
            IType type = Utils.getClassType(field.getType());
            res.add(new Property(name, type, readonly));
        }


        return res;
    }

    private static class BeanMethods {

        public boolean validateGetter(Method getter) {
            return valid = this.type == getter.getReturnType();
        }

        public boolean validateSetter(Method setter) {
            return valid = this.type == setter.getParameterTypes()[0];
        }

        public void addGetter(Method getter) {
            this.type = getter.getReturnType();
        }

        public void addSetter(Method setter) {
            this.hasSetter = true;
            this.type = setter.getParameterTypes()[0];
        }

        private boolean hasSetter;
        private Class<?> type;
        private boolean valid = true;

    }

    private static final Logger logger = LogManager.getLogger();

    private static void logError(String msg, Class<?> clazz, Throwable throwable) {
        if (throwable instanceof NoClassDefFoundError) {
            logger.warn(msg + ": Failed to load class: ", clazz);
        }
        try {
            logger.warn(msg + ": Failed to load class: {}", clazz.getSimpleName());
        } catch (Exception ignored) {

        }
    }


    public static List<Property> getBeans(List<Method> methods) {
        // Create bean properties from corresponding get/set methods first for
        // static members and then for instance members

        Map<String, BeanMethods> result = new HashMap<>();

        // Now, For each member, make "bean" properties.
        for (Method method : methods) {
            String name = method.getName();
            RemapForJS remap = method.getAnnotation(RemapForJS.class);

            if (remap != null) {
                name = remap.value();
            }

            // Is this a getter?
            boolean memberIsGetMethod = name.startsWith("get");
            boolean memberIsSetMethod = name.startsWith("set");
            boolean memberIsIsMethod = name.startsWith("is");
            if (memberIsGetMethod || memberIsIsMethod || memberIsSetMethod) {
                int parameterLength = method.getParameterCount();
                if (memberIsSetMethod) {
                    if (parameterLength != 1) {
                        continue;
                    }
                } else if (parameterLength != 0) {
                    continue;
                }
                // Double check name component.
                String nameComponent = name.substring(memberIsIsMethod ? 2 : 3);
                if (nameComponent.length() == 0) {
                    continue;
                }

                // Make the bean property name.
                String beanPropertyName = nameComponent;
                char ch0 = nameComponent.charAt(0);
                if (Character.isUpperCase(ch0)) {
                    if (nameComponent.length() == 1) {
                        beanPropertyName = nameComponent.toLowerCase();
                    } else {
                        char ch1 = nameComponent.charAt(1);
                        if (!Character.isUpperCase(ch1)) {
                            beanPropertyName = Character.toLowerCase(ch0) + nameComponent.substring(1);
                        }
                    }
                }

                BeanMethods beanMethods = result.get(beanPropertyName);
                //没有，添加
                if (beanMethods == null) {
                    beanMethods = new BeanMethods();
                    if (memberIsSetMethod) {
                        beanMethods.addSetter(method);
                    } else {
                        beanMethods.addGetter(method);
                    }
                    result.put(beanPropertyName, beanMethods);
                    continue;
                }

                //不合法，跳过
                if (!beanMethods.valid) {
                    continue;
                }

                if (memberIsSetMethod) {
                    if (!beanMethods.validateSetter(method)) {
                        continue;
                    }
                    beanMethods.addSetter(method);
                } else {
                    if (!beanMethods.validateGetter(method)) {
                        continue;
                    }
                    beanMethods.addGetter(method);
                }
            }
        }

        List<Property> res = new ArrayList<>();
        for (Map.Entry<String, BeanMethods> entry : result.entrySet()) {
            if (entry.getValue().valid) {
                boolean readonly = !entry.getValue().hasSetter;
                IType type = Utils.getClassType(entry.getValue().type);
                res.add(new Property(entry.getKey(), type, readonly));
            }
        }
        return res;
    }

    public static List<Constructor<?>> getConstructors(Class<?> clazz) {
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            return Collections.emptyList();
        }

        List<Constructor<?>> constructorsList = new ArrayList<>();

        try {
            for (Constructor<?> c : clazz.getConstructors()) {
                if (!c.isAnnotationPresent(HideFromJS.class)) {
                    constructorsList.add(c);
                }
            }
        } catch (Throwable e) {
            logError("getConstructors", clazz, e);
        }
        return constructorsList;
    }

    public static List<Field> getFields(Class<?> clazz, boolean isStatic) {
        List<Field> result = new ArrayList<>();
        try {
            for (Field f : clazz.getDeclaredFields()) {
                int modifiers = f.getModifiers();
                if (!f.isAnnotationPresent(HideFromJS.class) && Modifier.isPublic(modifiers) && (isStatic == Modifier.isStatic(modifiers))) {
                    result.add(f);
                }
            }
        } catch (Throwable e) {
            logError("getFields", clazz, e);
        }
        return result;
    }

    public static List<Method> getMethods(Class<?> clazz, boolean isStatic) {
        List<Method> result = new ArrayList<>();
        try {
            Multimap<String, Class<?>[]> superMethods = HashMultimap.create();
            if (!clazz.isInterface()) {
                Class<?> superclass = clazz.getSuperclass();
                if (superclass != null) {
                    for (Method method : superclass.getMethods()) {
                        superMethods.put(method.getName(), method.getParameterTypes());
                    }
                    return result;
                }
            }
            for (Class<?> superInterface : clazz.getInterfaces()) {
                for (Method method : superInterface.getMethods()) {
                    superMethods.put(method.getName(), method.getParameterTypes());
                }
                return result;
            }

            loopMethods:
            for (Method m : clazz.getDeclaredMethods()) {
                int modifiers = m.getModifiers();
                if (!m.isAnnotationPresent(HideFromJS.class) && Modifier.isPublic(modifiers) && (isStatic == Modifier.isStatic(modifiers))) {
                    //去掉实现方法
                    Collection<Class<?>[]> params = superMethods.get(m.getName());
                    if (params != null) {
                        Class<?>[] types = m.getParameterTypes();
                        for (Class<?>[] superTypes : params) {
                            if (Arrays.equals(superTypes, types)) {
                                continue loopMethods;
                            }
                        }
                    }
                    result.add(m);
                }
            }
            return result;

        } catch (Throwable e) {
            logError("getMethods", clazz, e);
            return Collections.emptyList();
        }
    }
}
