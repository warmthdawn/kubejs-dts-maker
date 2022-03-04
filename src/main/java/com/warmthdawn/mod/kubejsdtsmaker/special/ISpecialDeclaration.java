package com.warmthdawn.mod.kubejsdtsmaker.special;

import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;

public interface ISpecialDeclaration {
    String getIdentity();

    IDeclaration generate();
}
