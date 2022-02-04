package com.warmthdawn.mod.kubejsgrammardump.extras;

import com.warmthdawn.mod.kubejsgrammardump.collector.JavaClassCollector;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.JavaClass;
import com.warmthdawn.mod.kubejsgrammardump.typescript.type.JavaClassProto;
import org.lwjgl.system.CallbackI;

import java.util.HashMap;
import java.util.Map;

public class JavaMethodCallFix {
    public static JavaMethodCallFix INSTANCE = new JavaMethodCallFix();
    private final Map<String, JavaClassProto> classMaps = new HashMap<>();

    public void clear() {
        classMaps.clear();
    }

    public void addClass(Class<?> clazz, JavaClassProto proto) {
        classMaps.put(clazz.getCanonicalName(), proto);
    }

    public void generate(StringBuilder builder) {
        builder.append("declare function java<K extends keyof JavaClass>(name: K): JavaClass[K]\n");
        builder.append("declare function java(name: string): any\n");
        builder.append("type JavaClass = {\n");
        classMaps.forEach((k, v) -> {
            builder.append("    ").append("'").append(k).append("': ").append(v.resolve(null).getSignature()).append(",\n");
        });
        builder.append("}\n\n");

    }


}
