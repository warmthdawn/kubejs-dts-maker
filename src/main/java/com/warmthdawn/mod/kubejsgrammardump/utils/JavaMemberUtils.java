package com.warmthdawn.mod.kubejsgrammardump.utils;

import com.warmthdawn.mod.kubejsgrammardump.typescript.function.FunctionParameter;
import com.warmthdawn.mod.kubejsgrammardump.typescript.function.JSConstructor;
import com.warmthdawn.mod.kubejsgrammardump.typescript.function.JSFunction;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.value.Property;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;

import java.lang.reflect.*;
import java.util.*;

public class JavaMemberUtils {
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
            res.add(new JSFunction(name, result, getParameters(parameters)));
        }

        return res;
    }

    private static FunctionParameter[] getParameters(Parameter[] parameters) {
        FunctionParameter[] params = new FunctionParameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            IType type = Utils.getClassType(parameter.getType());
            params[i] = new FunctionParameter(type, parameter.getName(), parameter.isVarArgs());
        }
        return params;
    }

    public static List<JSConstructor> getCtors(List<Constructor<?>> methods) {
        List<JSConstructor> res = new ArrayList<>();

        for (Constructor<?> ctor : methods) {
            IType owner = Utils.getClassType(ctor.getDeclaringClass());
            Parameter[] parameters = ctor.getParameters();
            res.add(new JSConstructor(owner,  getParameters(parameters)));
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
        for (Constructor<?> c : clazz.getConstructors()) {
            if (!c.isAnnotationPresent(HideFromJS.class)) {
                constructorsList.add(c);
            }
        }
        return constructorsList;
    }

    public static List<Field> getFields(Class<?> clazz, boolean isStatic) {
        List<Field> result = new ArrayList<>();
        for (Field f : clazz.getFields()) {
            if (!f.isAnnotationPresent(HideFromJS.class) && (isStatic == Modifier.isStatic(f.getModifiers()))) {
                result.add(f);
            }
        }
        return result;
    }

    public static List<Method> getMethods(Class<?> clazz, boolean isStatic) {
        List<Method> result = new ArrayList<>();
        for (Method m : clazz.getMethods()) {
            if (!m.isAnnotationPresent(HideFromJS.class) && (isStatic == Modifier.isStatic(m.getModifiers()))) {
                result.add(m);
            }
        }
        return result;
    }
}
