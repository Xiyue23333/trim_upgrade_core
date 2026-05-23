package com.xiyue.trimmod.common.trim.set.swamp;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.common.util.TrimSkillCooldowns;
import com.xiyue.trimmod.core.init.EntityInit;
import com.xiyue.trimmod.entity.SwampThornEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;
import java.util.function.Consumer;

public final class  SwampSkill {
    private static final int DURATION_TICKS = 6 * 20;
    private static final int DAMAGE_INTERVAL_TICKS = 20;
    private static final int ROOT_DURATION_TICKS = 5 * 20;
    private static final int ROOT_KNOCKBACK_RESIST_DURATION_TICKS = 110; // 5.5 seconds
    private static final double DOMAIN_HEIGHT_HALF = 1.5D;
    private static final double DOMAIN_RADIUS = 5.0D;
    private static final UUID SWAMP_DOMAIN_ATTACK_SPEED_UUID = UUID.fromString("1f04bf2d-3b31-45af-93cf-5f624b0a6a4e");
    private static final UUID SWAMP_ROOT_KNOCKBACK_RESIST_UUID = UUID.fromString("2fa74dae-2831-4c7e-9b35-86d361e0b0f8");
    public static final String SWAMP_LAST_INSIDE_TICK_TAG = "trimupgrade_swamp_last_inside_tick";
    public static final String SWAMP_ROOT_UNTIL_TICK_TAG = "trimupgrade_swamp_root_until_tick";
    public static final String SWAMP_ROOT_KNOCKBACK_RESIST_UNTIL_TICK_TAG = "trimupgrade_swamp_root_kb_resist_until_tick";

