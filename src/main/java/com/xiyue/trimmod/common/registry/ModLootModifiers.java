package com.xiyue.trimmod.common.registry;

import com.mojang.serialization.Codec;
import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.common.loot.AddItemLootModifier;
import com.xiyue.trimmod.common.loot.RemoveItemLootModifier;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, TrimMOD.MODID);

    public static final RegistryObject<Codec<AddItemLootModifier>> ADD_ITEM =
            LOOT_MODIFIERS.register("add_item", () -> AddItemLootModifier.CODEC);

    public static final RegistryObject<Codec<RemoveItemLootModifier>> REMOVE_ITEM =
            LOOT_MODIFIERS.register("remove_item", () -> RemoveItemLootModifier.CODEC);
}

