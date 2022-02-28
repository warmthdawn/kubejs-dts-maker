package com.warmthdawn.mod.kubejsdtsmaker.builder;

import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.context.KubeJsGlobalContext;
import com.warmthdawn.mod.kubejsdtsmaker.context.ResolveContext;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.Namespace;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.InterfaceDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.TypeAliasDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.global.GlobalVariableDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.global.IGlobalDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GlobalMemberFactory {
    private TypescriptFactory tsFactory;
    private BuildContext buildContext;
    private ResolveContext resolveContext;
    private KubeJsGlobalContext globalContext;

    public GlobalMemberFactory(TypescriptFactory tsFactory, BuildContext buildContext, ResolveContext resolveContext, KubeJsGlobalContext globalContext) {
        this.tsFactory = tsFactory;
        this.buildContext = buildContext;
        this.resolveContext = resolveContext;
        this.globalContext = globalContext;
    }

    public Namespace createWrappersNamespace() {
        return null;
    }

    public List<IGlobalDeclaration> createGlobals() {

        List<IGlobalDeclaration> result = new ArrayList<>();
        List<GlobalVariableDeclaration> globalVariables = createGlobalVariables();
        result.addAll(globalVariables);

        return result;
    }

    public List<GlobalVariableDeclaration> createGlobalVariables() {
        Map<String, Class<?>> typeAliases = globalContext.getTypeAliases();

        List<GlobalVariableDeclaration> result = new ArrayList<>();

        for (Map.Entry<String, Class<?>> entry : typeAliases.entrySet()) {
            TsType reference = buildContext.makeConstructorReference(entry.getValue());
            result.add(new GlobalVariableDeclaration(entry.getKey(), true, reference));
        }

        Map<String, Class<?>> globalVars = globalContext.getGlobalVars();

        for (Map.Entry<String, Class<?>> entry : globalVars.entrySet()) {
            TsType reference = tsFactory.createReferenceNonnull(entry.getValue());
            result.add(new GlobalVariableDeclaration(entry.getKey(), true, reference));
        }

        return result;
    }
}