    public void activate(ServerPlayer player, int minLevel, Consumer<Component> actionBarSender) {
        CompoundTag data = player.getPersistentData();
        if (data.getInt(PersistentDataKeys.SWAMP_DOMAIN_REMAINING_TICKS) > 0) {
            actionBarSender.accept(Component.translatable("actionbar.trimupgrade.swamp_skill_active"));
            return;
        }
        if (TrimSkillCooldowns.isSwampOnCooldown(player)) {
            return;
        }

        int clampedLevel = Math.max(0, Math.min(4, minLevel));
        data.putInt(PersistentDataKeys.SWAMP_DOMAIN_REMAINING_TICKS, DURATION_TICKS);
        data.putInt(PersistentDataKeys.SWAMP_DOMAIN_DAMAGE_TICKS, 0);
        data.putInt(PersistentDataKeys.SWAMP_DOMAIN_LEVEL, clampedLevel);
        data.putDouble(PersistentDataKeys.SWAMP_DOMAIN_CENTER_X, player.getX());
        data.putDouble(PersistentDataKeys.SWAMP_DOMAIN_CENTER_Y, player.getY());
        data.putDouble(PersistentDataKeys.SWAMP_DOMAIN_CENTER_Z, player.getZ());

        ServerLevel level = player.serverLevel();
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.MANGROVE_ROOTS_BREAK, SoundSource.PLAYERS, 1.1F, 0.9F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.MOSS_PLACE, SoundSource.PLAYERS, 1.0F, 0.8F);
        TrimSkillCooldowns.startSwampCooldown(player, clampedLevel);
    }

    public void tickActiveDomain(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        int remaining = data.getInt(PersistentDataKeys.SWAMP_DOMAIN_REMAINING_TICKS);
        if (remaining <= 0) {
            removeAttackSpeedBuff(player);
            clearDomainState(data);
            return;
        }

        int minLevel = Math.max(0, Math.min(4, data.getInt(PersistentDataKeys.SWAMP_DOMAIN_LEVEL)));
        Vec3 center = new Vec3(
                data.getDouble(PersistentDataKeys.SWAMP_DOMAIN_CENTER_X),
                data.getDouble(PersistentDataKeys.SWAMP_DOMAIN_CENTER_Y),
                data.getDouble(PersistentDataKeys.SWAMP_DOMAIN_CENTER_Z)
        );

        boolean damageTick = false;
        int damageTicks = data.getInt(PersistentDataKeys.SWAMP_DOMAIN_DAMAGE_TICKS) + 1;
        if (damageTicks >= DAMAGE_INTERVAL_TICKS) {
            damageTick = true;
            damageTicks = 0;
        }
        data.putInt(PersistentDataKeys.SWAMP_DOMAIN_DAMAGE_TICKS, damageTicks);

        ServerLevel level = player.serverLevel();
        renderDomainParticles(level, center, remaining);
        rootAndDamageTargets(level, player, center, minLevel, damageTick);
        applyOrRemoveAttackSpeed(player, center, minLevel);

        remaining--;
        data.putInt(PersistentDataKeys.SWAMP_DOMAIN_REMAINING_TICKS, remaining);
        if (remaining <= 0) {
            removeAttackSpeedBuff(player);
            clearDomainState(data);
        }
    }

    private void rootAndDamageTargets(ServerLevel level, ServerPlayer owner, Vec3 center, int minLevel, boolean damageTick) {
        float damage = (float) (2.0D + 1.0D * minLevel);
        long now = level.getGameTime();
        AABB area = new AABB(center.x, center.y, center.z, center.x, center.y, center.z).inflate(DOMAIN_RADIUS, DOMAIN_HEIGHT_HALF, DOMAIN_RADIUS);
        level.getEntitiesOfClass(LivingEntity.class, area, target -> target != owner && target.isAlive() && !(target instanceof Player)).forEach(target -> {
            double dx = target.getX() - center.x;
            double dz = target.getZ() - center.z;
            if ((dx * dx) + (dz * dz) > DOMAIN_RADIUS * DOMAIN_RADIUS) {
                return;
            }
            if (target.getY() < center.y - DOMAIN_HEIGHT_HALF || target.getY() > center.y + DOMAIN_HEIGHT_HALF) {
                return;
            }

            if (canBeRooted(target)) {
                CompoundTag targetData = target.getPersistentData();
                long lastInsideTick = targetData.getLong(SWAMP_LAST_INSIDE_TICK_TAG);
                if (now - lastInsideTick > 1L) {
                    // Entering the domain triggers a fixed 6-second root duration.
                    targetData.putLong(SWAMP_ROOT_UNTIL_TICK_TAG, now + ROOT_DURATION_TICKS);
                    targetData.putLong(SWAMP_ROOT_KNOCKBACK_RESIST_UNTIL_TICK_TAG, now + ROOT_KNOCKBACK_RESIST_DURATION_TICKS);
                    ensureThornVisual(level, target, ROOT_DURATION_TICKS);
                }
                targetData.putLong(SWAMP_LAST_INSIDE_TICK_TAG, now);

                long rootUntil = targetData.getLong(SWAMP_ROOT_UNTIL_TICK_TAG);
                if (rootUntil > now) {
                    int remaining = (int) Math.min(Integer.MAX_VALUE, rootUntil - now);
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, Math.max(2, remaining), 8, true, false, true));
                    long kbResistUntil = targetData.getLong(SWAMP_ROOT_KNOCKBACK_RESIST_UNTIL_TICK_TAG);
                    applyOrRemoveRootKnockbackResistance(target, now, kbResistUntil);
                    target.setDeltaMovement(0.0D, Math.min(target.getDeltaMovement().y, 0.0D), 0.0D);
                    target.hurtMarked = true;
                } else {
                    removeRootKnockbackResistance(target);
                }
            }

            if (damageTick) {
                target.hurt(owner.damageSources().magic(), damage);
                level.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, target.getX(), target.getY() + 1.0D, target.getZ(), 10, 0.25D, 0.35D, 0.25D, 0.02D);
                level.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1.0D, target.getZ(), 6, 0.2D, 0.2D, 0.2D, 0.01D);
            }
        });
    }

    private boolean canBeRooted(LivingEntity target) {
        return !(target instanceof EnderDragon) && !(target instanceof WitherBoss);
    }

    private void applyOrRemoveRootKnockbackResistance(LivingEntity target, long now, long kbResistUntil) {
        AttributeInstance knockbackResist = target.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (knockbackResist == null) {
            return;
        }

        if (kbResistUntil > now) {
            knockbackResist.removeModifier(SWAMP_ROOT_KNOCKBACK_RESIST_UUID);
            knockbackResist.addTransientModifier(new AttributeModifier(
                    SWAMP_ROOT_KNOCKBACK_RESIST_UUID,
                    "Swamp Root Knockback Resistance",
                    1.0D,
                    AttributeModifier.Operation.ADDITION
            ));
        } else {
            knockbackResist.removeModifier(SWAMP_ROOT_KNOCKBACK_RESIST_UUID);
        }
    }

    private void removeRootKnockbackResistance(LivingEntity target) {
        AttributeInstance knockbackResist = target.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (knockbackResist != null) {
            knockbackResist.removeModifier(SWAMP_ROOT_KNOCKBACK_RESIST_UUID);
        }
    }

    private void ensureThornVisual(ServerLevel level, LivingEntity target, int lifeTicks) {
        AABB check = target.getBoundingBox().inflate(0.6D);
        boolean exists = !level.getEntitiesOfClass(SwampThornEntity.class, check,
                thorn -> thorn.isAlive() && thorn.isBoundTo(target)).isEmpty();
        if (exists) {
            return;
        }

        SwampThornEntity thorn = EntityInit.SWAMP_THORN.get().create(level);
        if (thorn == null) {
            return;
        }

        thorn.setPos(target.getX(), target.getY(), target.getZ());
        thorn.setYRot(target.getYRot());
        thorn.setLife(Math.max(1, lifeTicks));
        thorn.setBoundTarget(target);
        level.addFreshEntity(thorn);
    }

    public static boolean isEntitySwampRooted(LivingEntity entity) {
        if (entity == null || !entity.isAlive()) {
            return false;
        }
        long now = entity.level().getGameTime();
        return entity.getPersistentData().getLong(SWAMP_ROOT_UNTIL_TICK_TAG) > now;
    }

    private void applyOrRemoveAttackSpeed(ServerPlayer player, Vec3 center, int minLevel) {
        double dx = player.getX() - center.x;
        double dz = player.getZ() - center.z;
        boolean inside = (dx * dx) + (dz * dz) <= DOMAIN_RADIUS * DOMAIN_RADIUS;
        if (!inside) {
            removeAttackSpeedBuff(player);
            return;
        }

        double bonus = 0.12D + 0.03D * minLevel;
        AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeed == null) {
            return;
        }
        attackSpeed.removeModifier(SWAMP_DOMAIN_ATTACK_SPEED_UUID);
        attackSpeed.addTransientModifier(new AttributeModifier(
                SWAMP_DOMAIN_ATTACK_SPEED_UUID,
                "Swamp Domain Attack Speed",
                bonus,
                AttributeModifier.Operation.MULTIPLY_BASE
        ));
    }

    private void removeAttackSpeedBuff(ServerPlayer player) {
        AttributeInstance attackSpeed = player.getAttribute(Attributes.ATTACK_SPEED);
        if (attackSpeed != null) {
            attackSpeed.removeModifier(SWAMP_DOMAIN_ATTACK_SPEED_UUID);
        }
    }

    private void renderDomainParticles(ServerLevel level, Vec3 center, int remainingTicks) {
        for (int i = 0; i < 72; i++) {
            double angle = Math.PI * 2.0D * i / 72.0D;
            double x = center.x + Math.cos(angle) * DOMAIN_RADIUS;
            double z = center.z + Math.sin(angle) * DOMAIN_RADIUS;
            level.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, x, center.y + 0.35D, z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    private void clearDomainState(CompoundTag data) {
        data.remove(PersistentDataKeys.SWAMP_DOMAIN_REMAINING_TICKS);
        data.remove(PersistentDataKeys.SWAMP_DOMAIN_DAMAGE_TICKS);
        data.remove(PersistentDataKeys.SWAMP_DOMAIN_LEVEL);
        data.remove(PersistentDataKeys.SWAMP_DOMAIN_CENTER_X);
        data.remove(PersistentDataKeys.SWAMP_DOMAIN_CENTER_Y);
        data.remove(PersistentDataKeys.SWAMP_DOMAIN_CENTER_Z);
    }
}
