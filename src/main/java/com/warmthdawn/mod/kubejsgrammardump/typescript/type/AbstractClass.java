package com.warmthdawn.mod.kubejsgrammardump.typescript.type;

import com.warmthdawn.mod.kubejsgrammardump.typescript.IClassMember;
import com.warmthdawn.mod.kubejsgrammardump.typescript.ILineBuilder;
import com.warmthdawn.mod.kubejsgrammardump.typescript.namespace.Namespace;

import java.util.function.Consumer;

public abstract class AbstractClass implements IType, ILineBuilder {
    private Namespace namespace;
    private String name;

    public Namespace getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }


    @Override
    public String getSignature() {
        return namespace.getName() + '.' + name;
    }

    public AbstractClass(Namespace namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    public abstract void forEachMembers(Consumer<IClassMember> functionConsumer);

}
