package com.warmthdawn.mod.kubejsdtsmaker.plugins;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.warmthdawn.mod.kubejsdtsmaker.BuilderManager;
import com.warmthdawn.mod.kubejsdtsmaker.collector.WrapperTypeWrappers;
import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.Namespace;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.global.IGlobalDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TypeReference;
import com.warmthdawn.mod.kubejsdtsmaker.wrappers.WrapperContext;
import com.warmthdawn.mod.kubejsdtsmaker.wrappers.WrapperManager;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class WrappersPlugin implements IBuilderPlugin {
    public static final String WRAPPER_NAMESPACE = "Wrappers.wrapper";
    public static final String EXTRA_NAMESPACE = "Wrappers.type";


    private WrapperContext wrapperContext = new WrapperContext();
    private BuildContext buildContext;


    @Override
    public void init(BuilderManager manager) {
        buildContext = manager.getBuildContext();
    }

    @Override
    public void onResolveFinished() {
        WrapperManager.INSTANCE.forEachWrapper(it -> it.buildAndAdd(buildContext, wrapperContext));
        wrapperContext.evaluateExtras(buildContext);

    }

    @Override
    public void addResolveClass(Set<Class<?>> resolveClass) {

        WrapperManager.INSTANCE.forEachWrapper(it -> {
            resolveClass.add(it.getTargetClass());
            resolveClass.addAll(it.getAlternativeClasses());

        });

    }

    @Override
    public TsType onParameterWrapper(Class<?> type, TypeReference old) {
        Map<Class<?>, TsType> wrapperTypes = wrapperContext.getWrapperTypes();
        return wrapperTypes.get(type);
    }


    @Override
    public void addExtraGlobals(List<IGlobalDeclaration> output, List<Namespace> extraNamespaces) {
        extraNamespaces.add(new Namespace(EXTRA_NAMESPACE, wrapperContext.getExtraDeclarations()));
        extraNamespaces.add(new Namespace(WRAPPER_NAMESPACE, wrapperContext.getWrapperDeclarations()));
    }


}
