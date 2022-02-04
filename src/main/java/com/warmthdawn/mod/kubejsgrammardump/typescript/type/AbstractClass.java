package com.warmthdawn.mod.kubejsgrammardump.typescript.type;

import com.warmthdawn.mod.kubejsgrammardump.typescript.IClassMember;
import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.GenericVariable;
import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.GenericVariableProvider;
import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.ResolvedGenericType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.namespace.Namespace;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractClass implements IDeclaredType, GenericVariableProvider {
    private final Namespace namespace;
    private final String name;
    private List<GenericVariable> genericVariables = null;
    private Set<String> variableNames = new HashSet<>();

    @Override
    public Namespace getNamespace() {
        return namespace;
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public boolean containsVariable(String name) {
        return variableNames.contains(name);
    }

    @Override
    public List<GenericVariable> getVariables() {
        if (genericVariables == null) {
            return Collections.emptyList();
        }
        return genericVariables;
    }

    public void setGenericVariables(List<GenericVariable> genericVariables) {
        this.genericVariables = genericVariables;
        this.variableNames = genericVariables.stream().map(GenericVariable::getName).collect(Collectors.toSet());
    }

    @Override
    public IType resolve(GenericVariableProvider provider) {
        if (!isGenericClass()) {
            return this;
        }
        IType[] objects = genericVariables.stream().map(it -> it.resolve(provider)).toArray(IType[]::new);
        return new ResolvedGenericType(this, objects);
    }

    @Override
    public @Nonnull String getSignature() {
        return namespace.getName() + '.' + name;
    }

    public AbstractClass(Namespace namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    @Override
    public boolean isGenericClass() {
        return genericVariables != null && genericVariables.size() > 0;
    }



    public abstract void forEachMembers(Consumer<IClassMember> functionConsumer);

}
