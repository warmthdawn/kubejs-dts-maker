package com.warmthdawn.mod.kubejsdtsmaker.special.template;

import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.special.IDependencyDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.special.ISpecialDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.special.ITemplateDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.RawDeclaration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Supplier;

public class ExtraDeclaration implements IDependencyDeclaration<ExtraDeclaration>, ITemplateDeclaration<ExtraDeclaration> {
    private static final Logger logger = LogManager.getLogger();
    private String identity;
    private List<String> dependencies;
    private Supplier<List<String>> linesSupplier;
    private Map<String, Class<?>> templateParameters;
    private List<String> lines;

    public ExtraDeclaration(String identity, Supplier<List<String>> linesSupplier) {
        this.identity = identity;
        this.dependencies = new ArrayList<>();
        this.templateParameters = new HashMap<>();
        this.linesSupplier = linesSupplier;
    }


    @Override
    public List<String> getDependencies() {
        return dependencies;
    }

    @Override
    public ExtraDeclaration withDependencies(String... dependencies) {
        this.dependencies.addAll(Arrays.asList(dependencies));
        return this;
    }

    @Override
    public ExtraDeclaration withDependencies(ISpecialDeclaration... dependencies) {
        for (ISpecialDeclaration dependency : dependencies) {
            this.dependencies.add(dependency.getIdentity());
        }
        return this;
    }

    @Override
    public String getIdentity() {
        return identity;
    }

    @Override
    public ExtraDeclaration withParam(String name, Class<?> clazz) {
        templateParameters.put(name, clazz);
        return this;
    }

    @Override
    public boolean evaluate(BuildContext context) {
        List<String> list = linesSupplier.get();
        if (list == null) {
            logger.error("Could not load external declaration: {}", getIdentity());
            return false;
        }
        this.lines = TemplateUtils.replaceParameters(context, list.stream(), templateParameters);
        return true;
    }

    @Override
    public IDeclaration generate() {
        return new RawDeclaration(lines);
    }
}
