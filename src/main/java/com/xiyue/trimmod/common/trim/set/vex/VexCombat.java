package com.xiyue.trimmod.common.trim.set.vex;

import com.xiyue.trimmod.common.effect.SoulSacrificeEffect;
import com.xiyue.trimmod.common.util.TrimUtils;
import com.xiyue.trimmod.core.init.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event;

public final class VexCombat {
    public void onCriticalHit(CriticalHitEvent event, Player player) {
        MobEffectInstance effectInstance = player.getEffect(ModEffects.SOUL_SACRIFICE.get());
        if (effectInstance == null || !(effectInstance.getEffect() instanceof SoulSacrificeEffect soulEffect)) {
            return;
        }
        int amplifier = Math.min(effectInstance.getAmplifier(), 4);
        if (!event.isVanillaCritical() && event.getResult() != Event.Result.ALLOW) {
            float critChance = 0.5f + (0.125f * amplifier);
            if (player.getRandom().nextFloat() < critChance) {
                event.setResult(Event.Result.ALLOW);
            }
        }
        boolean isCritical = event.isVanillaCritical() || event.getResult() == Event.Result.ALLOW;
        if (isCritical) {
            float bonus = soulEffect.getCritDamageBonus(amplifier);
            float base = Math.max(event.getDamageModifier(), 1.5f);
            event.setDamageModifier(base + bonus);
        }
    }

    public float fullSetDamageReduction(Player player) {
        if (TrimUtils.getTrimCount(player, TrimPatterns.VEX) < 4) {
            return 0.0f;
        }
        return 0.15f + 0.0725f * Math.min(TrimUtils.getMinUpgradeLevel(player), 4);
    }
}
