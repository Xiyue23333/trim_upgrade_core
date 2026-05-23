package com.xiyue.trimmod.common.util;

import com.xiyue.trimmod.Config;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;

public final class TrimSkillCooldowns {
    private static final Item SWAMP_COOLDOWN_ITEM = Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE;

    private TrimSkillCooldowns() {}

    public static boolean isOnCooldown(Player player, ResourceKey<TrimPattern> pattern) {
        return player.getCooldowns().isOnCooldown(getCooldownItem(pattern));
    }

    public static float getCooldownPercent(Player player, ResourceKey<TrimPattern> pattern) {
        return player.getCooldowns().getCooldownPercent(getCooldownItem(pattern), 0.0F);
    }

    public static void startCooldown(ServerPlayer player, ResourceKey<TrimPattern> pattern, int minLevel) {
        int cooldownTicks = getCooldownTicks(pattern, minLevel);
        if (cooldownTicks > 0) {
            player.getCooldowns().addCooldown(getCooldownItem(pattern), cooldownTicks);
        }
    }

    public static boolean isSwampOnCooldown(Player player) {
        return player.getCooldowns().isOnCooldown(SWAMP_COOLDOWN_ITEM);
    }

    public static float getSwampCooldownPercent(Player player) {
        return player.getCooldowns().getCooldownPercent(SWAMP_COOLDOWN_ITEM, 0.0F);
    }

    public static void startSwampCooldown(ServerPlayer player, int minLevel) {
        int clampedLevel = Math.max(0, Math.min(4, minLevel));
        int seconds = Math.max(1, 20 - clampedLevel);
        int cooldownTicks = Config.cooldownTicks(seconds);
        if (cooldownTicks > 0) {
            player.getCooldowns().addCooldown(SWAMP_COOLDOWN_ITEM, cooldownTicks);
        }
    }

    private static int getCooldownTicks(ResourceKey<TrimPattern> pattern, int minLevel) {
        if (pattern.equals(TrimPatterns.TIDE)) {
            return Config.cooldownTicks(Config.cooldownTideSeconds);
        }
        if (pattern.equals(TrimPatterns.SPIRE)) {
            return Config.cooldownTicks(Config.cooldownSpireSeconds);
        }
        if (pattern.equals(TrimPatterns.SNOUT)) {
            return Math.max(1, Config.cooldownTicks(Config.cooldownSnoutSeconds) - (Math.max(0, minLevel) * 5));
        }
        if (pattern.equals(TrimPatterns.WAYFINDER)) {
            return Config.cooldownTicks(Config.cooldownWayfinderSeconds);
        }
        if (pattern.equals(TrimPatterns.VEX)) {
            return Config.cooldownTicks(Config.cooldownVexSeconds);
        }
        if (pattern.equals(TrimPatterns.COAST)) {
            return Config.cooldownTicks(Config.cooldownCoastSeconds);
        }
        if (pattern.equals(TrimPatterns.DUNE)) {
            return Config.cooldownTicks(Config.cooldownDuneSeconds);
        }
        return 0;
    }

    private static Item getCooldownItem(ResourceKey<TrimPattern> pattern) {
        if (pattern.equals(TrimPatterns.TIDE)) {
            return Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE;
        }
        if (pattern.equals(TrimPatterns.SPIRE)) {
            return Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE;
        }
        if (pattern.equals(TrimPatterns.SNOUT)) {
            return Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE;
        }
        if (pattern.equals(TrimPatterns.WAYFINDER)) {
            return Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE;
        }
        if (pattern.equals(TrimPatterns.VEX)) {
            return Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE;
        }
        if (pattern.equals(TrimPatterns.COAST)) {
            return Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE;
        }
        if (pattern.equals(TrimPatterns.DUNE)) {
            return Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE;
        }
        return Items.ENDER_PEARL;
    }
}
