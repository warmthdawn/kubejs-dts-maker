package com.warmthdawn.mod.kubejsdtsmaker.util;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.generic.TypeArguments;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.PredefinedType;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

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
            list.add(PredefinedType.ANY);
        }
        return new TypeArguments(list);
    }

    public static TypeReference createTypeReference(BuildContext context, Class<?> clazz) {
        TypeArguments typeArguments = BuilderUtils.createEmptyTypeArguments(clazz.getTypeParameters().length);
        String namespace = context.getNamespace(clazz);
        return new TypeReference(typeArguments, namespace, clazz.getSimpleName());
    }

}
