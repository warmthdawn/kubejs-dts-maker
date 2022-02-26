package com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.generic.TypeParameters;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.member.Member;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.NamedType;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TypeReference;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class InterfaceDeclaration implements NamedType {
    private List<Member> members;
    private String identity;
    private TypeParameters typeParameters;
    private List<TsType> parents;

    public InterfaceDeclaration(String identity, List<Member> members, TypeParameters typeParameters, List<? extends TsType> parents) {
        this.members = members;
        this.identity = identity;
        this.typeParameters = typeParameters;
        if (parents == null) {
            this.parents = Collections.emptyList();
        } else {
            this.parents = Collections.unmodifiableList(parents);
        }
    }

    @Override
    public void build(DeclarationBuilder builder) {
        builder.append("interface ")
            .append(identity);
        if (typeParameters != null) {
            builder.append(typeParameters);
        }

        if (!parents.isEmpty()) {
            builder.append(" extends ")
                .appendJoining(", ", parents, (type, b) -> b.append(type));
        }

        builder.append(" {")
            .increaseIndent();

        for (int i = 0; i < members.size(); i++) {
            if (i != 0) {
                //extra line between members
                builder.newLine();
            }
            builder.append(members.get(i));
        }

        builder.decreaseIndent()
            .newLine()
            .append("}");

    }

    public List<Member> getMembers() {
        return members;
    }

    @Nonnull
    @Override
    public String getIdentity() {
        return identity;
    }


    @Override
    public String toString() {
        return "interface " + identity;
    }
}
