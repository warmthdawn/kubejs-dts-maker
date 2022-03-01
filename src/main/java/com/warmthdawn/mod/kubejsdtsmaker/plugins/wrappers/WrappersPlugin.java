package com.warmthdawn.mod.kubejsdtsmaker.plugins.wrappers;

import com.warmthdawn.mod.kubejsdtsmaker.BuilderManager;
import com.warmthdawn.mod.kubejsdtsmaker.collector.WrapperTypeWrappers;
import com.warmthdawn.mod.kubejsdtsmaker.plugins.IBuilderPlugin;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.Namespace;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.InterfaceDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.global.IGlobalDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TypeReference;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.KubeJSPlugins;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WrappersPlugin implements IBuilderPlugin {
    private List<Class<?>> wrapperClasses = new ArrayList<>();
    private static final String ADD_TYPE_WRAPPERS_EVENT = "dtsmaker.add_type_wrappers";


    public WrappersPlugin() {
        WrapperTypeWrappers wrappers = new WrapperTypeWrappers();
        KubeJSPlugins.forEachPlugin(p -> p.addTypeWrappers(ScriptType.SERVER, wrappers));
        Set<Class<?>> classes = wrappers.getWrappers().keySet();
        wrapperClasses.addAll(classes);
    }

    @Override
    public void init(BuilderManager manager) {

    }

    @Override
    public void onResolveFinished() {
        AddTypeWrappersEvent wrappersEvent = new AddTypeWrappersEvent();
        wrappersEvent.post(ScriptType.SERVER, ADD_TYPE_WRAPPERS_EVENT);
        Map<Class<?>, String[]> configWrappers = wrappersEvent.getConfigWrappers();

    }

    @Override
    public TypeReference onParameterWrapper(Class<?> type, TypeReference old) {
        return null;
    }


    @Override
    public void addResolveClass(Set<Class<?>> resolveClass) {

    }

    @Override
    public void addExtraGlobals(List<IGlobalDeclaration> output, List<Namespace> extraNamespaces) {
        extraNamespaces.add(generateWrappersNamespace());
    }


    public Namespace generateWrappersNamespace() {
        List<IDeclaration> declarations = new ArrayList<>();
        return new Namespace("Wrappers", declarations);
    }
}
