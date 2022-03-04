// @ts-nocheck

type RecipeFilter = ArrayOrSelf<{
    exact?: boolean,
    or?: RecipeFilter,
    not?: RecipeFilter,
    id?: string,
    type?: string,
    group?: string,
    mod?: ModId,
    input?: Ingredient,
    output?: Ingredient,
}>