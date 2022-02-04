package com.warmthdawn.mod.kubejsgrammardump.typescript.type;

import com.warmthdawn.mod.kubejsgrammardump.typescript.IClassMember;
import com.warmthdawn.mod.kubejsgrammardump.typescript.function.JSFunction;
import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.GenericVariable;
import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.IPartialType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.namespace.Namespace;
import com.warmthdawn.mod.kubejsgrammardump.typescript.value.Property;

import java.util.List;

public class JavaClassBuilder {
    private Namespace namespace;
    private String name;
    private List<JSFunction> functions;
    private List<Property> properties;
    private List<IClassMember> extraMembers;
    private List<IPartialType> extendFrom;
    private List<GenericVariable> genericVariables;

    public JavaClassBuilder setNamespace(Namespace namespace) {
        this.namespace = namespace;
        return this;
    }

    public JavaClassBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public JavaClassBuilder setFunctions(List<JSFunction> functions) {
        this.functions = functions;
        return this;
    }

    public JavaClassBuilder setProperties(List<Property> properties) {
        this.properties = properties;
        return this;
    }

    public JavaClassBuilder setExtraMembers(List<IClassMember> extraMembers) {
        this.extraMembers = extraMembers;
        return this;
    }

    public JavaClassBuilder setExtendFrom(List<IPartialType> extendFrom) {
        this.extendFrom = extendFrom;
        return this;
    }

    public JavaClassBuilder setGenericVariables(List<GenericVariable> genericVariables) {
        this.genericVariables = genericVariables;
        return this;
    }

    public JavaClass createJavaClass() {
        JavaClass result = new JavaClass(namespace, name, functions, properties, extraMembers, extendFrom);
        for (JSFunction function : functions) {
            function.setRelevantClass(result);
        }
        for (Property property : properties) {
            property.setRelevantClass(result);
        }
        for (IClassMember extraMember : extraMembers) {
            extraMember.setRelevantClass(result);
        }
        result.setGenericVariables(genericVariables);
        return result;
    }

}