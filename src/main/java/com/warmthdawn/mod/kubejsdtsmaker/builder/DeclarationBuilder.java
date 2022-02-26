package com.warmthdawn.mod.kubejsdtsmaker.builder;

import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.ISignatureDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.util.BuilderUtils;

import java.util.List;
import java.util.function.BiConsumer;

public class DeclarationBuilder {
    private final StringBuilder builder;
    private int indent = 0;
    private String indentStr = "    ";

    public DeclarationBuilder(String indentStr) {
        this();
        this.indentStr = indentStr;
    }

    public DeclarationBuilder() {
        builder = new StringBuilder();
    }

    public DeclarationBuilder append(String content) {
        builder.append(content);
        return this;
    }

    public DeclarationBuilder append(ISignatureDeclaration content) {
        content.buildSignature(builder);
        return this;
    }

    public DeclarationBuilder append(IDeclaration content) {
        content.build(this);
        return this;
    }

    public DeclarationBuilder newLine() {
        builder.append("\n");
        for (int i = 0; i < indent; i++) {
            builder.append(indentStr);
        }
        return this;
    }

    public DeclarationBuilder increaseIndent() {
        indent++;
        return this;
    }

    public DeclarationBuilder decreaseIndent() {
        indent--;
        if (indent < 0) {
            throw new IllegalStateException("Indent can not be lower than zero");
        }
        return this;
    }

    public <T> DeclarationBuilder appendJoining(String divider, List<T> list, BiConsumer<T, DeclarationBuilder> action) {
        BuilderUtils.join(this, divider, list, action);
        return this;
    }

    public DeclarationBuilder appendJoining(String divider, List<? extends IDeclaration> list) {
        BuilderUtils.join(this, divider, list, IDeclaration::build);
        return this;
    }

    public String build() {
        return builder.toString();
    }
}
