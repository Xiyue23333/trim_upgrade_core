package com.xiyue.trimmod.common.trim.set.tide;

import com.xiyue.trimmod.common.util.TrimUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;

public final class TideCombat {
    public void onCriticalHit(CriticalHitEvent event, Player player) {
        int tideCount = 0;
        int minTideLevel = 99;
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.isEmpty()) {
                continue;
            }
            var trim = ArmorTrim.getTrim(player.level().registryAccess(), stack);
            if (trim.isPresent() && trim.get().pattern().is(TrimPatterns.TIDE)) {
                tideCount++;
                minTideLevel = Math.min(minTideLevel, TrimUtils.getUpgradeLevel(stack));
            }
        }
        if (tideCount < 4) {
            return;
        }

        int safeLevel = Math.max(0, Math.min(minTideLevel, 4));
        boolean isDay = player.level().isDay();
        boolean isOverworld = player.level().dimension() == net.minecraft.world.level.Level.OVERWORLD;
        float[] dayCritRates = {0.17f, 0.25f, 0.32f, 0.40f, 0.50f};
        float[] nightCritBonus = {0.15f, 0.255f, 0.375f, 0.48f, 0.60f};

        if (isOverworld) {
            if (isDay) {
                if (event.getResult() != Event.Result.ALLOW && player.getRandom().nextFloat() < dayCritRates[safeLevel]) {
                    event.setResult(Event.Result.ALLOW);
                }
            } else {
                event.setDamageModifier(event.getDamageModifier() + nightCritBonus[safeLevel]);
            }
        } else {
            if (event.getResult() != Event.Result.ALLOW && player.getRandom().nextFloat() < dayCritRates[safeLevel] / 2.0f) {
                event.setResult(Event.Result.ALLOW);
            }
            event.setDamageModifier(event.getDamageModifier() + nightCritBonus[safeLevel] / 2.0f);
        }
    }

    public float damageReductionForPiece(Player player, ItemStack stack, int level) {
        var trim = ArmorTrim.getTrim(player.level().registryAccess(), stack);
        if (trim.isPresent() && trim.get().pattern().is(TrimPatterns.TIDE)) {
            return 0.015f + 0.0075f * level;
        }
        return 0.0f;
    }
}
