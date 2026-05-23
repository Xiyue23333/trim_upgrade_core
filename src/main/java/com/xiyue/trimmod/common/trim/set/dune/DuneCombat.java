package com.xiyue.trimmod.common.trim.set.dune;

import com.xiyue.trimmod.common.util.TrimUtils;
import com.xiyue.trimmod.core.init.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.armortrim.TrimPatterns;

public final class DuneCombat {
    public void applyAttackerEffects(Player attacker, LivingEntity target) {
        if (TrimUtils.getTrimCount(attacker, TrimPatterns.DUNE) < 4) {
            return;
        }
        int minLevel = TrimUtils.getMinUpgradeLevel(attacker);
        int effectLevel = minLevel <= 0 ? 1 : (minLevel <= 2 ? 2 : 3);
        target.addEffect(new MobEffectInstance(ModEffects.PHARAO_CURSE.get(), 60, effectLevel - 1));
    }
}
