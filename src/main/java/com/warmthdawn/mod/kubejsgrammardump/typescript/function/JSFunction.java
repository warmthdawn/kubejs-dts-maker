package com.warmthdawn.mod.kubejsgrammardump.typescript.function;

import com.google.common.collect.ImmutableList;
import com.warmthdawn.mod.kubejsgrammardump.typescript.IClassMember;
import com.warmthdawn.mod.kubejsgrammardump.typescript.ILineBuilder;
import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.GenericVariable;
import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.GenericVariableProvider;
import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.ResolvedGenericType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.namespace.Namespace;
import com.warmthdawn.mod.kubejsgrammardump.typescript.primitives.TSPrimitive;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.AbstractClass;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class JSFunction implements IClassMember, ILineBuilder, GenericVariableProvider {
    public JSFunction(String name, IType returnType, FunctionParameter[] parameters) {
        this.name = name;
        this.returnType = returnType;
        this.parameters = parameters;
    }

    private final String name;
    protected final IType returnType;
    protected final FunctionParameter[] parameters;
    private List<GenericVariable> genericVariables = null;
    private Set<String> variableNames = new HashSet<>();


    private AbstractClass relevantClass;

    @Override
    @Nullable
    public AbstractClass getRelevantClass() {
        return relevantClass;
    }

    @Override
    public void setRelevantClass(AbstractClass relevantClass) {
        this.relevantClass = relevantClass;
    }

    @Override
    public boolean containsVariable(String name) {
        return variableNames.contains(name) || (getRelevantClass() != null && getRelevantClass().containsVariable(name));
    }

    @Override
    public List<GenericVariable> getVariables() {
        if (genericVariables != null) {
            return genericVariables;
        }
        return Collections.emptyList();
    }

    public void setGenericVariables(List<GenericVariable> genericVariables) {
        this.genericVariables = genericVariables;
        this.variableNames = genericVariables.stream().map(GenericVariable::getName).collect(Collectors.toSet());
    }

    protected void generateGeneric(StringBuilder builder) {
        List<GenericVariable> variables = getVariables();
        if (variables.isEmpty()) {
            return;
        }
        int size = variables.size();
        for (int i = 0; i < size; i++) {
            if (i == 0) {
                builder.append("<");
            }
            GenericVariable variable = variables.get(i);
            variable.appendTo(builder, this);
            if (i == size - 1) {
                builder.append(">");
            } else {
                builder.append(", ");
            }
        }
    }

    @Override
    public void generate(StringBuilder builder) {
        builder.append(name);
        generateGeneric(builder);
        builder.append("(");
        for (int i = 0; i < parameters.length; i++) {
            parameters[i].appendTo(builder, this);
            if (i != parameters.length - 1) {
                builder.append(", ");
            }
        }
        builder.append("): ");
        String signature = returnType.resolve(this).getSignature();

        builder.append(signature);
        builder.append(";");

    }

    @Override
    public String getName() {
        return name;
    }
}
