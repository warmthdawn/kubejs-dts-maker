package com.warmthdawn.mod.kubejsdtsmaker.plugins.wrappers;

import com.sun.xml.internal.bind.v2.model.core.ID;
import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.CustomDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.generic.TypeArguments;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.*;
import com.warmthdawn.mod.kubejsdtsmaker.util.BuilderUtils;
import dev.latvian.mods.rhino.NativeArray;
import net.minecraft.util.Tuple;

import java.util.*;

public class WrappersResolver {

    private final BuildContext buildContext;
    private final String extraNamespace;

    public WrappersResolver(BuildContext buildContext, String extraNamespace) {
        this.buildContext = buildContext;
        this.extraNamespace = extraNamespace;
    }


    public List<IDeclaration> resolveExtraTypes(List<Tuple<Map<String, Object>, String>> extraTypes) {
        List<IDeclaration> result = new ArrayList<>(extraTypes.size());
        for (Tuple<Map<String, Object>, String> extraType : extraTypes) {
            Map<String, Object> mappings = extraType.getA();
            Map<String, TsType> typeMappings = new HashMap<>(mappings.size());
            String content = extraType.getB();
            for (Map.Entry<String, Object> entry : mappings.entrySet()) {
                String replacement = "${" + entry.getKey() + "}";
                content = content.replace(replacement, resolveType(entry.getValue()).getSignature());
            }
            result.add(new CustomDeclaration(content));

        }
        return result;
    }

    public Map<Class<?>, TsType> resolveWrappers(Map<Class<?>, Object[]> wrappersCfg) {
        Map<Class<?>, TsType> result = new HashMap<>(wrappersCfg.size());
        for (Map.Entry<Class<?>, Object[]> entry : wrappersCfg.entrySet()) {

            TsType wrappersType = createWrappersType(entry.getKey(), entry.getValue());
            result.put(entry.getKey(), wrappersType);
        }

        return result;
    }

    public TsType createWrappersType(Class<?> key, Object[] config) {
        List<TsType> members = new ArrayList<>();
        TypeReference self = createTypeReference(key);
        for (Object o : config) {
            TsType tsType = resolveType(o);
            members.add(tsType);
        }

        if (members.size() == 0) {
            throw new IllegalArgumentException("config can not be empty");
        }
        if (members.size() == 1) {
            TsType tsType = members.get(0);
            if (tsType == PredefinedTypes.ANY) {
                return PredefinedTypes.ANY;
            }
        }
        members.add(0, self);
        return new UnionType(members);
    }


    public TypeReference createTypeReference(Class<?> clazz) {
        TypeArguments typeArguments = BuilderUtils.createEmptyTypeArguments(clazz.getTypeParameters().length);
        String namespace = buildContext.getNamespace(clazz);
        return new TypeReference(typeArguments, namespace, clazz.getSimpleName());
    }


    public TsType resolveStringType(String obj) {
        for (PredefinedTypes value : PredefinedTypes.values()) {
            if (Objects.equals(obj, value.getSignature())) {
                return value;
            }
        }

        return new TypeReference(null, extraNamespace, obj);
    }

    public TsType resolveType(Object obj) {
        if (obj instanceof Class<?>) {
            return createTypeReference((Class<?>) obj);
        }
        if (obj instanceof EmbeddedType) {
            return (TsType) obj;
        } else if (obj instanceof String) {
            return resolveStringType((String) obj);
        } else if (obj instanceof NativeArray) {
            ArrayList<TsType> result = new ArrayList<>(((NativeArray) obj).size());
            for (Object item : (NativeArray) obj) {
                result.add(resolveType(item));
            }
            if (result.size() == 1) {
                return new ArrayType(result.get(0));
            }
            if (result.size() > 1) {
                return new TupleType(result);
            }
            throw new IllegalArgumentException("Array can not be empty!");

        }

        throw new IllegalArgumentException("Unsupported type: " + obj.getClass());
    }

}
