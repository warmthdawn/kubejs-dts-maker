package com.warmthdawn.mod.kubejsgrammardump.typescript.namespace;

import com.warmthdawn.mod.kubejsgrammardump.typescript.ILineBuilder;

import java.util.Objects;

public class Namespace implements ILineBuilder {
    public String getName() {
        return name;
    }

    private final String name;

    public Namespace(String packageName) {
        this.name = packageName;
    }

    public Namespace(Namespace parent, String clazzName) {
        this.name = parent.name + '.' + clazzName;
    }

    @Override
    public void generate(StringBuilder builder) {
        builder.append("declare namespace ").append(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Namespace namespace = (Namespace) o;
        return Objects.equals(name, namespace.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
