package com.warmthdawn.mod.kubejsgrammardump.typescript.type;

import com.warmthdawn.mod.kubejsgrammardump.typescript.IClassMember;
import com.warmthdawn.mod.kubejsgrammardump.typescript.function.JSFunction;
import com.warmthdawn.mod.kubejsgrammardump.typescript.namespace.Namespace;
import com.warmthdawn.mod.kubejsgrammardump.typescript.value.Property;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class JavaClass extends AbstractClass {
    private final List<JSFunction> functions;
    private final List<Property> properties;
    private final List<IClassMember> extraMembers;
    private final List<JavaClass> extendFrom;
    private JavaClassProto proto;

    public JavaClass(Namespace namespace, String name, List<JSFunction> functions, List<Property> properties, List<IClassMember> extraMembers, List<JavaClass> extendFrom) {
        super(namespace, name);
        this.functions = functions;
        this.properties = properties;
        this.extraMembers = extraMembers;
        this.extendFrom = extendFrom;
    }

    public void setProto(JavaClassProto constructor) {
        this.proto = constructor;
    }

    @Override
    public void generate(StringBuilder builder) {
        builder.append("interface ").append(getName());
        if (proto != null) {
            builder.append(" extends Omit<").append(proto.getSignature()).append(", 'new'>");
            if (extendFrom.size() > 0) {
                builder.append(", ");
            }
        } else if (extendFrom.size() > 0) {
            builder.append(" extends ");
        }
        for (int i = 0; i < extendFrom.size(); i++) {
            JavaClass superType = extendFrom.get(i);
            builder.append(superType.getSignature());
            if (i != extendFrom.size() - 1) {
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
