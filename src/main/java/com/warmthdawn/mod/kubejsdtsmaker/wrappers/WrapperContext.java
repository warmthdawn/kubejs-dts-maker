package com.warmthdawn.mod.kubejsdtsmaker.wrappers;

import com.warmthdawn.mod.kubejsdtsmaker.special.ISpecialDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.RawDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;

import java.util.*;

public class WrapperContext {
    private List<IDeclaration> wrapperDeclarations = new ArrayList<>();
    private Map<String, IDeclaration> extraDeclarations = new HashMap<>();
    private Map<Class<?>, TsType> wrapperTypes = new HashMap<>();

    public List<IDeclaration> getWrapperDeclarations() {
        return wrapperDeclarations;
    }

    public List<IDeclaration> getExtraDeclarations() {
        return new ArrayList<>(extraDeclarations.values());
    }

    public Map<Class<?>, TsType> getWrapperTypes() {
        return wrapperTypes;
    }

    public void addWrapperDeclaration(IDeclaration declaration) {
        wrapperDeclarations.add(declaration);
    }

    public void addExtraDeclarations(String name, RawDeclaration declaration) {
        if (!extraDeclarations.containsKey(name)) {
            extraDeclarations.put(name, declaration);
        }
    }

    public boolean containsExtra(String name) {
        return extraDeclarations.containsKey(name);
    }

    public void addExtraDeclaration(ISpecialDeclaration declaration) {
        String name = declaration.getIdentity();
        if (!extraDeclarations.containsKey(name)) {
            extraDeclarations.put(name, declaration.generate());
        }
    }

    public void addWrapperType(Class<?> clazz, TsType type) {
        wrapperTypes.put(clazz, type);
    }
}
