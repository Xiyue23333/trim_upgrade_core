package com.xiyue.trimmod.common.trim.set.wild;

import com.xiyue.trimmod.common.trim.context.PlayerTrimContext;
import com.xiyue.trimmod.common.util.TrimUtils;
import com.xiyue.trimmod.core.init.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;

public final class WildCombat {
    public void onEffectApplicable(MobEffectEvent.Applicable event, PlayerTrimContext context) {
        if (event.getEffectInstance().getEffect() != MobEffects.POISON) {
            return;
        }
        if (context.trimCount(TrimPatterns.WILD) >= 4) {
            event.setResult(Event.Result.DENY);
        }
    }

    public void onAttackerHurt(Player attacker, LivingEntity target) {
        int wildCount = TrimUtils.getTrimCount(attacker, TrimPatterns.WILD);
        if (wildCount < 4) {
            return;
        }
        int minWildLevel = TrimUtils.getMinUpgradeLevel(attacker);
        int amp = (minWildLevel <= 1) ? 0 : (minWildLevel <= 3 ? 1 : 2);
        target.addEffect(new MobEffectInstance(ModEffects.WILD_POISON.get(), 60, amp));
    }
}
