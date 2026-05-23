package com.xiyue.trimmod.common.trim.set.host;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public final class HostTick {
    public void applyPassiveEffects(Player player, boolean hasFullSet, int minLevel) {
        if (!hasFullSet) {
            return;
        }
        player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 220, minLevel, true, false, true));
    }
}
