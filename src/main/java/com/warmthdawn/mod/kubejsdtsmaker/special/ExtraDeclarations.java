package com.warmthdawn.mod.kubejsdtsmaker.special;

import com.warmthdawn.mod.kubejsdtsmaker.special.generated.RegistryDeclarations;
import com.warmthdawn.mod.kubejsdtsmaker.special.template.ExtraDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.special.template.ExternalDeclarationsManager;

import java.util.Arrays;

import static com.warmthdawn.mod.kubejsdtsmaker.special.generated.RegistryDeclarations.*;
import static com.warmthdawn.mod.kubejsdtsmaker.special.template.ExtraDeclarationsUtils.*;

public class ExtraDeclarations {

    //Utils
    public static final ExtraDeclaration ItemOfArray = external("utiltypes.d.ts", "ItemOfArray");
    public static final ExtraDeclaration ItemOfRegistry = external("utiltypes.d.ts", "ItemOfRegistry");
    public static final ExtraDeclaration Counted = external("utiltypes.d.ts", "Counted");
    public static final ExtraDeclaration Amounted = external("utiltypes.d.ts", "Amounted");
    public static final ExtraDeclaration ArrayOrSelf = raw("ArrayOrSelf", "type ArrayOrSelf<T> = T | T[]");
    public static final ExtraDeclaration EmptyItem = simple("EmptyItem", "\"-\" | \"air\"");
    public static final ExtraDeclaration EmptyFluid = simple("EmptyFluid", "\"-\" | \"empty\" | \"minecraft:empty\"");


    //Registries
    public static final ExtraDeclaration Item = wrap("Item", ItemOfRegistry, ITEMS);
    public static final ExtraDeclaration Block = wrap("Block", ItemOfRegistry, BLOCKS);
    public static final ExtraDeclaration Fluid = wrap("Fluid", ItemOfRegistry, FLUIDS);
    public static final ExtraDeclaration ItemTag = wrap("ItemTag", ItemOfRegistry, ITEM_TAGS);
    public static final ExtraDeclaration BlockTag = wrap("BlockTag", ItemOfRegistry, BLOCK_TAGS);
    public static final ExtraDeclaration FluidTag = wrap("FluidTag", ItemOfRegistry, FLUID_TAGS);
    public static final ExtraDeclaration EntityTypeTag = wrap("EntityTypeTag", ItemOfRegistry, ENTITY_TYPES);


    public static final ExtraDeclaration CreativeTab = wrap("CreativeTab", ItemOfArray, CREATIVE_TABS);
    public static final ExtraDeclaration Material = wrap("Material", ItemOfArray, MATERIALS);
    public static final ExtraDeclaration ModId = wrap("ModId", ItemOfArray, MOD_IDS);
    public static final ISpecialDeclaration ItemType = wrap("ItemType", ItemOfArray, ITEM_TYPES);
    public static final ISpecialDeclaration Color = wrap("Color", ItemOfArray, COLORS);
    ;

    //Prefix Strings
    public static final ExtraDeclaration ItemTagSelector = prefix("ItemTagSelector", ItemTag, "#");
    public static final ExtraDeclaration BlockTagSelector = prefix("BlockTagSelector", BlockTag, "#");
    public static final ExtraDeclaration FluidTagSelector = prefix("FluidTagSelector", FluidTag, "#");
    public static final ExtraDeclaration EntityTypeTagSelector = prefix("EntityTypeTagSelector", EntityTypeTag, "#");

    public static final ExtraDeclaration ModSelector = prefix("ModSelector", ModId, "@");
    public static final ExtraDeclaration CreativeTabSelector = prefix("CreativeTabSelector", CreativeTab, "%");

    //Items
    public static final ExtraDeclaration ItemObject = external("items.d.ts", "ItemObject")
        .withDependencies(ItemTag, Item);
    public static final ExtraDeclaration ItemStack = external("items.d.ts", "ItemStack")
        .withDependencies(Item, ItemTagSelector, ModSelector, CreativeTabSelector, ItemObject, EmptyItem, Counted);

    //Fluids
    public static final ExtraDeclaration FluidObject = external("fluids.d.ts", "FluidObject")
        .withDependencies(Fluid);
    public static final ExtraDeclaration FluidStack = external("fluids.d.ts", "FluidStack")
        .withDependencies(Fluid, FluidObject, EmptyFluid, Amounted);

    //Ingredient
    public static final ExtraDeclaration IngredientObject = external("items.d.ts", "IngredientObject")
        .withDependencies(Item, ItemTag, FluidStack);
    public static final ExtraDeclaration Ingredient = external("items.d.ts", "Ingredient")
        .withDependencies(Item, ItemTagSelector, ModSelector, CreativeTabSelector, IngredientObject, EmptyItem, Counted);

    public static final ExtraDeclaration BlockStatePredicate = simple("BlockStatePredicate", "BlockTagSelector | Block | `${Block}[${string}]` | RegExp")
        .withDependencies(BlockTagSelector, Block);

    //Recipe
    public static final ExtraDeclaration RecipeFilter = external("recipes.d.ts", "RecipeFilter")
        .withDependencies(Ingredient, ArrayOrSelf, ModId);
}
