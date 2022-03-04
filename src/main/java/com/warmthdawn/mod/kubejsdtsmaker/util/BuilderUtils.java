package com.warmthdawn.mod.kubejsdtsmaker.util;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.generic.TypeArguments;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

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

    public static <T> TsType createStringLiterals(Supplier<T[]> literalProviders, Function<T, String> literalFunction) {
        T[] constants = literalProviders.get();
        if (constants == null || constants.length == 0) {
            throw new IllegalArgumentException("args can not be null or empty!");
        }
        if (constants.length == 1) {
            return new StringLiteral(literalFunction.apply(constants[0]));
        }
        ArrayList<StringLiteral> list = new ArrayList<>(constants.length);
        for (T arg : constants) {
            list.add(new StringLiteral(literalFunction.apply(arg)));
        }
        return new TupleType(list);
    }

    public static TsType createStringLiterals(List<String> args) {

        if (args == null || args.size() == 0) {
            throw new IllegalArgumentException("args can not be null or empty!");
        }
        if (args.size() == 1) {
            return new StringLiteral(args.get(0));
        }
        ArrayList<StringLiteral> list = new ArrayList<>(args.size());
        for (String arg : args) {
            list.add(new StringLiteral(arg));
        }
        return new TupleType(list);
    }

    public static TsType createStringLiterals(Class<?> type) {
        if (Enum.class.isAssignableFrom(type)) {
            return createStringLiterals(type::getEnumConstants, it -> ((Enum<?>) it).name());
        }
        throw new IllegalArgumentException("type can only be enum");
    }

    public static TsType createStringLiterals(Class<?> type, Function<Object, String> literalFunction) {
        if (Enum.class.isAssignableFrom(type)) {
            return createStringLiterals(type::getEnumConstants, literalFunction);
        }
        throw new IllegalArgumentException("type can only be enum");
    }

}
