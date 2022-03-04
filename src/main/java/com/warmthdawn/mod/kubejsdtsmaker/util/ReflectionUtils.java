package com.warmthdawn.mod.kubejsdtsmaker.util;

import java.lang.reflect.Field;
import java.util.function.Supplier;

public class ReflectionUtils {
    public static <T> T getStaticField(Class<?> clazz, String name, Supplier<T> fallback) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            Object result = field.get(null);
            if (result != null) {
                return (T) result;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return fallback.get();
    }
}
