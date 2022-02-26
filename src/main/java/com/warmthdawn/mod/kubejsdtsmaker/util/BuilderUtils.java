package com.warmthdawn.mod.kubejsdtsmaker.util;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BuilderUtils {
    public static <T> void join(StringBuilder builder, String divider, List<T> list, BiConsumer<T, StringBuilder> action) {
        for (int i = 0; i < list.size(); i++) {
            action.accept(list.get(i), builder);
            if (i != list.size() - 1) {
                builder.append(divider);
            }
        }
    }

    public static <T> void join(DeclarationBuilder builder, String divider, List<T> list, BiConsumer<T, DeclarationBuilder> action) {
        for (int i = 0; i < list.size(); i++) {
            action.accept(list.get(i), builder);
            if (i != list.size() - 1) {
                builder.append(divider);
            }
        }
    }
}
