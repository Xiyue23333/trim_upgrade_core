package com.xiyue.trimmod.common.registry;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.common.enchant.InheritanceEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEnchantments {
    private ModEnchantments() {}

    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, TrimMOD.MODID);

    public static final RegistryObject<Enchantment> INHERITANCE =
            ENCHANTMENTS.register("inheritance", InheritanceEnchantment::new);
}

