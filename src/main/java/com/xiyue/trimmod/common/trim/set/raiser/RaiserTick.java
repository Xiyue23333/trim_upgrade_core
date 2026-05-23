package com.xiyue.trimmod.common.trim.set.raiser;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public final class RaiserTick {
    public void tick(Player player, boolean hasFullSet, int minLevel) {
        if (!hasFullSet) {
            if (player.hasEffect(MobEffects.SATURATION)) {
                player.removeEffect(MobEffects.SATURATION);
            }
            return;
        }

        int duration = 2 + minLevel;
        if (player.getTicksFrozen() > 0) {
            player.setTicksFrozen(0);
        }

        if (player.tickCount % 300 == 0 && player.getFoodData().needsFood()) {
            player.addEffect(new MobEffectInstance(MobEffects.SATURATION, duration, 0, true, false));
            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        player.getX(), player.getY() + 2.0, player.getZ(),
                        5, 0.3, 0.3, 0.3, 0.05);
            }
        }
    }
}
