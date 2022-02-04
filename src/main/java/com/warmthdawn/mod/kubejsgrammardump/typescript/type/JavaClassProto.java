package com.warmthdawn.mod.kubejsgrammardump.typescript.type;

import com.warmthdawn.mod.kubejsgrammardump.typescript.IClassMember;
import com.warmthdawn.mod.kubejsgrammardump.typescript.function.JSConstructor;
import com.warmthdawn.mod.kubejsgrammardump.typescript.function.JSFunction;
import com.warmthdawn.mod.kubejsgrammardump.typescript.namespace.Namespace;
import com.warmthdawn.mod.kubejsgrammardump.typescript.value.Property;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public class JavaClassProto extends AbstractClass {
    private final List<JSFunction> staticFuncs;
    private final List<Property> staticFields;
    private final List<JSConstructor> constructors;
    private String actualName;

    public JavaClassProto(Namespace namespace, String name, List<JSFunction> staticFuncs, List<Property> staticFields, List<JSConstructor> constructors) {
        super(namespace, name);
        this.staticFuncs = staticFuncs;
        this.staticFields = staticFields;
        this.constructors = constructors;
    }

    public boolean hasCtors() {
        return constructors.size() > 0;
    }

    public boolean hasStaticMembers() {
        return staticFields.size() > 0 || staticFuncs.size() > 0;
    }

    public List<JSFunction> getStaticFuncs() {
        return staticFuncs;
    }

    public List<Property> getStaticFields() {
        return staticFields;
    }

    public List<JSConstructor> getConstructors() {
        return constructors;
    }

    public String getActualName() {
        return actualName;
    }

    public void adjustActualName(Set<String> currentNames) {
        actualName = getName() + "Constructor";
        for (int i = 0; currentNames.contains(actualName); i++) {
            actualName = getName() + "Constructor" + i;
        }
    }

    @Override
    @Nonnull
    public String getSignature() {
        return getNamespace().getName() + '.' + getActualName();
    }

    @Override
    public void generate(StringBuilder builder) {
        if (getNamespace() == null) {
            builder.append("declare ");
        }
        builder.append("interface ").append(getActualName());
        generateGeneric(builder);
    }

    @Override
    public void forEachMembers(Consumer<IClassMember> functionConsumer) {
        Set<String> usedProps = new HashSet<>();
        constructors.forEach(functionConsumer);
        staticFuncs.forEach(it -> {
            usedProps.add(it.getName());
            functionConsumer.accept(it);
        });
        staticFields.forEach(it -> {
            if (!usedProps.contains(it.getName())) {
                functionConsumer.accept(it);
            }
        });
    }
}
