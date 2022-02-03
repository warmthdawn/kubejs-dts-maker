package com.warmthdawn.mod.kubejsgrammardump.typescript.type;

import com.warmthdawn.mod.kubejsgrammardump.collector.JavaClassCollector;
import com.warmthdawn.mod.kubejsgrammardump.utils.JavaResolveUtils;

public class LazyType implements IType {
    private final Class<?> javaClass;
    private IType resolved;

    public LazyType(Class<?> javaClass) {
        this.javaClass = javaClass;
    }

    public IType resolve() {
        if (resolved == null) {

            resolved = JavaResolveUtils.resolveClass(javaClass);
        }
        return resolved;
    }

    @Override
    public String getSignature() {
        return resolve().getSignature();
    }
}
