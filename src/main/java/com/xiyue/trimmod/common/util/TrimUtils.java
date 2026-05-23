package com.xiyue.trimmod.common.util;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPattern;

public final class TrimUtils {
    private TrimUtils() {}

    public static int getUpgradeLevel(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("Trim")) {
            return stack.getTag().getCompound("Trim").getInt("TrimUpgradeLevel");
        }
        return 0;
    }

    public static int getMinUpgradeLevel(Player player) {
        int min = 99;
        boolean hasAny = false;
        for (ItemStack s : player.getArmorSlots()) {
            if (!s.isEmpty()) {
                int lvl = getUpgradeLevel(s);
                min = Math.min(min, lvl);
                hasAny = true;
            } else {
                return 0;
            }
        }
        return hasAny ? min : 0;
    }

    public static boolean isTrim(Player player, ItemStack stack, ResourceKey<TrimPattern> patternKey) {
        if (stack.isEmpty()) return false;
        return ArmorTrim.getTrim(player.level().registryAccess(), stack)
                .map(trim -> trim.pattern().is(patternKey))
                .orElse(false);
    }

    public static int getTrimCount(Player player, ResourceKey<TrimPattern> patternKey) {
        int count = 0;
        for (ItemStack stack : player.getArmorSlots()) {
            if (isTrim(player, stack, patternKey)) {
                count++;
            }
        }
        return count;
    }

    public static Holder<TrimPattern> getFullSetPatternHolder(Player player) {
        Holder<TrimPattern> first = null;
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.isEmpty()) return null;
            var trim = ArmorTrim.getTrim(player.level().registryAccess(), armor);
            if (trim.isEmpty()) return null;

            Holder<TrimPattern> pattern = trim.get().pattern();
            if (first == null) first = pattern;
            else if (!pattern.equals(first)) return null;
        }
        return first;
    }
}
