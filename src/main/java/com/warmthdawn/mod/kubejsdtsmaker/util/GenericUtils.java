package com.warmthdawn.mod.kubejsdtsmaker.util;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.*;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class GenericUtils {
    public static void findRelativeClass(Type type, Set<Class<?>> classes) {
        findRelativeClassImpl(type, classes, true);
    }

    public static void findRelativeClassImpl(Type type, Set<Class<?>> classes, boolean includeBounds) {
        if (type instanceof Class<?>) {
            if (((Class<?>) type).isArray()) {
                classes.add(((Class<?>) type).getComponentType());
            } else {
                classes.add((Class<?>) type);
            }
        } else if (type instanceof GenericArrayType) {
            findRelativeClassImpl(((GenericArrayType) type).getGenericComponentType(), classes, includeBounds);
        } else if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            findRelativeClassImpl(rawType, classes, includeBounds);
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            for (Type actualTypeArgument : actualTypeArguments) {
                findRelativeClassImpl(actualTypeArgument, classes, includeBounds);
            }
        } else if (type instanceof TypeVariable) {
            //防止无限递归
            if (includeBounds) {
                for (Type bound : ((TypeVariable<?>) type).getBounds()) {
                    findRelativeClassImpl(bound, classes, false);
                }
            }
        } else if (type instanceof WildcardType) {
            for (Type upperBound : ((WildcardType) type).getUpperBounds()) {
                findRelativeClassImpl(upperBound, classes, includeBounds);
            }
        }
    }

    private static Type[] unrollAll(Map<TypeVariable<?>, Type> arguments, Type[] type) {
        return MiscUtils.arrayMapping(type, Type[]::new, t -> unrollTypeArguments(arguments, t));
    }

    public static Type unrollTypeArguments(Map<TypeVariable<?>, Type> arguments, Type type) {
        if (arguments == null) {
            arguments = Collections.emptyMap();
        }
        if (type instanceof TypeVariable) {
            Type result = arguments.get(type);
            if (result != null) {
                return result;
            }
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType original = (ParameterizedType) type;
            return TypeUtils.parameterize(
                (Class<?>) original.getRawType(),
                unrollAll(arguments, original.getActualTypeArguments())
            );

        }
        if (type instanceof GenericArrayType) {
            return TypeUtils.genericArrayType(unrollTypeArguments(arguments, ((GenericArrayType) type).getGenericComponentType()));
        }
        if (type instanceof WildcardType) {
            WildcardType original = (WildcardType) type;
            return TypeUtils.wildcardType()
                .withUpperBounds(unrollAll(arguments, original.getUpperBounds()))
                .withLowerBounds(unrollAll(arguments, original.getLowerBounds()))
                .build();
        }

        return type;
    }

    public static boolean isSameType(Type first, Type second) {
        return isSameTypeImpl(first, second, false);
    }

    //判断两个泛型类型是否相似，这个方法对于来自两个不同方法的泛型变量比较宽容
    private static boolean isSameTypeImpl(Type first, Type second, boolean ignoreBounds) {
        if (Objects.equals(first, second)) {
            return true;
        }
        if (first instanceof GenericArrayType && second instanceof GenericArrayType) {
            return isSameTypeImpl(((GenericArrayType) first).getGenericComponentType(), ((GenericArrayType) second).getGenericComponentType(), ignoreBounds);
        }
        if (first instanceof ParameterizedType && second instanceof ParameterizedType) {
            ParameterizedType a = (ParameterizedType) first;
            ParameterizedType b = (ParameterizedType) second;
            return isSameTypeImpl(a.getRawType(), b.getRawType(), ignoreBounds)
                && MiscUtils.all(a.getActualTypeArguments(), b.getActualTypeArguments(), (f, s) -> isSameTypeImpl(f, s, ignoreBounds));
        }
        if (first instanceof TypeVariable && second instanceof TypeVariable) {
            TypeVariable<?> a = (TypeVariable<?>) first;
            TypeVariable<?> b = (TypeVariable<?>) second;
            if (a.getGenericDeclaration() instanceof Method && b.getGenericDeclaration() instanceof Method) {
                //如果ab均为方法上面定义的泛型变量，就只需要他们俩的范围一致就行
                return MiscUtils.all(a.getBounds(), b.getBounds(), (f, s) -> isSameTypeImpl(f, s, true));
            }
            return Objects.equals(a.getGenericDeclaration(), b.getGenericDeclaration()) &&
                Objects.equals(a.getName(), b.getName());
        }
        if (first instanceof WildcardType && second instanceof WildcardType) {
            WildcardType a = (WildcardType) first;
            WildcardType b = (WildcardType) second;
            return MiscUtils.all(a.getUpperBounds(), b.getUpperBounds(), (f, s) -> isSameTypeImpl(f, s, ignoreBounds))
                && MiscUtils.all(a.getLowerBounds(), b.getLowerBounds(), (f, s) -> isSameTypeImpl(f, s, ignoreBounds));
        }

        return false;
    }


    public static boolean isAssignable(Field selfField, PropertySignature parentField, Class<?> clazz) {
        Class<?> selfFieldType = selfField.getType();
        Type parentType = TypeUtils.unrollVariables(TypeUtils.getTypeArguments(clazz, parentField.getOriginalClass()), parentField.getType());
        Class<?> parentClazz = TypeUtils.getRawType(parentType, null);

        return ClassUtils.isAssignable(selfFieldType, parentClazz);
    }

    public static boolean isAssignable(PropertySignature type, PropertySignature toType, Class<?> current) {
        return TypeUtils.isAssignable(
            TypeUtils.unrollVariables(TypeUtils.getTypeArguments(current, type.getOriginalClass()), type.getType()),
            TypeUtils.unrollVariables(TypeUtils.getTypeArguments(current, toType.getOriginalClass()), toType.getType())
        );
    }

}
