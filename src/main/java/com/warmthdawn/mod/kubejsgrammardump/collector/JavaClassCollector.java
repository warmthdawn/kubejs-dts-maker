package com.warmthdawn.mod.kubejsgrammardump.collector;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.ClassPath;
import com.warmthdawn.mod.kubejsgrammardump.KubeJSGrammarDump;
import com.warmthdawn.mod.kubejsgrammardump.typescript.primitives.EmptyClass;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.AbstractClass;
import com.warmthdawn.mod.kubejsgrammardump.utils.JavaTypeUtils;
import com.warmthdawn.mod.kubejsgrammardump.utils.Utils;
import com.warmthdawn.mod.kubejsgrammardump.typescript.namespace.Namespace;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.JavaClass;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.JavaClassProto;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.util.ClassFilter;
import net.minecraft.util.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.*;

public class JavaClassCollector {
    public static JavaClassCollector INSTANCE = new JavaClassCollector();
    private ScriptManager classFilter;
    private final Multimap<Namespace, AbstractClass> classes = HashMultimap.create();
    private final Map<Class<?>, JavaClass> resolvedClasses = new HashMap<>();


    public Multimap<Namespace, AbstractClass> getClasses() {
        return classes;
    }

    public Map<Class<?>, JavaClass> getResolvedClasses() {
        return resolvedClasses;
    }

    private static Logger logger = LogManager.getLogger();

    public IType resolve(Class<?> clazz) {
        if (!resolvedClasses.containsKey(clazz)) {
            logger.warn("Class {} are not resolved, using empty", clazz.getCanonicalName());
//            Package aPackage = clazz.getPackage();
//            if (aPackage == null) {
//                return null;
//            }
//            resolveClass(aPackage.getName(), clazz);
        }
        JavaClass javaClass = resolvedClasses.get(clazz);
        return javaClass == null ? EmptyClass.INSTANCE : javaClass;
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

    public JavaClass resolveClass(String packageName, Class<?> clazz) {
        JavaClass type = resolvedClasses.get(clazz);
        if (type != null) {
            return type;
        }

        try {
            if (!Modifier.isPublic(clazz.getModifiers())) {
                return null;
            }
            List<JavaClass> extendFrom = new ArrayList<>();
            for (Class<?> clazzInterface : clazz.getInterfaces()) {
                JavaClass interfaceType = resolveClass(clazzInterface.getPackage().getName(), clazzInterface);
                if (interfaceType != null) {
                    extendFrom.add(interfaceType);
                }
            }
            Namespace namespace = Utils.getNamespace(packageName);
            if (clazz.isInterface()) {
                return resolve(namespace, clazz, extendFrom);
            }

            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
                JavaClass superType = resolveClass(superclass.getPackage().getName(), superclass);
                if(superType != null) {
                    extendFrom.add(superType);
                }
            }
            return resolve(namespace, clazz, extendFrom);

        } catch (NoClassDefFoundError e) {
            logger.warn("Could not load class", e);
            return null;
        }
    }

    private JavaClass resolve(Namespace namespace, Class<?> clazz, List<JavaClass> extendFrom) {

        try {
            JavaClass javaClass = JavaTypeUtils.resolveClass(clazz, extendFrom, namespace);
            JavaClassProto constructor = JavaTypeUtils.resolveProto(clazz, namespace);
            if (constructor != null) {
                classes.put(namespace, constructor);
                if (javaClass != null) {
                    javaClass.setProto(constructor);
                }
            }
            if (javaClass != null) {
                classes.put(namespace, javaClass);
            }
            resolvedClasses.put(clazz, javaClass);
            return javaClass;

        } catch (NoClassDefFoundError e) {
            logger.warn("Could not load class", e);
            return null;
        }
    }
}
