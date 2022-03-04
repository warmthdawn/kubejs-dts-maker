package com.warmthdawn.mod.kubejsdtsmaker.special.generated;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.warmthdawn.mod.kubejsdtsmaker.special.ISpecialDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.function.Supplier;

public class CollectionRegistries implements ISpecialDeclaration {
    private final String identity;
    private final Supplier<? extends Collection<String>> entriesSupplier;

    public CollectionRegistries(String identity, Supplier<? extends Collection<String>> entriesSupplier) {
        this.identity = identity;
        this.entriesSupplier = entriesSupplier;
    }

    @Override
    public String getIdentity() {
        return identity;
    }


    @Override
    public IDeclaration generate() {
        return builder -> {
            builder.append("type ").append(getIdentity()).append(" = [");
            Collection<String> collection = entriesSupplier.get();
            boolean first = true;
            for (String path : collection) {
                if (!first) {
                    builder.append(", ");
                }
                builder.append("\"").append(path).append("\"");
                first = false;
            }
            builder.append("];");

        };
    }
}
