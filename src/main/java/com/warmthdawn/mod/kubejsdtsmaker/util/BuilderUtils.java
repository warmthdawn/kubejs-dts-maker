package com.warmthdawn.mod.kubejsdtsmaker.util;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.generic.TypeArguments;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.PredefinedTypes;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;

import java.util.ArrayList;
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


    public static TypeArguments createEmptyTypeArguments(int argCount) {
        if (argCount == 0) {
            return null;
        }
        ArrayList<TsType> list = new ArrayList<>(argCount);
        for (int i = 0; i < argCount; i++) {
            list.add(PredefinedTypes.ANY);
        }
        return new TypeArguments(list);
    }
}
