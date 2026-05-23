package com.xiyue.trimmod.common.trim.set.vex;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.core.init.ModEffects;
import com.xiyue.trimmod.network.ModMessages;
import com.xiyue.trimmod.network.PacketSyncVexEnergy;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;

public final class VexTick {
    private static final String ENERGY_TAG = PersistentDataKeys.VEX_ENERGY;
    private static final String TICK_TAG = PersistentDataKeys.VEX_REGEN_TICKS;

    public void tick(ServerPlayer player, boolean hasFullSet) {
        if (hasFullSet && !player.getPersistentData().contains(ENERGY_TAG)) {
            player.getPersistentData().putInt(ENERGY_TAG, 100);
            player.getPersistentData().putInt(TICK_TAG, 0);
            ModMessages.sendToPlayer(new PacketSyncVexEnergy(100), player);
        }

        int currentEnergy = player.getPersistentData().getInt(ENERGY_TAG);
        if (hasFullSet) {
            int fullRechargeTicks = Config.cooldownTicks(Config.cooldownVexSeconds);
            if (fullRechargeTicks <= 0) {
                if (currentEnergy < 100) {
                    player.getPersistentData().putInt(ENERGY_TAG, 100);
                    player.getPersistentData().putInt(TICK_TAG, 0);
                    ModMessages.sendToPlayer(new PacketSyncVexEnergy(100), player);
                }
            } else if (currentEnergy < 100) {
                int ticks = player.getPersistentData().getInt(TICK_TAG) + 1;
                int newEnergy = Math.min(100, (ticks * 100) / fullRechargeTicks);
                if (newEnergy != currentEnergy) {
                    player.getPersistentData().putInt(ENERGY_TAG, newEnergy);
                    ModMessages.sendToPlayer(new PacketSyncVexEnergy(newEnergy), player);
                }
                if (newEnergy >= 100) {
                    ticks = 0;
                }
                player.getPersistentData().putInt(TICK_TAG, ticks);
            } else if (player.getPersistentData().getInt(TICK_TAG) > 0) {
                player.getPersistentData().putInt(TICK_TAG, 0);
            }
        } else if (currentEnergy > 0 || player.getPersistentData().getInt(TICK_TAG) > 0) {
            player.getPersistentData().putInt(ENERGY_TAG, 0);
            player.getPersistentData().putInt(TICK_TAG, 0);
            ModMessages.sendToPlayer(new PacketSyncVexEnergy(0), player);
        }
    }

    public void emitSoulSacrificeAura(ServerPlayer player) {
        MobEffectInstance soulEffect = player.getEffect(ModEffects.SOUL_SACRIFICE.get());
        if (soulEffect == null || player.tickCount % 3 != 0) {
            return;
        }
        ServerLevel level = player.serverLevel();
        int amplifier = Math.max(0, soulEffect.getAmplifier());
        int soulCount = 4 + amplifier;
        double radius = 0.28D + amplifier * 0.03D;
        for (int i = 0; i < soulCount; i++) {
            double angle = player.getRandom().nextDouble() * Math.PI * 2.0D;
            double distance = radius * (0.55D + player.getRandom().nextDouble() * 0.85D);
            double x = player.getX() + Math.cos(angle) * distance;
            double y = player.getY() + 0.25D + player.getRandom().nextDouble() * 1.35D;
            double z = player.getZ() + Math.sin(angle) * distance;
            level.sendParticles(ParticleTypes.SOUL, x, y, z, 1, 0.0D, 0.015D, 0.0D, 0.0D);
        }
        int accentCount = 1 + amplifier / 2;
        for (int i = 0; i < accentCount; i++) {
            double angle = player.getRandom().nextDouble() * Math.PI * 2.0D;
            double distance = radius * (0.35D + player.getRandom().nextDouble() * 0.65D);
            double x = player.getX() + Math.cos(angle) * distance;
            double y = player.getY() + 0.55D + player.getRandom().nextDouble() * 0.85D;
            double z = player.getZ() + Math.sin(angle) * distance;
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 1, 0.01D, 0.02D, 0.01D, 0.0D);
        }
    }
}


