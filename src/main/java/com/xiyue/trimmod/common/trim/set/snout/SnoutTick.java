package com.xiyue.trimmod.common.trim.set.snout;

import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.network.ModMessages;
import com.xiyue.trimmod.network.PacketSyncSnoutEnergy;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class SnoutTick {
    public void tickEnergy(ServerPlayer player, boolean hasFullSet, int fullSetMinLevel) {
        if (hasFullSet && !player.getPersistentData().contains(SnoutSkill.ENERGY_TAG)) {
            player.getPersistentData().putInt(SnoutSkill.ENERGY_TAG, 100);
            player.getPersistentData().putInt(SnoutSkill.TICK_TAG, 0);
            ModMessages.sendToPlayer(new PacketSyncSnoutEnergy(100), player);
        }

        int energy = player.getPersistentData().getInt(SnoutSkill.ENERGY_TAG);
        if (hasFullSet) {
            int fullRechargeTicks = Math.max(1, Config.cooldownTicks(Config.cooldownSnoutSeconds) - (fullSetMinLevel * 5));
            if (energy < 100) {
                int ticks = player.getPersistentData().getInt(SnoutSkill.TICK_TAG) + 1;
                int newEnergy = Math.min(100, (ticks * 100) / fullRechargeTicks);
                if (newEnergy != energy) {
                    player.getPersistentData().putInt(SnoutSkill.ENERGY_TAG, newEnergy);
                    ModMessages.sendToPlayer(new PacketSyncSnoutEnergy(newEnergy), player);
                }
                if (newEnergy >= 100) {
                    ticks = 0;
                }
                player.getPersistentData().putInt(SnoutSkill.TICK_TAG, ticks);
            } else if (player.getPersistentData().getInt(SnoutSkill.TICK_TAG) > 0) {
                player.getPersistentData().putInt(SnoutSkill.TICK_TAG, 0);
            }
        } else if (energy > 0 || player.getPersistentData().getInt(SnoutSkill.TICK_TAG) > 0) {
            player.getPersistentData().putInt(SnoutSkill.ENERGY_TAG, 0);
            player.getPersistentData().putInt(SnoutSkill.TICK_TAG, 0);
            ModMessages.sendToPlayer(new PacketSyncSnoutEnergy(0), player);
        }
    }

    public void tickDash(ServerPlayer player) {
        int dashTicks = player.getPersistentData().getInt(SnoutSkill.DASH_TICKS_TAG);
        if (dashTicks <= 0) {
            return;
        }

        double dirX = player.getPersistentData().getDouble(SnoutSkill.DASH_DIR_X_TAG);
        double dirZ = player.getPersistentData().getDouble(SnoutSkill.DASH_DIR_Z_TAG);
        double speed = player.getPersistentData().getDouble(SnoutSkill.DASH_SPEED_TAG);
        int dashId = player.getPersistentData().getInt(SnoutSkill.DASH_ID_TAG);
        int minLevel = player.getPersistentData().getInt(SnoutSkill.DASH_LEVEL_TAG);
        float damage = player.getPersistentData().getFloat(SnoutSkill.DASH_DAMAGE_TAG);
        ServerLevel level = player.serverLevel();
        Vec3 direction = new Vec3(dirX, 0.0D, dirZ);
        if (direction.lengthSqr() < 1.0E-6D) {
            direction = Vec3.directionFromRotation(0.0F, player.getYRot());
        } else {
            direction = direction.normalize();
        }
        final Vec3 finalDirection = direction;

        double vertical = player.onGround() ? 0.0D : Math.max(-0.12D, player.getDeltaMovement().y);
        player.setDeltaMovement(finalDirection.x * speed, vertical, finalDirection.z * speed);
        player.hurtMarked = true;
        player.hasImpulse = true;
        player.setSprinting(true);
        player.fallDistance = 0.0F;

        if ((player.tickCount & 1) == 0) {
            level.sendParticles(ParticleTypes.FLAME,
                    player.getX(), player.getY() + 0.15D, player.getZ(),
                    4, 0.22D, 0.05D, 0.22D, 0.01D);
            level.sendParticles(ParticleTypes.SMALL_FLAME,
                    player.getX(), player.getY() + 0.1D, player.getZ(),
                    3, 0.18D, 0.04D, 0.18D, 0.005D);
            level.sendParticles(ParticleTypes.LAVA,
                    player.getX(), player.getY() + 0.08D, player.getZ(),
                    1, 0.12D, 0.03D, 0.12D, 0.0D);
        }

        AABB hitArea = player.getBoundingBox().inflate(0.55D).move(finalDirection.scale(0.65D));
        level.getEntitiesOfClass(LivingEntity.class, hitArea, entity -> entity != player && entity.isAlive()).forEach(target -> {
            String hitKey = SnoutSkill.HIT_KEY_PREFIX + player.getId();
            if (target.getPersistentData().getInt(hitKey) == dashId) {
                return;
            }

            target.getPersistentData().putInt(hitKey, dashId);
            target.hurt(player.damageSources().playerAttack(player), damage);
            target.setSecondsOnFire(3 + minLevel);
            target.knockback(1.1D + (minLevel * 0.1D), -finalDirection.x, -finalDirection.z);
            level.sendParticles(ParticleTypes.FLAME,
                    target.getX(), target.getY() + 1.0D, target.getZ(),
                    8, 0.25D, 0.25D, 0.25D, 0.02D);
        });

        dashTicks--;
        player.getPersistentData().putInt(SnoutSkill.DASH_TICKS_TAG, dashTicks);
        if (dashTicks <= 0) {
            clearDashState(player);
        }
    }

    public void clearDashState(ServerPlayer player) {
        player.getPersistentData().putInt(SnoutSkill.DASH_TICKS_TAG, 0);
        player.getPersistentData().putInt(SnoutSkill.DASH_MAX_TICKS_TAG, 0);
        player.getPersistentData().remove(SnoutSkill.DASH_DIR_X_TAG);
        player.getPersistentData().remove(SnoutSkill.DASH_DIR_Z_TAG);
        player.getPersistentData().remove(SnoutSkill.DASH_SPEED_TAG);
        player.getPersistentData().remove(SnoutSkill.DASH_DAMAGE_TAG);
        player.getPersistentData().remove(SnoutSkill.DASH_LEVEL_TAG);
    }
}
