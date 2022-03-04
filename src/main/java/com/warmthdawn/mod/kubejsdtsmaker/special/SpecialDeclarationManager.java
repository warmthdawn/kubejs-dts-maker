package com.warmthdawn.mod.kubejsdtsmaker.special;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecialDeclarationManager {
    private static SpecialDeclarationManager instance = new SpecialDeclarationManager();

    public static SpecialDeclarationManager getInstance() {
        return instance;
    }

    private Map<String, ISpecialDeclaration> declarationMap = new HashMap<>();


    public void add(ISpecialDeclaration declaration) {
        declarationMap.put(declaration.getIdentity(), declaration);
    }

    public ISpecialDeclaration get(String identity) {
        return declarationMap.get(identity);
    }





}
