package com.warmthdawn.mod.kubejsgrammardump.typescript.primitives;

import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.GenericVariable;
import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.IPartialType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.namespace.Namespace;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TSUnionType implements IDeclaredType {
    private final List<IType> parents;
    private final String name;

    private final List<GenericVariable> genericVariables;
    private final Set<String> variableNames;

    public TSUnionType(List<IType> parents, String name, Namespace namespace, List<GenericVariable> genericVariables) {
        this.parents = parents;
        this.name = name;
        this.namespace = namespace;
        this.genericVariables = genericVariables;
        this.variableNames = genericVariables.stream().map(GenericVariable::getName).collect(Collectors.toSet());
    }

    public static IType create(JavaClass javaClass) {
        List<IType> result = new ArrayList<>();
        for (IPartialType superType : javaClass.getParents()) {
            IType resolve = superType.resolve(null);
            if (LazyType.isInstance(AbstractClass.class, resolve)) {
                result.add(LazyType.cast(AbstractClass.class, resolve));
            }
        }
        if (result.size() == 1) {
            return result.get(0);
        }
        if (result.isEmpty()) {
            return EmptyClass.INSTANCE;
        }
        return new TSUnionType(result, javaClass.getName(), javaClass.getNamespace(), javaClass.getVariables());
    }


    @Override
    @Nonnull
    public String getSignature() {
        return getNamespace().getName() + "." + name;
    }

    @Override
    public void generate(StringBuilder builder) {
        builder.append("type ").append(name);
        generateGeneric(builder);
        builder.append(" = ");
        for (int i = 0; i < parents.size(); i++) {
            builder.append(parents.get(i).resolve(this).getSignature());
            if (i != parents.size() - 1) {
                builder.append(" & ");
            }
        }
    }

    private final Namespace namespace;

    @Override
    public Namespace getNamespace() {
        return namespace;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isGenericClass() {
        return genericVariables != null && genericVariables.size() > 0;
    }

    @Override
    public List<GenericVariable> getVariables() {
        return genericVariables;
    }

    @Override
    public boolean containsVariable(String name) {
        return variableNames.contains(name);
    }
}
