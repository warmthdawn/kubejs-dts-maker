package com.warmthdawn.mod.kubejsgrammardump.typescript.namespace;

import com.warmthdawn.mod.kubejsgrammardump.typescript.ILineBuilder;

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
}
