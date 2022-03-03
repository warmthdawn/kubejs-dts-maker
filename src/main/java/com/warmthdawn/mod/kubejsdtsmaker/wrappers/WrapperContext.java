package com.warmthdawn.mod.kubejsdtsmaker.wrappers;

import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;

import java.util.*;

public class WrapperContext {
    private List<IDeclaration> wrapperDeclarations = new ArrayList<>();
    private List<IDeclaration> extraDeclarations = new ArrayList<>();
    private Map<Class<?>, TsType> wrapperTypes = new HashMap<>();

    public List<IDeclaration> getWrapperDeclarations() {
        return wrapperDeclarations;
    }

    public List<IDeclaration> getExtraDeclarations() {
        return extraDeclarations;
    }

    public Map<Class<?>, TsType> getWrapperTypes() {
        return wrapperTypes;
    }

    public void addWrapperDeclaration(IDeclaration declaration) {
        wrapperDeclarations.add(declaration);
    }

    public void addExtraDeclarations(Collection<? extends IDeclaration> declaration) {
        extraDeclarations.addAll(declaration);
    }

    public void addWrapperType(Class<?> clazz, TsType type) {
        wrapperTypes.put(clazz, type);
    }
}
