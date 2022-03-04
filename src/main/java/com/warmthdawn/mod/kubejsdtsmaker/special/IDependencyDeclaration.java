package com.warmthdawn.mod.kubejsdtsmaker.special;

import java.util.List;

public interface IDependencyDeclaration<T extends IDependencyDeclaration<T>> extends ISpecialDeclaration {
    List<String> getDependencies();

    T withDependencies(String... dependencies);
    T withDependencies(ISpecialDeclaration... dependencies);
}
