package com.warmthdawn.mod.kubejsdtsmaker.util;

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
}
