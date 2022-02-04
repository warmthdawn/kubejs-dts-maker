package com.warmthdawn.mod.kubejsgrammardump.typescript.type;

import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.GenericVariableProvider;
import com.warmthdawn.mod.kubejsgrammardump.utils.JavaResolveUtils;

import javax.annotation.Nonnull;

public class LazyType implements IType {
    private final Class<?> javaClass;
    private IType resolved;

    public LazyType(Class<?> javaClass) {
        this.javaClass = javaClass;
    }

    @Override
    public IType resolve(GenericVariableProvider provider) {
        return resolve().resolve(provider);
    }

    public IType resolve() {
        if (resolved == null) {

            resolved = JavaResolveUtils.resolveClass(javaClass);
        }
        return resolved;
    }

    public static <T extends IType> boolean isInstance(Class<?> cls, T obj) {
        return cls.isInstance(obj) || (obj instanceof LazyType && cls.isInstance(((LazyType) obj).resolve()));
    }

    public static <T extends IType> T cast(Class<T> cls, IType obj) {
        //noinspection unchecked
        return obj instanceof LazyType ? (T) ((LazyType) obj).resolve() : (T) obj;
    }

    @Override
    public @Nonnull String getSignature() {
        return resolve().getSignature();
    }
}
