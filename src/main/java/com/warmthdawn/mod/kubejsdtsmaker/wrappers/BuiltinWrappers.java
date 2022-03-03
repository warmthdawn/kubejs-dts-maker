package com.warmthdawn.mod.kubejsdtsmaker.wrappers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.PredefinedType;
import dev.latvian.kubejs.block.BlockStatePredicate;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.kubejs.util.ListJS;
import dev.latvian.kubejs.util.MapJS;
import dev.latvian.kubejs.world.BlockContainerJS;
import net.minecraft.entity.EntityClassification;
import net.minecraft.nbt.CollectionNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
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
        manager.builderFor(Pattern.class)
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
        //TODO: Item
        manager.builderFor(GenerationStage.Decoration.class)
            .addAsEnum();
        manager.builderFor(EntityClassification.class)
            .addAsEnum();
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
            .addAsEnum();
        manager.builderFor(MapJS.class)
            .acceptAny();
        manager.builderFor(ListJS.class)
            .addArray(PredefinedType.ANY)
            .add(PredefinedType.OBJECT);
        //TODO: ItemStackJS
        //TODO: IngredientJS
        //TODO: IngredientStackJS
        manager.builderFor(Text.class)
            .acceptAny();
        //TODO: BlockStatePredicate
        //TODO: FluidStackJS
        //TODO: RecipeFilter
        //TODO: MaterialJS
        //TODO: ItemType
        //TODO: dev.latvian.mods.rhino.mod.util.color.Color
        //TODO: Unit
    }
}
