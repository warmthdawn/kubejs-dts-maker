package com.warmthdawn.mod.kubejsgrammardump.collector;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.ClassPath;
import com.warmthdawn.mod.kubejsgrammardump.KubeJSGrammarDump;
import com.warmthdawn.mod.kubejsgrammardump.extras.JavaMethodCallFix;
import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.*;
import com.warmthdawn.mod.kubejsgrammardump.typescript.primitives.TSArray;
import com.warmthdawn.mod.kubejsgrammardump.typescript.primitives.TSPrimitive;
import com.warmthdawn.mod.kubejsgrammardump.typescript.primitives.TSUnionType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.*;
import com.warmthdawn.mod.kubejsgrammardump.utils.JavaTypeUtils;
import com.warmthdawn.mod.kubejsgrammardump.utils.Utils;
import com.warmthdawn.mod.kubejsgrammardump.typescript.namespace.Namespace;
import dev.latvian.kubejs.script.ScriptManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

public class JavaClassCollector {
    public static JavaClassCollector INSTANCE = new JavaClassCollector();
    private ScriptManager classFilter;
    private final Multimap<Namespace, IDeclaredType> classes = HashMultimap.create();
    private final Map<Class<?>, IDeclaredType> resolvedClasses = new HashMap<>();

    private boolean isLocked = false;
    public void lock() {
        isLocked = true;
    }
    public void clear() {
        classes.clear();
        resolvedClasses.clear();
        visiting.clear();
        isLocked = false;
    }

    public Multimap<Namespace, IDeclaredType> getClasses() {
        return ImmutableMultimap.copyOf(classes);
    }

    public Map<Class<?>, IDeclaredType> getResolvedClasses() {
        return resolvedClasses;
    }

    private static Logger logger = LogManager.getLogger();

    public IType resolve(Class<?> clazz) {
        if (!resolvedClasses.containsKey(clazz)) {
//            logger.warn("Class {} are not resolved, using any", clazz.getCanonicalName());
//            Package aPackage = clazz.getPackage();
//            if (aPackage == null) {
//                return null;
//            }
//            resolveClass(aPackage.getName(), clazz);
        }
        IDeclaredType javaClass = resolvedClasses.get(clazz);
        return javaClass == null ? TSPrimitive.ANY : javaClass;
    }

    public void setClassFilter(ScriptManager classFilter) {
        this.classFilter = classFilter;
    }


    public void findAllClasses() throws IOException {
        for (ClassPath.ClassInfo classInfo : ClassPath.from(KubeJSGrammarDump.class.getClassLoader()).getAllClasses()) {
            if (!classFilter.isClassAllowed(classInfo.getName())) {
                continue;
            }
            Class<?> clazz = null;
            try {
                clazz = Class.forName(classInfo.getName());
            } catch (ClassNotFoundException | NoClassDefFoundError | RuntimeException e) {
                logger.warn("Could not load class {} for {}", classInfo.getName(), e);
            }
            if (clazz != null) {
                resolveClass(classInfo.getPackageName(), clazz);
            }
        }

    }

    public IType resolveClass(Class<?> clazz) {
        if (visiting.contains(clazz)) {
            //防止无限递归
            return Utils.getClassType(clazz);
        }
        TSPrimitive primitive = Utils.getPrimitive(clazz);
        if (primitive != null) {
            return primitive;
        }
        Package aPackage = clazz.getPackage();
        String packageName = aPackage == null ? "defaultPackage" : aPackage.getName();
        IType alternative = WrappedObjectCollector.INSTANCE.findAlternative(clazz);
        if (alternative != null) {
            return alternative;
        }
        IDeclaredType resolved = resolveClass(packageName, clazz);
        if (resolved == null) {
            return null;
        }
        return WrappedObjectCollector.INSTANCE.findJSWarp(clazz);
    }

    private final Set<Class<?>> visiting = new HashSet<>();

    private IDeclaredType resolveClass(String packageName, Class<?> clazz) {
        IDeclaredType type = resolvedClasses.get(clazz);
        if (type != null) {
            return type;
        }
        visiting.add(clazz);
        try {
            if (clazz.isAnonymousClass() || clazz.isLocalClass() || !Modifier.isPublic(clazz.getModifiers())) {
                visiting.remove(clazz);
                return null;
            }


            List<IPartialType> extendFrom = new ArrayList<>();
            for (Type clazzInterface : clazz.getGenericInterfaces()) {
                IPartialType interfaceType = resolveGenericSuperclass(clazzInterface);
                if (interfaceType != null) {
                    extendFrom.add(interfaceType);
                }
            }
            Namespace namespace;
            if (clazz.isMemberClass()) {
                Class<?> enclosingClass = clazz.getEnclosingClass();
                IType enclosing = resolveClass(enclosingClass);
                if (enclosing instanceof IDeclaredType) {
                    namespace = Utils.getNamespace(((IDeclaredType) enclosing).getNamespace(), ((IDeclaredType) enclosing).getName());
                } else {
                    visiting.remove(clazz);
                    return null;
                }
            } else {
                namespace = Utils.getNamespace(packageName);
            }
            if (clazz.isInterface()) {
                IDeclaredType resolve = resolve(namespace, clazz, extendFrom);
                visiting.remove(clazz);
                return resolve;
            }

            Type superclass = clazz.getGenericSuperclass();
            if (superclass != null) {
                IPartialType superType = resolveGenericSuperclass(superclass);
                if (superType != null) {
                    extendFrom.add(superType);
                }
            }
            IDeclaredType resolve = resolve(namespace, clazz, extendFrom);
            visiting.remove(clazz);
            return resolve;

        } catch (NoClassDefFoundError e) {
            logger.warn("Could not load class", e);
            visiting.remove(clazz);
            return null;
        }
    }

