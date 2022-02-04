package com.warmthdawn.mod.kubejsgrammardump.typescript.type;

import com.warmthdawn.mod.kubejsgrammardump.typescript.IClassMember;
import com.warmthdawn.mod.kubejsgrammardump.typescript.function.JSFunction;
import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.IPartialType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.namespace.Namespace;
import com.warmthdawn.mod.kubejsgrammardump.typescript.value.Property;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class JavaClass extends AbstractClass {
    private final List<JSFunction> functions;
    private final List<Property> properties;
    private final List<IClassMember> extraMembers;
    private final List<IPartialType> parents;
    private JavaClassProto proto;

    public JavaClass(Namespace namespace, String name, List<JSFunction> functions, List<Property> properties, List<IClassMember> extraMembers, List<IPartialType> extendFrom) {
        super(namespace, name);
        this.functions = functions;
        this.properties = properties;
        this.extraMembers = extraMembers;
        this.parents = extendFrom;
    }

    public void setProto(JavaClassProto constructor) {
        this.proto = constructor;
    }

    public boolean isEmpty() {
        return functions.isEmpty() && properties.isEmpty() && extraMembers.isEmpty();
    }

    public List<IPartialType> getParents() {
        return parents;
    }

    @Override
    public void generate(StringBuilder builder) {
        builder.append("interface ").append(getName());
        generateGeneric(builder);
        List<IDeclaredType> actualSuper = new ArrayList<>();
        for (IPartialType superType : parents) {
            IType resolve = superType.resolve(this);
            if (LazyType.isInstance(IDeclaredType.class, resolve)) {
                actualSuper.add(LazyType.cast(IDeclaredType.class, resolve));
            }
        }
        boolean hasExtends = false;
        if (proto != null) {
            if (proto.hasCtors() && proto.hasStaticMembers()) {
                builder.append(" extends Omit<").append(proto.getSignature()).append(", 'new'>");
                hasExtends = true;
            } else if (proto.hasStaticMembers()) {
                builder.append(" extends ").append(proto.getSignature());
                hasExtends = true;
            }

        }
        if (actualSuper.size() > 0) {
            if (hasExtends) {
                builder.append(", ");
            } else {
                builder.append(" extends ");
            }
        }
        for (int i = 0; i < actualSuper.size(); i++) {
            builder.append(actualSuper.get(i).getSignature());
            if (i != actualSuper.size() - 1) {
                builder.append(", ");
            }
        }
    }

    @Override
    public void forEachMembers(Consumer<IClassMember> functionConsumer) {
        Set<String> usedProps = new HashSet<>();
        functions.forEach(it -> {
            usedProps.add(it.getName());
            functionConsumer.accept(it);
        });
        properties.forEach(it -> {
            if (!usedProps.contains(it.getName())) {
                functionConsumer.accept(it);
            }
        });
        extraMembers.forEach(functionConsumer);
    }
}
