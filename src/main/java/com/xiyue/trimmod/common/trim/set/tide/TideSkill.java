package com.xiyue.trimmod.common.trim.set.tide;

import com.xiyue.trimmod.common.registry.ModParticles;
import com.xiyue.trimmod.common.util.TrimSkillCooldowns;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public final class TideSkill {
    public static final String ENERGY_TAG = "tide_energy";
    public static final String ACTIVE_TICKS_KEY = "trimupgrade_tide_active_ticks";
    public static final String NEXT_PULSE_TICKS_KEY = "trimupgrade_tide_next_pulse_ticks";
    public static final String PULSE_TICKS_KEY = "trimupgrade_tide_pulse_ticks";
    public static final String PULSE_ID_KEY = "trimupgrade_tide_pulse_id";
    public static final String TARGET_HIT_KEY_PREFIX = "trimupgrade_tide_hit_";
    private static final int SKILL_PULSE_INTERVAL_TICKS = 20;
    private static final int SKILL_PULSE_DURATION_TICKS = 12;

    public void activate(ServerPlayer player, Consumer<Component> actionBar, Runnable clearActionBar) {
        if (player.getPersistentData().getInt(ACTIVE_TICKS_KEY) > 0) {
            actionBar.accept(Component.translatable("actionbar.trimupgrade.tide_skill_active"));
            return;
        }
        if (TrimSkillCooldowns.isOnCooldown(player, TrimPatterns.TIDE)) {
            return;
        }

        ServerLevel level = player.serverLevel();
        player.getPersistentData().putInt(ACTIVE_TICKS_KEY, 120);
        player.getPersistentData().putInt(NEXT_PULSE_TICKS_KEY, 0);
        player.getPersistentData().putInt(PULSE_TICKS_KEY, 0);
        player.getPersistentData().putInt(PULSE_ID_KEY, 0);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.35f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.45f, 1.6f);
        TrimSkillCooldowns.startCooldown(player, TrimPatterns.TIDE, 0);
        clearActionBar.run();
    }

    public void tickActiveSkill(ServerPlayer player, int minLevel) {
        int activeTicks = player.getPersistentData().getInt(ACTIVE_TICKS_KEY);
        if (activeTicks <= 0) {
            return;
        }
        ServerLevel level = player.serverLevel();
        renderOrbit(level, player);

        int nextPulseTicks = player.getPersistentData().getInt(NEXT_PULSE_TICKS_KEY);
        if (nextPulseTicks > 0) {
            nextPulseTicks--;
        }
        if (nextPulseTicks <= 0) {
            int nextPulseId = player.getPersistentData().getInt(PULSE_ID_KEY) + 1;
            player.getPersistentData().putInt(PULSE_ID_KEY, nextPulseId);
            player.getPersistentData().putInt(PULSE_TICKS_KEY, 1);
            nextPulseTicks = SKILL_PULSE_INTERVAL_TICKS;
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.15f, 1.75f);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 0.85f, 1.35f);
        }
        player.getPersistentData().putInt(NEXT_PULSE_TICKS_KEY, nextPulseTicks);

        int pulseTicks = player.getPersistentData().getInt(PULSE_TICKS_KEY);
        if (pulseTicks > 0) {
            renderPulse(level, player, minLevel, pulseTicks, player.getPersistentData().getInt(PULSE_ID_KEY));
            pulseTicks++;
            if (pulseTicks > SKILL_PULSE_DURATION_TICKS) {
                pulseTicks = 0;
            }
            player.getPersistentData().putInt(PULSE_TICKS_KEY, pulseTicks);
        }

        activeTicks--;
        player.getPersistentData().putInt(ACTIVE_TICKS_KEY, activeTicks);
        if (activeTicks <= 0) {
            clearActiveSkill(player);
        }
    }

    public void clearActiveSkill(ServerPlayer player) {
        player.getPersistentData().remove(ACTIVE_TICKS_KEY);
        player.getPersistentData().remove(NEXT_PULSE_TICKS_KEY);
        player.getPersistentData().remove(PULSE_TICKS_KEY);
        player.getPersistentData().remove(PULSE_ID_KEY);
    }

    private void renderOrbit(ServerLevel level, ServerPlayer player) {
        double time = level.getGameTime() * 0.112D;
        double radius = 1.05D;
        double topYOffset = 1.5D;
        double bottomYOffset = 0.12D;
        int orbitCount = 12;
        double angleStep = Math.PI * 2.0D / orbitCount;
        for (int starIndex = 0; starIndex < orbitCount; starIndex++) {
            double t = starIndex / (double) (orbitCount - 1);
            double y = player.getY() + topYOffset - (topYOffset - bottomYOffset) * t;
            double direction = starIndex % 2 == 0 ? 1.0D : -1.0D;
            double phase = angleStep * starIndex + (starIndex % 2 == 0 ? 0.0D : angleStep * 0.5D);
            double angle = phase + time * direction;
            double x = player.getX() + Math.cos(angle) * radius;
            double z = player.getZ() + Math.sin(angle) * radius;
            level.sendParticles(ModParticles.TIDE_STAR.get(), x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            for (int tail = 1; tail <= 4; tail++) {
                double tailAngle = angle - direction * tail * 0.18D;
                double tx = player.getX() + Math.cos(tailAngle) * radius;
                double tz = player.getZ() + Math.sin(tailAngle) * radius;
                level.sendParticles(ModParticles.TIDE_STAR_TRAIL.get(), tx, y, tz, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private void renderPulse(ServerLevel level, ServerPlayer player, int minLevel, int pulseTicks, int pulseId) {
        double maxRadius = 4.5D + (minLevel * 0.35D);
        double radius = maxRadius * pulseTicks / (double) SKILL_PULSE_DURATION_TICKS;
        float damage = (float) (4.0D + minLevel * 1.5D);
        int directionCount = 12;
        double angleStep = Math.PI * 2.0D / directionCount;
        int waterRingCount = 180;
        double y = player.getY() + 0.75D;

        for (int i = 0; i < waterRingCount; i++) {
            double angle = Math.PI * 2.0D * i / waterRingCount;
            double x = player.getX() + Math.cos(angle) * radius;
            double z = player.getZ() + Math.sin(angle) * radius;
            level.sendParticles(ParticleTypes.BUBBLE_POP, x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        for (int starIndex = 0; starIndex < directionCount; starIndex++) {
            double angle = angleStep * starIndex;
            double x = player.getX() + Math.cos(angle) * radius;
            double z = player.getZ() + Math.sin(angle) * radius;
            level.sendParticles(ModParticles.NIGHT_STAR.get(), x, y, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            for (int tail = 1; tail <= 2; tail++) {
                double tailRadius = Math.max(0.0D, radius - tail * 0.34D);
                double tx = player.getX() + Math.cos(angle) * tailRadius;
                double tz = player.getZ() + Math.sin(angle) * tailRadius;
                level.sendParticles(ModParticles.TIDE_STAR_TRAIL.get(), tx, y, tz, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }

        AABB area = player.getBoundingBox().inflate(maxRadius + 1.0D, 2.5D, maxRadius + 1.0D);
        String hitKey = TARGET_HIT_KEY_PREFIX + player.getStringUUID();
        level.getEntitiesOfClass(LivingEntity.class, area, entity -> entity != player && entity.isAlive()).forEach(target -> {
            double dx = target.getX() - player.getX();
            double dz = target.getZ() - player.getZ();
            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
            if (horizontalDistance > radius || Math.abs(target.getY() - player.getY()) > 2.5D) {
                return;
            }
            if (target.getPersistentData().getInt(hitKey) == pulseId) {
                return;
            }
            target.getPersistentData().putInt(hitKey, pulseId);
            target.hurt(player.damageSources().magic(), damage);
            Vec3 direction = horizontalDistance > 1.0E-4D
                    ? new Vec3(dx / horizontalDistance, 0.0D, dz / horizontalDistance)
                    : player.getLookAngle().normalize();
            double pushScale = (0.65D + (minLevel * 0.08D)) * 1.2D;
            target.setDeltaMovement(target.getDeltaMovement().add(direction.x * pushScale, 0.18D + minLevel * 0.03D, direction.z * pushScale));
            target.hurtMarked = true;
        });
    }
}
