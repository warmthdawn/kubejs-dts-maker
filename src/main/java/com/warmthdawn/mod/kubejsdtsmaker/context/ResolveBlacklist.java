package com.warmthdawn.mod.kubejsdtsmaker.context;

public class ResolveBlacklist {
    public boolean isBlacklisted(Class<?> clazz) {
        if (clazz.getPackage() != null && clazz.getPackage().getName().startsWith("dev.latvian.mods.rhino")) {
            return true;
        }
        return false;
    }
}
