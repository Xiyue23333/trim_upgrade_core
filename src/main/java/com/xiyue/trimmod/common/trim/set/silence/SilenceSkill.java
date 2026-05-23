package com.xiyue.trimmod.common.trim.set.silence;

import com.xiyue.trimmod.common.util.HighlightUtils;
import com.xiyue.trimmod.common.util.MarkUtils;
import com.xiyue.trimmod.network.ModMessages;
import com.xiyue.trimmod.network.PacketSyncSilenceCharge;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public final class SilenceSkill {
    public static final String CHARGE_TAG = "silence_charge_count";

    public void activate(ServerPlayer player, int minLevel, Consumer<Component> actionBar) {
        int charges = player.getPersistentData().getInt(CHARGE_TAG);
        if (charges < 4) {
            actionBar.accept(Component.translatable("actionbar.trimupgrade.silence_energy_not_enough", 4));
            return;
        }

        if (player.isShiftKeyDown()) {
            executeSilenceField(player, minLevel);
        } else {
            executeSonicBoom(player, minLevel);
        }

        player.getPersistentData().putInt(CHARGE_TAG, 0);
        ModMessages.sendToPlayer(new PacketSyncSilenceCharge(0), player);
    }

    private void executeSilenceField(ServerPlayer player, int minLevel) {
        ServerLevel level = player.serverLevel();
        double radius = 3.0D + minLevel;

        for (double angle = 0.0D; angle <= Math.PI * 2.0D; angle += Math.PI / 8.0D) {
            double xOffset = Math.cos(angle) * radius;
            double zOffset = Math.sin(angle) * radius;
            level.sendParticles(ParticleTypes.SONIC_BOOM, player.getX() + xOffset, player.getY() + 1.0D, player.getZ() + zOffset,
                    1, 0, 0, 0, 0);
            level.sendParticles(ParticleTypes.SCULK_SOUL, player.getX() + xOffset * 0.5D, player.getY() + 0.1D, player.getZ() + zOffset * 0.5D,
                    2, 0.1D, 0.1D, 0.1D, 0.02D);
        }

        level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(radius)).forEach(target -> {
            if (target == player) {
                return;
            }
            target.hurt(player.damageSources().magic(), player.getArmorValue() * (0.35f + minLevel * 0.1f));
            double slow = -(0.45D + (minLevel * 0.05D));
            MarkUtils.applySilenceHighlightSlow(target, level, slow, 100);
            target.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0));
            level.sendParticles(ParticleTypes.SCULK_CHARGE_POP, target.getX(), target.getY() + 1.0D, target.getZ(), 10, 0.2D, 0.2D, 0.2D, 0.05D);
        });

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 2.0F, 0.5F);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_ROAR, SoundSource.PLAYERS, 1.5F, 1.0F);
    }

    private void executeSonicBoom(ServerPlayer player, int minLevel) {
        ServerLevel level = player.serverLevel();
        float damage = player.getArmorValue() * (0.25f + minLevel * 0.075f);
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0F);
        double maxRange = 16.0D;
        Vec3 endPos = eyePos.add(lookVec.scale(maxRange));
        double step = 0.8D;

        for (double d = 0.0D; d < maxRange; d += step) {
            Vec3 pos = eyePos.add(lookVec.scale(d));
            level.sendParticles(ParticleTypes.SONIC_BOOM, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
        }
        level.sendParticles(ParticleTypes.SONIC_BOOM, endPos.x, endPos.y, endPos.z, 1, 0, 0, 0, 0);

        AABB beamArea = new AABB(eyePos, endPos).inflate(1.2D);
        level.getEntitiesOfClass(LivingEntity.class, beamArea, e -> e != player).forEach(target -> {
            if (!target.getBoundingBox().inflate(0.5D).clip(eyePos, endPos).isPresent()) {
                return;
            }
            target.hurt(player.damageSources().magic(), damage);
            HighlightUtils.applyColoredHighlight(target, level, "tu_hl_si", ChatFormatting.DARK_AQUA, 100);
            double slow = -(0.45D + (minLevel * 0.05D));
            MarkUtils.applySilenceHighlightSlow(target, level, slow, 100);
            level.sendParticles(ParticleTypes.SONIC_BOOM, target.getX(), target.getY() + 1.0D, target.getZ(), 1, 0, 0, 0, 0);
        });

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 2.0F, 1.0F);
    }
}