    private IPartialType resolveGenericSuperclass(Type type) {
        if (type instanceof Class) {
            return resolveClass((Class<?>) type);
        }

        if (type instanceof ParameterizedType) {
            return resolvePartialType(type);
        }

        return TSPrimitive.UNKNOWN;
    }

    private IType resolveType(Type type) {
        if (type instanceof Class) {
            return resolveClass((Class<?>) type);
        }

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (rawType instanceof Class) {
                IType rawResult = resolveClass((Class<?>) rawType);
                if (rawResult == null) {
                    return TSPrimitive.UNKNOWN;
                }
                Type[] arguments = parameterizedType.getActualTypeArguments();
                IType[] argumentTypes = new IType[arguments.length];
                for (int i = 0; i < arguments.length; i++) {
                    if (arguments[i] instanceof TypeVariable) {
//                        logger.warn("Trying to resolve a variable generic: {}", type);
                        argumentTypes[i] = TSPrimitive.ANY;
                    } else {
                        IType argType = resolveType(arguments[i]);
                        argumentTypes[i] = argType;
                    }
                }
                return new ResolvedGenericType(rawResult, argumentTypes);
            } else {
                return TSPrimitive.UNKNOWN;
            }
        }

        if (type instanceof GenericArrayType) {
            return new TSArray(resolveType(((GenericArrayType) type).getGenericComponentType()));
        }

        if (type instanceof WildcardType) {
            return TSPrimitive.ANY;
        }

        return TSPrimitive.UNKNOWN;
    }

    private IPartialType resolvePartialType(Type type) {
        if (type instanceof Class) {
            IType result = resolveClass((Class<?>) type);
            if (result == null) {
                return TSPrimitive.UNKNOWN;
            }
            return result;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();

            if (rawType instanceof Class) {
                IType rawResult = resolveClass((Class<?>) rawType);
                if (rawResult == null) {
                    return TSPrimitive.UNKNOWN;
                }
                Type[] arguments = parameterizedType.getActualTypeArguments();
                IPartialType[] argumentTypes = new IPartialType[arguments.length];
                for (int i = 0; i < arguments.length; i++) {
                    IPartialType argType = resolvePartialType(arguments[i]);
                    argumentTypes[i] = argType;
                }
                return new GenericType(rawResult, argumentTypes);
            } else {
                return TSPrimitive.UNKNOWN;
            }
        }

        if (type instanceof TypeVariable) {
            Type[] bounds = ((TypeVariable<?>) type).getBounds();
            IType[] boundsResult = new IType[bounds.length];
            for (int i = 0; i < bounds.length; i++) {
                boundsResult[i] = resolveType(bounds[i]);
            }
            return new GenericVariable(boundsResult, ((TypeVariable<?>) type).getName());
        }

        if (type instanceof GenericArrayType) {
            return new GenericTSArray(resolvePartialType(((GenericArrayType) type).getGenericComponentType()));
        }

        if (type instanceof WildcardType) {
            return TSPrimitive.ANY;
        }

        return TSPrimitive.UNKNOWN;
    }


    private IDeclaredType resolve(Namespace namespace, Class<?> clazz, List<IPartialType> extendFrom) {

        try {
            JavaClass javaClass = JavaTypeUtils.resolveClass(clazz, extendFrom, namespace);

            if (javaClass.isEmpty()) {
                IType unionType = TSUnionType.create(javaClass);
                WrappedObjectCollector.INSTANCE.addAlternative(clazz, unionType);
                if (unionType instanceof IDeclaredType) {
                    if(isLocked) {
                        throw new IllegalStateException("Can not modify");
                    }
                    JavaClassProto constructor = JavaTypeUtils.resolveProto(clazz, namespace, null);
                    classes.put(namespace, (IDeclaredType) unionType);
                    resolvedClasses.put(clazz, (IDeclaredType) unionType);
                    if (constructor != null) {
                        if (constructor.hasStaticMembers()) {
                            classes.put(namespace, constructor);
                            if (classFilter.isClassAllowed(clazz.getCanonicalName())) {
                                JavaMethodCallFix.INSTANCE.addClass(clazz, constructor);
                            }
                        }
                    }
                    return (IDeclaredType) unionType;
                } else {
                    resolvedClasses.put(clazz, null);
                    return null;
                }
            } else {
                if(isLocked) {
                    throw new IllegalStateException("Can not modify");
                }
                classes.put(namespace, javaClass);
                resolvedClasses.put(clazz, javaClass);
                JavaClassProto constructor = JavaTypeUtils.resolveProto(clazz, namespace, javaClass);
                if (constructor != null) {
                    if (!javaClass.isEmpty() || constructor.hasStaticMembers()) {
                        classes.put(namespace, constructor);
                        if (classFilter.isClassAllowed(clazz.getCanonicalName())) {
                            JavaMethodCallFix.INSTANCE.addClass(clazz, constructor);
                        }
                        javaClass.setProto(constructor);
                    }
                }
                return javaClass;
            }

        } catch (NoClassDefFoundError e) {
            logger.warn("Could not load class", e);
            return null;
        }
    }


}
