package com.warmthdawn.mod.kubejsdtsmaker.special;

import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TypeReference;

public interface ISpecialDeclaration {
    String getIdentity();

    void evaluate();

    IDeclaration generate();
}
