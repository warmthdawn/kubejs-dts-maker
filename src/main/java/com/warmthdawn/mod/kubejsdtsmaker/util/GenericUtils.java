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
        if (Objects.equals(first, second)) {
            return true;
        }
        if (first instanceof GenericArrayType && second instanceof GenericArrayType) {
            return isSameType(((GenericArrayType) first).getGenericComponentType(), ((GenericArrayType) second).getGenericComponentType());
        }
        if (first instanceof ParameterizedType && second instanceof ParameterizedType) {
            ParameterizedType a = (ParameterizedType) first;
            ParameterizedType b = (ParameterizedType) second;
            return isSameType(a.getRawType(), b.getRawType())
                && MiscUtils.all(a.getActualTypeArguments(), b.getActualTypeArguments(), GenericUtils::isSameType);
        }
        if (first instanceof TypeVariable && second instanceof TypeVariable) {
            TypeVariable<?> a = (TypeVariable<?>) first;
            TypeVariable<?> b = (TypeVariable<?>) second;
            if (a.getGenericDeclaration() instanceof Method && b.getGenericDeclaration() instanceof Method) {
                //方法的就暂时忽略b 
                return Objects.equals(a.getName(), b.getName());
            }
            return Objects.equals(a.getGenericDeclaration(), b.getGenericDeclaration()) &&
                Objects.equals(a.getName(), b.getName());
        }
        if (first instanceof WildcardType && second instanceof WildcardType) {
            WildcardType a = (WildcardType) first;
            WildcardType b = (WildcardType) second;
            return MiscUtils.all(a.getUpperBounds(), b.getUpperBounds(), GenericUtils::isSameType)
                && MiscUtils.all(a.getLowerBounds(), b.getLowerBounds(), GenericUtils::isSameType);
        }

        return false;
    }


    public static boolean isSubtype(Type parent, Type sub) {
        if (parent == null) {
            return sub == null;
        }

        if (parent instanceof TypeVariable) {
            Type[] parentBounds = ((TypeVariable<?>) parent).getBounds();
            if (sub instanceof TypeVariable) {
                if (isSameType(parent, sub)) {
                    return true;
                }

                return false;
            }
            if (sub instanceof WildcardType) {

            }

            return false;
        }

        if (sub instanceof TypeVariable) {
            Type[] bounds = ((TypeVariable<?>) sub).getBounds();
            for (Type bound : bounds) {
                if (isSubtype(parent, bound)) {
                    return true;
                }
            }
            return false;
        }
        if (sub instanceof WildcardType) {
            Type[] bounds = ((WildcardType) sub).getUpperBounds();
            for (Type bound : bounds) {
                if (isSubtype(parent, bound)) {
                    return true;
                }
            }
            return false;
        }

        if (parent instanceof Class<?>) {
            if (sub instanceof Class<?>) {
                return ClassUtils.isAssignable((Class<?>) sub, (Class<?>) parent);
            }
            if (sub instanceof ParameterizedType) {
                return ClassUtils.isAssignable((Class<?>) ((ParameterizedType) sub).getRawType(), (Class<?>) parent);
            }

            return false;
        }
        if (parent instanceof GenericArrayType) {
            if (sub instanceof Class) {

            }
            if (sub instanceof GenericArrayType) {

            }
        }

        return false;
    }
}
