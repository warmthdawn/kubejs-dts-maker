package com.warmthdawn.mod.kubejsdtsmaker.wrappers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.warmthdawn.mod.kubejsdtsmaker.special.ExtraDeclarations;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.PredefinedType;
import dev.latvian.kubejs.block.BlockStatePredicate;
import dev.latvian.kubejs.block.MaterialJS;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.fluid.FluidStackJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.custom.ItemType;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.item.ingredient.IngredientStackJS;
import dev.latvian.kubejs.recipe.filter.RecipeFilter;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import dev.latvian.mods.rhino.mod.wrapper.DirectionWrapper;
import jdk.nashorn.internal.ir.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityClassification;
import net.minecraft.item.Item;
import net.minecraft.nbt.CollectionNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.gen.GenerationStage;

import java.util.UUID;
import java.util.regex.Pattern;

public class BuiltinWrappers {
    public static void addDefaultWrappers() {
        WrapperManager manager = WrapperManager.INSTANCE;

        manager.builderFor(UUID.class)
            .add("string & { length: 32 | 36 }");
        WrapperBuilder regExp = manager.builderFor(Pattern.class)
            .add("RegExp")
            .add(PredefinedType.STRING);
        manager.builderFor(JsonObject.class)
            .add(PredefinedType.STRING)
            .add(PredefinedType.OBJECT);
        manager.builderFor(JsonArray.class)
            .add(PredefinedType.STRING)
            .addArray(PredefinedType.ANY);
        manager.builderFor(ResourceLocation.class)
            .add(PredefinedType.STRING);

        manager.builderFor(CompoundNBT.class)
            .acceptAny();
        manager.builderFor(CollectionNBT.class)
            .add(PredefinedType.STRING)
            .addArray(PredefinedType.ANY);
        manager.builderFor(ListNBT.class)
            .add(PredefinedType.STRING)
            .addArray(PredefinedType.ANY);
        manager.builderFor(ITextComponent.class)
            .acceptAny();
        manager.builderFor(IFormattableTextComponent.class)
            .acceptAny();
        manager.builderFor(BlockPos.class)
            .add(BlockContainerJS.class)
            .addTuple(PredefinedType.NUMBER, 3);
        manager.builderFor(Vector3d.class)
            .add(EntityJS.class)
            .add(BlockPos.class)
            .add(BlockContainerJS.class)
            .addTuple(PredefinedType.NUMBER, 3);

        manager.builderFor(Item.class)
            .nullable()
            .addExtra(ExtraDeclarations.Item)
            .addExtra(ExtraDeclarations.ItemTagSelector)
            .addLiterals("-", "air")
            .add(ItemStackJS.class);
        manager.builderFor(GenerationStage.Decoration.class)
            .addAsEnum();
        manager.builderFor(EntityClassification.class)
            .addAsEnum(it -> ((EntityClassification) it).getName());
        manager.builderFor(Color.class)
            .add(PredefinedType.NUMBER)
            .add(PredefinedType.STRING)
            .add(TextFormatting.class);
        manager.builderFor(AxisAlignedBB.class)
            .add(BlockPos.class)
            .add(PredefinedType.STRING)
            .addTuple(PredefinedType.NUMBER, 3)
            .addTuple(PredefinedType.NUMBER, 6);
        manager.builderFor(Direction.class)
            .addLiterals(DirectionWrapper.ALL.keySet());
        manager.builderFor(MapJS.class)
            .acceptAny();
        manager.builderFor(ListJS.class)
            .addArray(PredefinedType.ANY)
            .add(PredefinedType.OBJECT);
        manager.builderFor(ItemStackJS.class)
            .nullable()
            .addExtra(ExtraDeclarations.ItemStack)
            .add(IItemProvider.class);
        manager.builderFor(IngredientJS.class)
            .nullable()
            .addExtra(ExtraDeclarations.Ingredient);
        manager.builderFor(IngredientStackJS.class)
            .nullable()
            .addExtra(ExtraDeclarations.Ingredient);
        manager.builderFor(Text.class)
            .acceptAny();
        manager.builderFor(BlockStatePredicate.class)
            .withWrap(ExtraDeclarations.ArrayOrSelf)
            .add(Block.class)
            .add(BlockState.class)
            .addExtra(ExtraDeclarations.BlockStatePredicate);
        manager.builderFor(FluidStackJS.class)
            .addExtra(ExtraDeclarations.FluidStack);
        manager.builderFor(RecipeFilter.class)
            .nullable()
            .addExtra(ExtraDeclarations.RecipeFilter);
        manager.builderFor(MaterialJS.class)
            .addExtra(ExtraDeclarations.Material);
        manager.builderFor(ItemType.class)
            .addExtra(ExtraDeclarations.ItemType);
        manager.builderFor(dev.latvian.mods.rhino.mod.util.color.Color.class)
            .addExtra(ExtraDeclarations.Color)
            .add("`#${string}` | number");

        //TODO: Unit

    }
}
