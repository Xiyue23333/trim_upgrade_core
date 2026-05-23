package com.xiyue.trimmod.data.advancement;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.common.registry.ModItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;

import java.util.function.Consumer;

public class ModAdvancementProvider implements net.minecraft.data.advancements.AdvancementSubProvider {
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> out) {
        Advancement root = Advancement.Builder.advancement()
                .display(
                        ModItems.TRIM_UPGRADE_TABLE.get(),
                        Component.translatable("advancement.trimupgrade.root.title"),
                        Component.translatable("advancement.trimupgrade.root.description"),
                        ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/advancements/backgrounds/stone.png"),
                        FrameType.TASK,
                        false,
                        false,
                        false
                )
                .addCriterion("tick", PlayerTrigger.TriggerInstance.tick())
                .save(out, id("root"));

        Advancement getMagicCrystal = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        ModItems.MAGIC_CRYSTAL.get(),
                        Component.translatable("advancement.trimupgrade.get_magic_crystal.title"),
                        Component.translatable("advancement.trimupgrade.get_magic_crystal.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.MAGIC_CRYSTAL.get()))
                .save(out, id("get_magic_crystal"));

        Advancement getMiracleCrystal = Advancement.Builder.advancement()
                .parent(getMagicCrystal)
                .display(
                        ModItems.MIRACLE_CRYSTAL.get(),
                        Component.translatable("advancement.trimupgrade.get_miracle_crystal.title"),
                        Component.translatable("advancement.trimupgrade.get_miracle_crystal.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.MIRACLE_CRYSTAL.get()))
                .save(out, id("get_miracle_crystal"));

        Advancement craftUpgradeTable = Advancement.Builder.advancement()
                .parent(getMiracleCrystal)
                .display(
                        ModItems.TRIM_UPGRADE_TABLE.get(),
                        Component.translatable("advancement.trimupgrade.craft_upgrade_table.title"),
                        Component.translatable("advancement.trimupgrade.craft_upgrade_table.description"),
                        null,
                        FrameType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("has_item", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.TRIM_UPGRADE_TABLE.get()))
                .save(out, id("craft_upgrade_table"));

        Advancement firstUpgrade = Advancement.Builder.advancement()
                .parent(craftUpgradeTable)
                .display(
                        ModItems.UPGRADE_GEM_i.get(),
                        Component.translatable("advancement.trimupgrade.first_upgrade.title"),
                        Component.translatable("advancement.trimupgrade.first_upgrade.description"),
                        null,
                        FrameType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("upgraded_once", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item()
                                .of(ItemTags.TRIMMABLE_ARMOR)
                                .hasNbt(parseNbt("{Trim:{TrimUpgradeLevel:1}}"))
                                .build()
                ))
                .save(out, id("first_upgrade"));

        Advancement.Builder.advancement()
                .parent(firstUpgrade)
                .display(
                        ModItems.UPGRADE_GEM_iv.get(),
                        Component.translatable("advancement.trimupgrade.upgrade_trim_to_4.title"),
                        Component.translatable("advancement.trimupgrade.upgrade_trim_to_4.description"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion("has_lvl4_trim_armor", InventoryChangeTrigger.TriggerInstance.hasItems(
                        ItemPredicate.Builder.item()
                                .of(ItemTags.TRIMMABLE_ARMOR)
                                .hasNbt(parseNbt("{Trim:{TrimUpgradeLevel:4}}"))
                                .build()
                ))
                .rewards(AdvancementRewards.Builder.experience(100))
                .save(out, id("upgrade_trim_to_4"));
    }

    private static String id(String path) {
        return TrimMOD.MODID + ":" + path;
    }

    private static CompoundTag parseNbt(String snbt) {
        try {
            return TagParser.parseTag(snbt);
        } catch (CommandSyntaxException e) {
            throw new RuntimeException("Invalid SNBT for advancement predicate: " + snbt, e);
        }
    }
}
