package com.xiyue.trimmod.common.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModBlocks.MOD_ID);

    // 方块物品
    public static final RegistryObject<Item> TRIM_UPGRADE_TABLE = ITEMS.register("trim_upgrade_table",
            () -> new BlockItem(ModBlocks.TRIM_UPGRADE_TABLE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MAGIC_CRYSTAL_ORE = ITEMS.register("magic_crystal_ore",
            () -> new BlockItem(ModBlocks.MAGIC_CRYSTAL_ORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> DEEPSLATE_MAGIC_CRYSTAL_ORE = ITEMS.register("deepslate_magic_crystal_ore",
            () -> new BlockItem(ModBlocks.DEEPSLATE_MAGIC_CRYSTAL_ORE.get(), new Item.Properties()));

    // 升级材料 (等级 1 - 4)
    public static final RegistryObject<Item> UPGRADE_GEM_i = ITEMS.register("upgrade_gem_i",
            () -> new Item(new Item.Properties().rarity(Rarity.COMMON)));
    public static final RegistryObject<Item> UPGRADE_GEM_ii = ITEMS.register("upgrade_gem_ii",
            () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> UPGRADE_GEM_iii = ITEMS.register("upgrade_gem_iii",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
    public static final RegistryObject<Item> UPGRADE_GEM_iv = ITEMS.register("upgrade_gem_iv",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> MIRACLE_CRYSTAL = ITEMS.register("miracle_crystal",
            () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    // 燃料：魔力水晶
    public static final RegistryObject<Item> MAGIC_CRYSTAL = ITEMS.register("magic_crystal",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE)));
}

