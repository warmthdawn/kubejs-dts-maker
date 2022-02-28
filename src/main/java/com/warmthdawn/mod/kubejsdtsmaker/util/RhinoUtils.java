package com.warmthdawn.mod.kubejsdtsmaker.util;

import dev.latvian.mods.rhino.util.RemapForJS;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RhinoUtils {
    private static class BeanMethods {

        public boolean validateGetter(Method getter) {
            return valid = this.type == getter.getGenericReturnType();
        }

        public boolean validateSetter(Method setter) {
            return valid = this.type == setter.getGenericParameterTypes()[0];
        }

        public void addGetter(Method getter) {
            this.type = getter.getGenericReturnType();
            this.owner = getter.getDeclaringClass();
        }

        public void addSetter(Method setter) {
            this.hasSetter = true;
            this.type = setter.getGenericParameterTypes()[0];
            this.owner = setter.getDeclaringClass();
        }

        private boolean hasSetter;
        private Class<?> owner;
        private Type type;
        private boolean valid = true;

    }


    public static List<PropertySignature> getBeans(List<Method> methods) {
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
            if (method.getTypeParameters().length > 0) {
                continue;
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

        List<PropertySignature> res = new ArrayList<>();
        for (Map.Entry<String, BeanMethods> entry : result.entrySet()) {
            if (entry.getKey().matches("^[0-9].*")) {
                continue;
            }
            if (JSKeywords.isKeyword(entry.getKey())) {
                continue;
            }
            if (entry.getValue().valid) {
                boolean readonly = !entry.getValue().hasSetter;
                res.add(new PropertySignature(entry.getKey(), readonly, entry.getValue().type, entry.getValue().owner));
            }
        }
        return res;
    }

}
