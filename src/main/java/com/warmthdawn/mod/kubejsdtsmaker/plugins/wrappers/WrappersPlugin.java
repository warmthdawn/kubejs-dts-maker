package com.warmthdawn.mod.kubejsdtsmaker.plugins.wrappers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.warmthdawn.mod.kubejsdtsmaker.BuilderManager;
import com.warmthdawn.mod.kubejsdtsmaker.collector.WrapperTypeWrappers;
import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.plugins.IBuilderPlugin;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.Namespace;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.InterfaceDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.global.IGlobalDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TypeReference;
import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.KubeJSPlugins;
import net.minecraft.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WrappersPlugin implements IBuilderPlugin {
    private static final String ADD_TYPE_WRAPPERS_EVENT = "dtsmaker.add_type_wrappers";
    private static final String WRAPPER_NAMESPACE = "Wrappers.wrapper";
    private static final String EXTRA_NAMESPACE = "Wrappers.type";
    private static final String WRAPPER_SUFFIX = "Wrapper";

    private List<IDeclaration> extraTypes;
    private WrappersResolver wrappersResolver;

    private BiMap<String, Class<?>> wrapperNames = HashBiMap.create();
    private Map<Class<?>, TsType> wrappers;

    public WrappersPlugin() {
        WrapperTypeWrappers wrappers = new WrapperTypeWrappers();
        KubeJSPlugins.forEachPlugin(p -> p.addTypeWrappers(ScriptType.SERVER, wrappers));
        Set<Class<?>> classes = wrappers.getWrappers().keySet();
        for (Class<?> clazz : classes) {
            String name = clazz.getSimpleName() + WRAPPER_SUFFIX;
            String actualName = name;
            for (int i = 0; !wrapperNames.containsKey(actualName); i++) {
                actualName = name + i;
            }
            wrapperNames.put(actualName, clazz);
        }
    }

    @Override
    public void init(BuilderManager manager) {
        BuildContext buildContext = manager.getBuildContext();
        wrappersResolver = new WrappersResolver(buildContext, WRAPPER_NAMESPACE);
    }

    @Override
    public void onResolveFinished() {
        AddTypeWrappersEvent wrappersEvent = new AddTypeWrappersEvent();
        wrappersEvent.post(ScriptType.SERVER, ADD_TYPE_WRAPPERS_EVENT);

        List<Tuple<Map<String, Object>, String>> extraTypes = wrappersEvent.getExtraTypes();
        this.extraTypes = wrappersResolver.resolveExtraTypes(extraTypes);
        Map<Class<?>, Object[]> configWrappers = wrappersEvent.getConfigWrappers();
        wrappers = wrappersResolver.resolveWrappers(configWrappers);

    }

    @Override
    public TypeReference onParameterWrapper(Class<?> type, TypeReference old) {
        BiMap<Class<?>, String> inverse = wrapperNames.inverse();
        if (inverse.containsKey(type)) {
            return new TypeReference(null, WRAPPER_NAMESPACE, inverse.get(type));
        }
        return null;
    }


    @Override
    public void addExtraGlobals(List<IGlobalDeclaration> output, List<Namespace> extraNamespaces) {
        extraNamespaces.add(new Namespace(EXTRA_NAMESPACE, extraTypes));
        extraNamespaces.add(generateWrappersNamespace());
    }


    public Namespace generateWrappersNamespace() {
        List<IDeclaration> declarations = new ArrayList<>();
        for (Map.Entry<String, Class<?>> entry : wrapperNames.entrySet()) {
            TsType tsType = wrappers.get(entry.getValue());

        }
        return new Namespace(WRAPPER_NAMESPACE, declarations);
    }
}
