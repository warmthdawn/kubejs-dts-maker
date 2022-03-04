package com.warmthdawn.mod.kubejsdtsmaker.special.generated;

import com.warmthdawn.mod.kubejsdtsmaker.special.ISpecialDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.special.SpecialDeclarationManager;
import com.warmthdawn.mod.kubejsdtsmaker.util.ReflectionUtils;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.item.custom.ItemType;
import dev.latvian.kubejs.item.custom.ItemTypes;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import net.minecraft.item.ItemGroup;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RegistryDeclarations {
    private static ISpecialDeclaration resourceLocation(String identity, Supplier<? extends Collection<ResourceLocation>> entriesSupplier) {
        ResourceLocationRegistries result = new ResourceLocationRegistries(identity, entriesSupplier);
        SpecialDeclarationManager.getInstance().add(result);
        return result;
    }

    private static ISpecialDeclaration collection(String identity, Supplier<? extends Collection<String>> entriesSupplier) {
        CollectionRegistries result = new CollectionRegistries(identity, entriesSupplier);
        SpecialDeclarationManager.getInstance().add(result);
        return result;
    }

    //Registries
    public static final ISpecialDeclaration ITEMS = resourceLocation("ItemsRegistries",
        ForgeRegistries.ITEMS::getKeys);

    public static final ISpecialDeclaration BLOCKS = resourceLocation("BlocksRegistries",
        ForgeRegistries.BLOCKS::getKeys);

    public static final ISpecialDeclaration FLUIDS = resourceLocation("FluidsRegistries",
        ForgeRegistries.FLUIDS::getKeys);


    public static final ISpecialDeclaration ITEM_TAGS = resourceLocation("ItemTagsRegistries",
        () -> TagCollectionManager.getInstance().getItems().getAvailableTags());

    public static final ISpecialDeclaration BLOCK_TAGS = resourceLocation("BlockTagsRegistries",
        () -> TagCollectionManager.getInstance().getBlocks().getAvailableTags());

    public static final ISpecialDeclaration FLUID_TAGS = resourceLocation("FluidTagsRegistries",
        () -> TagCollectionManager.getInstance().getFluids().getAvailableTags());

    public static final ISpecialDeclaration ENTITY_TYPES = resourceLocation("EntityTypeTagsRegistries",
        () -> TagCollectionManager.getInstance().getEntityTypes().getAvailableTags());


    //Collections
    public static final ISpecialDeclaration CREATIVE_TABS = collection("CreativeTabsCollection",
        () -> Arrays.stream(ItemGroup.TABS).map(ItemGroup::getRecipeFolderName).collect(Collectors.toList()));

    public static final ISpecialDeclaration MATERIALS = collection("MaterialsCollection",
        MaterialListJS.INSTANCE.map::keySet);

    public static final ISpecialDeclaration ITEM_TYPES = collection("ItemTypesCollection",
        () -> ReflectionUtils.<Map<String, ItemType>>getStaticField(ItemTypes.class, "MAP", Collections::emptyMap).keySet());

    public static final ISpecialDeclaration MOD_IDS = collection("ModIdsCollection",
        () -> ModList.get().getMods().stream().map(ModInfo::getModId).collect(Collectors.toList()));
    public static final ISpecialDeclaration COLORS = collection("ColorsCollection",
        ColorWrapper.MAP::keySet);


}
