package com.warmthdawn.mod.kubejsdtsmaker.plugins;

import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.InterfaceDeclaration;
import dev.latvian.kubejs.recipe.RecipeEventJS;

public class RecipePlugin implements IBuilderPlugin {

    @Override
    public void onInterfaceBuild(Class<?> javaClazz, InterfaceDeclaration interfaceDeclaration, boolean isStatic) {
        if (isStatic) {
            return;
        }

        if(javaClazz == RecipeEventJS.class) {



        }

    }
}
