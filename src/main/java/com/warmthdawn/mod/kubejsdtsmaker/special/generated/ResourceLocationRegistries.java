package com.warmthdawn.mod.kubejsdtsmaker.special.generated;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.warmthdawn.mod.kubejsdtsmaker.special.ISpecialDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.function.Supplier;

public class ResourceLocationRegistries implements ISpecialDeclaration {
    private final String identity;
    private final Supplier<? extends Collection<ResourceLocation>> entriesSupplier;

    public ResourceLocationRegistries(String identity, Supplier<? extends Collection<ResourceLocation>> entriesSupplier) {
        this.identity = identity;
        this.entriesSupplier = entriesSupplier;
    }

    @Override
    public String getIdentity() {
        return identity;
    }


    @Override
    public IDeclaration generate() {
        Multimap<String, String> mappings = HashMultimap.create();

        for (ResourceLocation key : entriesSupplier.get()) {
            mappings.put(key.getNamespace(), key.getPath());
        }

        return builder -> {
            builder.append("type ").append(getIdentity()).append(" = {")
                .increaseIndent();

            for (String key : mappings.keySet()) {
                Collection<String> paths = mappings.get(key);
                builder.newLine().append("\"").append(key).append("\": [");
                boolean first = true;
                for (String path : paths) {
                    if (!first) {
                        builder.append(", ");
                    }
                    builder.append("\"").append(path).append("\"");
                    first = false;
                }
                builder.append("], ");
            }

            builder.decreaseIndent()
                .newLine()
                .append("}");

        };
    }
}
