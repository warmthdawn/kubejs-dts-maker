package com.warmthdawn.mod.kubejsdtsmaker.util;

import java.util.AbstractList;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public class MiscUtils {
    public static String getNamespace(Class<?> clazz) {
        if (clazz.isMemberClass()) {
            //成员类，命名空间为声明类型的全名
            return clazz.getDeclaringClass().getCanonicalName();
        } else {
            Package clazzPackage = clazz.getPackage();
            if (clazzPackage != null) {
                return clazzPackage.getName();
            }
            return "global";
        }
    }

    public static <T, R> R[] arrayMapping(T[] array, IntFunction<R[]> generator, Function<T, R> mapping) {
        R[] result = generator.apply(array.length);

        for (int i = 0; i < array.length; i++) {
            result[i] = mapping.apply(array[i]);
        }
        return result;
    }

    public static <T> boolean all(T[] array, Predicate<T> predicate) {
        for (T t : array) {
            if (!predicate.test(t)) {
                return false;
            }
        }
        return true;
    }


    public static <T1, T2> boolean all(T1[] a, T2[] b, BiPredicate<T1, T2> predicate) {
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (!predicate.test(a[i], b[i])) {
                return false;
            }
        }
        return true;
    }
}
