package com.xiyue.trimmod.common.registry;

import com.xiyue.trimmod.TrimMOD;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TrimMOD.MODID);

    public static final RegistryObject<CreativeModeTab> TRIM_UPGRADE_TAB = CREATIVE_MODE_TABS.register("trim_upgrade_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.TRIM_UPGRADE_TABLE.get()))
                    .title(Component.translatable("creativetab.trim_upgrade_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.TRIM_UPGRADE_TABLE.get());
                        output.accept(ModItems.MAGIC_CRYSTAL_ORE.get());
                        output.accept(ModItems.DEEPSLATE_MAGIC_CRYSTAL_ORE.get());
                        output.accept(ModItems.UPGRADE_GEM_i.get());
                        output.accept(ModItems.UPGRADE_GEM_ii.get());
                        output.accept(ModItems.UPGRADE_GEM_iii.get());
                        output.accept(ModItems.UPGRADE_GEM_iv.get());
                        output.accept(ModItems.MAGIC_CRYSTAL.get());
                        output.accept(ModItems.MIRACLE_CRYSTAL.get());

                        // “继承”附魔书（便于创造模式直接拿取）
                        for (int lvl = 1; lvl <= 4; lvl++) {
                            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
                            EnchantedBookItem.addEnchantment(book, new EnchantmentInstance(ModEnchantments.INHERITANCE.get(), lvl));
                            output.accept(book);
                        }
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
