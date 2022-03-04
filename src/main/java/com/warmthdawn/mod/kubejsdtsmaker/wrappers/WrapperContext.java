package com.warmthdawn.mod.kubejsdtsmaker.wrappers;

import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.special.IDependencyDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.special.ISpecialDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.special.ITemplateDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.special.SpecialDeclarationManager;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class WrapperContext {
    private static final Logger logger = LogManager.getLogger();
    private final List<IDeclaration> wrapperDeclarations = new ArrayList<>();
    private final Map<String, ISpecialDeclaration> extraDeclarations = new HashMap<>();
    private final Map<Class<?>, TsType> wrapperTypes = new HashMap<>();

    public List<IDeclaration> getWrapperDeclarations() {
        return wrapperDeclarations;
    }

    public List<IDeclaration> getExtraDeclarations() {
        return extraDeclarations.values().stream().map(ISpecialDeclaration::generate).collect(Collectors.toList());
    }

    public Map<Class<?>, TsType> getWrapperTypes() {
        return wrapperTypes;
    }

    public void addWrapperDeclaration(IDeclaration declaration) {
        wrapperDeclarations.add(declaration);
    }


    public void evaluateExtras(BuildContext context) {
        extraDeclarations.entrySet().removeIf(it -> {
            if (it.getValue() instanceof ITemplateDeclaration) {
                return !((ITemplateDeclaration<?>) it.getValue()).evaluate(context);
            }
            return false;
        });
    }

    public boolean containsExtra(String name) {
        return extraDeclarations.containsKey(name);
    }

    public void addSpecialDeclaration(ISpecialDeclaration declaration) {
        Objects.requireNonNull(declaration);
        String name = declaration.getIdentity();
        if (!extraDeclarations.containsKey(name)) {
            if (declaration instanceof IDependencyDeclaration) {
                List<String> dependencies = ((IDependencyDeclaration<?>) declaration).getDependencies();
                for (String dependency : dependencies) {
                    if (!extraDeclarations.containsKey(dependency)) {
                        ISpecialDeclaration dec = SpecialDeclarationManager.getInstance().get(dependency);
                        if (dec == null) {
                            logger.error("Could not find Declaration: {}", dependency);
                            continue;
                        }
                        addSpecialDeclaration(dec);
                    }
                }
            }
            extraDeclarations.put(name, declaration);
        }
    }

    public void addWrapperType(Class<?> clazz, TsType type) {
        wrapperTypes.put(clazz, type);
    }
}
