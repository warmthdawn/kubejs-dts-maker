package com.warmthdawn.mod.kubejsdtsmaker;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.builder.GlobalMemberFactory;
import com.warmthdawn.mod.kubejsdtsmaker.builder.TypescriptFactory;
import com.warmthdawn.mod.kubejsdtsmaker.bytecode.BytecodeUtils;
import com.warmthdawn.mod.kubejsdtsmaker.bytecode.ScanResult;
import com.warmthdawn.mod.kubejsdtsmaker.collector.WrappedBindingsEvent;
import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.context.GlobalTypeScope;
import com.warmthdawn.mod.kubejsdtsmaker.context.KubeJsGlobalContext;
import com.warmthdawn.mod.kubejsdtsmaker.context.ResolveContext;
import com.warmthdawn.mod.kubejsdtsmaker.java.JavaTypeInfo;
import com.warmthdawn.mod.kubejsdtsmaker.plugins.*;
import com.warmthdawn.mod.kubejsdtsmaker.plugins.WrappersPlugin;
import com.warmthdawn.mod.kubejsdtsmaker.resolver.JavaClassResolver;
import com.warmthdawn.mod.kubejsdtsmaker.resolver.MethodParameterNameResolver;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.DeclarationFile;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.Namespace;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.global.IGlobalDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.util.MiscUtils;
import com.warmthdawn.mod.kubejsdtsmaker.wrappers.BuiltinWrappers;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class BuilderManager {

    private final KubeJsGlobalContext kubeJsGlobalContext = new KubeJsGlobalContext();
    private final ResolveContext context = new ResolveContext();
    private final BuildContext buildContext = new BuildContext();

    private final MethodParameterNameResolver parameterNameResolver = new MethodParameterNameResolver();
    private final TypescriptFactory tsFactory = new TypescriptFactory(this, context, buildContext, parameterNameResolver);
    private final GlobalMemberFactory memberFactory = new GlobalMemberFactory(tsFactory, buildContext, context, kubeJsGlobalContext);

    private final Set<Class<?>> beginClasses = new HashSet<>();


    public KubeJsGlobalContext getKubeJsGlobalContext() {
        return kubeJsGlobalContext;
    }

    public ResolveContext getResolveContext() {
        return context;
    }

    public BuildContext getBuildContext() {
        return buildContext;
    }

    public TypescriptFactory getTypescriptFactory() {
        return tsFactory;
    }

    public GlobalMemberFactory getMemberFactory() {
        return memberFactory;
    }

    public Set<Class<?>> getBeginClasses() {
        return beginClasses;
    }

    public List<IBuilderPlugin> getPlugins() {
        return plugins;
    }

    private final List<IBuilderPlugin> plugins;

    public void forEachPlugin(Consumer<IBuilderPlugin> consumer) {
        for (IBuilderPlugin plugin : plugins) {
            consumer.accept(plugin);
        }
    }

    public <T> T applyOnPlugin(Function<IBuilderPlugin, T> function) {
        for (IBuilderPlugin plugin : plugins) {
            T apply = function.apply(plugin);
            if (apply != null) {
                return apply;
            }
        }
        return null;
    }

    private BuilderManager(List<IBuilderPlugin> plugins) {
        this.plugins = new ArrayList<>(plugins);
        forEachPlugin(it -> it.init(this));
    }

    public static BuilderManager create() {
        List<IBuilderPlugin> plugins = new ArrayList<>();
        plugins.add(new JavaMethodCallPlugin());
        plugins.add(new EventJSPlugin());
        plugins.add(new RhinoExtrasPlugin());
        plugins.add(new WrappersPlugin());
        BuilderManager manager = new BuilderManager(plugins);
        ScanResult result = BytecodeUtils.scanAllMods();
        manager.parameterNameResolver.acceptScanData(result);
        manager.forEachPlugin(it -> it.acceptScanData(result));
        WrappedBindingsEvent.collectGlobals(manager.kubeJsGlobalContext);
        manager.beginClasses.addAll(manager.kubeJsGlobalContext.getReferencedClasses());
        manager.forEachPlugin(it -> it.addResolveClass(manager.beginClasses));
        manager.context.getBlacklist().getWhitelistClass().addAll(manager.beginClasses);
        return manager;
    }


    public void resolveClasses() {
        JavaClassResolver.resolve(beginClasses, context, 2);
        context.getTypeInfos().values().forEach(it -> it.finalizeResolve(context));


        GlobalTypeScope typeScope = context.getTypeScope();
        Map<Class<?>, JavaTypeInfo> allTypes = context.getTypeInfos();
        for (Class<?> clazz : allTypes.keySet()) {
            String namespace = MiscUtils.getNamespace(clazz);
            String namespaceName = typeScope.namespaceNoConflict(namespace);
            buildContext.addNamespace(clazz, namespaceName);
        }

        forEachPlugin(IBuilderPlugin::onResolveFinished);
    }


    public String generateResult() {
        DeclarationFile file = tsFactory.createFile();
        List<IGlobalDeclaration> globals = memberFactory.createGlobals();
        List<Namespace> extraNamespaces = new ArrayList<>();
        forEachPlugin(it -> it.addExtraGlobals(globals, extraNamespaces));
        file.addGlobals(globals);
        file.addExtraNamespaces(extraNamespaces);

        DeclarationBuilder declarationBuilder = new DeclarationBuilder();
        file.build(declarationBuilder);

        return declarationBuilder.build();
    }
}
