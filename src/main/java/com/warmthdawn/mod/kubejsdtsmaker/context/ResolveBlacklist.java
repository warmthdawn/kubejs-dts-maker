package com.warmthdawn.mod.kubejsdtsmaker.context;

import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ClassFilter;
import dev.latvian.kubejs.util.KubeJSPlugins;

import java.util.HashSet;
import java.util.Set;

public class ResolveBlacklist {
    private ClassFilter classFilter;

    public ResolveBlacklist() {
        classFilter = KubeJSPlugins.createClassFilter(ScriptType.SERVER);
        blacklistPackage = new HashSet<>();
        whitelistPackage = new HashSet<>();
        blacklistClass = new HashSet<>();
        whitelistClass = new HashSet<>();
    }

    private Set<String> blacklistPackage;
    private Set<String> whitelistPackage;
    private Set<Class<?>> blacklistClass;
    private Set<Class<?>> whitelistClass;

    public ClassFilter getKubeJsClassFilter() {
        return classFilter;
    }

    public Set<String> getBlacklistPackage() {
        return blacklistPackage;
    }

    public Set<String> getWhitelistPackage() {
        return whitelistPackage;
    }

    public Set<Class<?>> getBlacklistClass() {
        return blacklistClass;
    }

    public Set<Class<?>> getWhitelistClass() {
        return whitelistClass;
    }

    public boolean isBlacklisted(Class<?> clazz) {
        if (whitelistClass.contains(clazz)) {
            return false;
        }
        if (blacklistClass.contains(clazz)) {
            return false;
        }
        if (clazz.getPackage() != null) {
            String name = clazz.getPackage().getName();

        }
        return !classFilter.isAllowed(clazz.getName());
    }
}
