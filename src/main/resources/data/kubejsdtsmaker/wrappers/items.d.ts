// @ts-nocheck

type ItemObject = {
    item?: Item,
    tag?: ItemTag,
    count?: number,
    nbt?: any,
}

type IngredientObject = {
    ingredient?: Ingredient
    item?: Item,
    tag?: ItemTag,
    fluid?: FiuidStack
    count?: number,
    amount?: number,
}

type ItemStack = Counted<Item | ItemTagSelector | ModSelector | CreativeTabSelector> | ItemObject | EmptyItem | RegExp;

type Ingredient = Counted<Item | ItemTagSelector | ModSelector | CreativeTabSelector | "*"> | IngredientObject | EmptyItem | RegExp;
