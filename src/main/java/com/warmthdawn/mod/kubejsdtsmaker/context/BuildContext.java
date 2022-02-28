package com.warmthdawn.mod.kubejsdtsmaker.context;

import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.InterfaceDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.PredefinedTypes;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TypeReference;
import com.warmthdawn.mod.kubejsdtsmaker.util.BuilderUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class BuildContext {
    private Map<Class<?>, String> namespaceMappings;
    private Map<Class<?>, IDeclaration> typeMappings;
    private Map<Class<?>, InterfaceDeclaration> constructorTypeMappings;
    private Map<Class<?>, TypeReference> constructorReferenceMappings;
    private static final Logger logger = LogManager.getLogger();

    public BuildContext() {
        namespaceMappings = new HashMap<>();
        typeMappings = new HashMap<>();
        constructorTypeMappings = new HashMap<>();
        constructorReferenceMappings = new HashMap<>();
    }

    private boolean loaded = false;

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public void addNamespace(Class<?> raw, String mapped) {
        namespaceMappings.put(raw, mapped);
    }

    public void addType(Class<?> clazz, IDeclaration type, InterfaceDeclaration constructor) {
        typeMappings.put(clazz, type);
        if (constructor != null) {
            constructorTypeMappings.put(clazz, constructor);
            constructorReferenceMappings.put(clazz,
                (TypeReference) makeConstructorReference(clazz, constructor)
            );
        }
    }

    public Map<Class<?>, TypeReference> getConstructorReferenceMappings() {
        return constructorReferenceMappings;
    }

    public InterfaceDeclaration getConstructorType(Class<?> clazz) {
        if (!loaded) {
            logger.error("Attempting to fetch type before full loaded!");
        }
        return constructorTypeMappings.get(clazz);
    }


    public TsType makeConstructorReference(Class<?> clazz) {

        InterfaceDeclaration constructorType = getConstructorType(clazz);
        return makeConstructorReference(clazz, constructorType);
    }

    private TsType makeConstructorReference(Class<?> clazz, InterfaceDeclaration constructorType) {
        String namespace = getNamespace(clazz);

        if (constructorType == null) {
            return PredefinedTypes.ANY;
        }
        return new TypeReference(null, namespace, constructorType.getIdentity());
    }

    public String getNamespace(Class<?> clazz) {
        return namespaceMappings.get(clazz);
    }


}
