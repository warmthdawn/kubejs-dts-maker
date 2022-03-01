package com.warmthdawn.mod.kubejsdtsmaker.plugins;


import com.warmthdawn.mod.kubejsdtsmaker.BuilderManager;
import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.context.ResolveContext;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.Namespace;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.global.IGlobalDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TypeReference;
import dev.latvian.kubejs.util.ClassFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavaMethodCallPlugin implements IBuilderPlugin {
    private BuildContext buildContext;
    private ResolveContext resolveContext;

    @Override
    public void addResolveClass(Set<Class<?>> resolveClass) {

    }

    @Override
    public void init(BuilderManager manager) {
        this.buildContext = manager.getBuildContext();
        this.resolveContext = manager.getResolveContext();
    }

    @Override
    public void addExtraGlobals(List<IGlobalDeclaration> output, List<Namespace> extraNamespaces) {
        ClassFilter classFilter = resolveContext.getBlacklist().getKubeJsClassFilter();
        Map<Class<?>, TypeReference> referenceMappings = buildContext.getConstructorReferenceMappings();
        Map<String, TsType> classMaps = new HashMap<>();
        for (Map.Entry<Class<?>, TypeReference> entry : referenceMappings.entrySet()) {
            if (classFilter.isAllowed(entry.getKey().getName())) {
                classMaps.put(entry.getKey().getName(), entry.getValue());
            }
        }
        output.add(new GlobalJavaMethodDeclaration("java", "JavaClassMappings", classMaps));
    }

    public static class GlobalJavaMethodDeclaration implements IGlobalDeclaration {
        private String methodName;
        private String typeMapName;
        private Map<String, TsType> classMaps;

        public GlobalJavaMethodDeclaration(String methodName, String typeMapName, Map<String, TsType> classMaps) {
            this.methodName = methodName;
            this.typeMapName = typeMapName;
            this.classMaps = classMaps;
        }

        @Override
        public void build(DeclarationBuilder builder) {
            builder
                .append("declare function ").append(methodName).append("<K extends keyof ")
                .append(typeMapName)
                .append(">(name: K): ")
                .append(typeMapName)
                .append("[K]")
                .newLine()
                .append("declare function ").append(methodName).append("(name: string): any")
                .newLine()
                .append("type ").append(typeMapName).append(" = {")
                .increaseIndent();
            classMaps.forEach((k, v) -> {
                builder.newLine()
                    .append("'").append(k).append("': ")
                    .append(v)
                    .append(",");
            });
            builder.decreaseIndent().newLine().append("}").newLine();
        }
    }


}
