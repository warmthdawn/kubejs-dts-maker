package com.warmthdawn.mod.kubejsdtsmaker;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.builder.GlobalMemberFactory;
import com.warmthdawn.mod.kubejsdtsmaker.builder.TypescriptFactory;
import com.warmthdawn.mod.kubejsdtsmaker.collector.WrappedBindingsEvent;
import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.context.KubeJsGlobalContext;
import com.warmthdawn.mod.kubejsdtsmaker.context.ResolveContext;
import com.warmthdawn.mod.kubejsdtsmaker.plugins.IBuilderPlugin;
import com.warmthdawn.mod.kubejsdtsmaker.plugins.JavaMethodCallPlugin;
import com.warmthdawn.mod.kubejsdtsmaker.resolver.JavaClassResolver;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.DeclarationFile;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.global.IGlobalDeclaration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class BuilderManager {

    private KubeJsGlobalContext kubeJsGlobalContext = new KubeJsGlobalContext();
    private ResolveContext context = new ResolveContext();
    private BuildContext buildContext = new BuildContext();
    private TypescriptFactory tsFactory = new TypescriptFactory(context, buildContext);
    private GlobalMemberFactory memberFactory = new GlobalMemberFactory(tsFactory, buildContext, context, kubeJsGlobalContext);

    private Set<Class<?>> beginClasses = new HashSet<>();


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

    private List<IBuilderPlugin> plugins;

    public void forEachPlugin(Consumer<IBuilderPlugin> consumer) {
        for (IBuilderPlugin plugin : plugins) {
            consumer.accept(plugin);
        }
    }

    private BuilderManager(List<IBuilderPlugin> plugins) {
        this.plugins = new ArrayList<>(plugins);
        forEachPlugin(it -> it.init(this));
    }

    public static BuilderManager create() {
        List<IBuilderPlugin> plugins = new ArrayList<>();
        plugins.add(new JavaMethodCallPlugin());
        BuilderManager manager = new BuilderManager(plugins);
        WrappedBindingsEvent.collectGlobals(manager.kubeJsGlobalContext);
        manager.beginClasses.addAll(manager.kubeJsGlobalContext.getReferencedClasses());
        manager.forEachPlugin(it -> it.addResolveClass(manager.beginClasses));
        manager.context.getBlacklist().getWhitelistClass().addAll(manager.beginClasses);
        return manager;
    }


    public void resolveClasses() {
        JavaClassResolver.resolve(beginClasses, context, 2);

    }


    public String generateResult() {
        DeclarationFile file = tsFactory.createFile();
        List<IGlobalDeclaration> globals = memberFactory.createGlobals();
        forEachPlugin(it -> it.addExtraGlobals(globals));
        file.addGlobals(globals);

        DeclarationBuilder declarationBuilder = new DeclarationBuilder();
        file.build(declarationBuilder);

        return declarationBuilder.build();
    }
}
