package com.xiyue.trimmod.compat.jei;

import net.minecraft.world.item.ItemStack;

public record TrimUpgradeJeiRecipe(
        int fromLevel,
        int toLevel,
        int requiredEnergy,
        ItemStack requiredGem,
        ItemStack energyItem,
        int energyPerItem
) {
}

