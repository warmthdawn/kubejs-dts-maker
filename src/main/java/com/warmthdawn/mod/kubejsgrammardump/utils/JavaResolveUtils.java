package com.warmthdawn.mod.kubejsgrammardump.utils;

import com.google.common.collect.Lists;
import com.warmthdawn.mod.kubejsgrammardump.collector.JavaClassCollector;
import com.warmthdawn.mod.kubejsgrammardump.collector.WrappedObjectCollector;
import com.warmthdawn.mod.kubejsgrammardump.typescript.IClassMember;
import com.warmthdawn.mod.kubejsgrammardump.typescript.primitives.TSArray;
import com.warmthdawn.mod.kubejsgrammardump.typescript.primitives.TSIndexFunction;
import com.warmthdawn.mod.kubejsgrammardump.typescript.primitives.TSPrimitive;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.IType;
import com.warmthdawn.mod.kubejsgrammardump.typescript.value.JSSymbolProperty;
import com.warmthdawn.mod.kubejsgrammardump.typescript.value.Property;
import dev.latvian.mods.rhino.SymbolKey;
import dev.latvian.mods.rhino.util.ListLike;
import dev.latvian.mods.rhino.util.MapLike;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JavaResolveUtils {

    public static IType resolveClass(Class<?> clazz) {
        if (clazz.isArray()) {
            return new TSArray(resolveClass(clazz.getComponentType()));
        }

        TSPrimitive primitive = Utils.getPrimitive(clazz);
        if (primitive != null) {
            return primitive;
        }
        IType alternative = WrappedObjectCollector.INSTANCE.findAlternative(clazz);
        if (alternative != null) {
            return alternative;
        }
        //不是特殊泛型，那就直接获取
        IType result = WrappedObjectCollector.INSTANCE.findJSWarp(clazz);
        if (result != null) {
            return result;
        }
        result = JavaClassCollector.INSTANCE.resolve(clazz);
        return result;


    }

    /**
     * 获取某些为js额外添加的方法
     *
     * @return
     */
    public static List<IClassMember> resolveExtraMembers(Class<?> clazz, boolean isStatic) {
        if (!isStatic) {
            if (List.class == clazz || ListLike.class == clazz) {
                return Lists.newArrayList(
                    new Property("length", TSPrimitive.NUMBER, true),
                    new JSSymbolProperty(SymbolKey.IS_CONCAT_SPREADABLE, TSPrimitive.BOOLEAN, true),
                    new TSIndexFunction(TSPrimitive.ANY, false)
                );
            }
            if (Map.class == clazz || MapLike.class == clazz) {
                return Collections.singletonList(
                    new TSIndexFunction(TSPrimitive.ANY, false, "key", false)
                );
            }
        }
        return Collections.emptyList();
    }


}
