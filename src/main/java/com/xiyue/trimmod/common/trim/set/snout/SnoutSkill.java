package com.xiyue.trimmod.common.trim.set.snout;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.common.util.TrimSkillCooldowns;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public final class SnoutSkill {
    private static final double DASH_SPEED = 0.6D; // Keep dash speed consistent across all levels (12 blocks/s).
    public static final String ENERGY_TAG = PersistentDataKeys.SNOUT_ENERGY;
    public static final String TICK_TAG = PersistentDataKeys.SNOUT_REGEN_TICKS;
    public static final String DASH_TICKS_TAG = "trimupgrade_snout_dash_ticks";
    public static final String DASH_MAX_TICKS_TAG = "trimupgrade_snout_dash_max_ticks";
    public static final String DASH_DIR_X_TAG = "trimupgrade_snout_dash_dir_x";
    public static final String DASH_DIR_Z_TAG = "trimupgrade_snout_dash_dir_z";
    public static final String DASH_SPEED_TAG = "trimupgrade_snout_dash_speed";
    public static final String DASH_DAMAGE_TAG = "trimupgrade_snout_dash_damage";
    public static final String DASH_LEVEL_TAG = "trimupgrade_snout_dash_level";
    public static final String DASH_ID_TAG = "trimupgrade_snout_dash_id";
    public static final String HIT_KEY_PREFIX = "trimupgrade_snout_hit_";

    public void activate(ServerPlayer player, int minLevel, Consumer<Component> actionBar) {
        if (TrimSkillCooldowns.isOnCooldown(player, TrimPatterns.SNOUT)) {
            return;
        }

        ServerLevel level = player.serverLevel();
        Vec3 look = player.getLookAngle();
        Vec3 horizontal = new Vec3(look.x, 0.0D, look.z);
        if (horizontal.lengthSqr() < 1.0E-6D) {
            horizontal = Vec3.directionFromRotation(0.0F, player.getYRot());
        } else {
            horizontal = horizontal.normalize();
        }

        int dashTicks = 20 + (minLevel * 4);
        double dashSpeed = DASH_SPEED;
        float dashDamage = 6.0f + (minLevel * 2.0f);
        int dashId = player.getPersistentData().getInt(DASH_ID_TAG) + 1;

        player.getPersistentData().putInt(DASH_TICKS_TAG, dashTicks);
        player.getPersistentData().putInt(DASH_MAX_TICKS_TAG, dashTicks);
        player.getPersistentData().putDouble(DASH_DIR_X_TAG, horizontal.x);
        player.getPersistentData().putDouble(DASH_DIR_Z_TAG, horizontal.z);
        player.getPersistentData().putDouble(DASH_SPEED_TAG, dashSpeed);
        player.getPersistentData().putFloat(DASH_DAMAGE_TAG, dashDamage);
        player.getPersistentData().putInt(DASH_LEVEL_TAG, minLevel);
        player.getPersistentData().putInt(DASH_ID_TAG, dashId);
        player.fallDistance = 0.0F;
        player.setSprinting(true);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.HOGLIN_ATTACK, SoundSource.PLAYERS, 1.0f, 0.85f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 0.7f, 1.15f);
        level.sendParticles(ParticleTypes.FLAME,
                player.getX(), player.getY() + 0.2D, player.getZ(),
                10, 0.35D, 0.08D, 0.35D, 0.02D);

        TrimSkillCooldowns.startCooldown(player, TrimPatterns.SNOUT, minLevel);
    }
}


