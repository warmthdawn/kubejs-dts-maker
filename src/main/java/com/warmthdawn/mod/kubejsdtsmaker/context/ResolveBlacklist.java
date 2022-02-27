package com.warmthdawn.mod.kubejsdtsmaker.context;

public class ResolveBlacklist {
    public boolean isBlacklisted(Class<?> clazz) {
        if (clazz.getPackage() != null) {
            String name = clazz.getPackage().getName();
            if (name.startsWith("dev.latvian.mods.rhino"))
                return true;
//            if (name.startsWith("it.unimi.dsi.fastutil"))
//                return true;
        }
        return false;
    }
}
