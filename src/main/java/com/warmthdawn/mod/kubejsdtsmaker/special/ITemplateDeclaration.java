package com.warmthdawn.mod.kubejsdtsmaker.special;

import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;

public interface ITemplateDeclaration<T extends ITemplateDeclaration<T>> extends ISpecialDeclaration{

    T withParam(String name, Class<?> clazz);
    boolean evaluate(BuildContext context);
}
