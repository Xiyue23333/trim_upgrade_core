package com.xiyue.trimmod.common.trim.set.vex;

import com.xiyue.trimmod.common.util.TrimSkillCooldowns;
import com.xiyue.trimmod.core.init.ModEffects;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.armortrim.TrimPatterns;

import java.util.function.Consumer;

public final class VexSkill {
    public void activate(ServerPlayer player, int minLevel, Consumer<Component> actionBar) {
        if (TrimSkillCooldowns.isOnCooldown(player, TrimPatterns.VEX)) {
            return;
        }
        int xpCost = 400 + (minLevel * 150);
        if (player.totalExperience < xpCost) {
            actionBar.accept(Component.translatable("actionbar.trimupgrade.vex_not_enough_xp", xpCost));
            player.playSound(SoundEvents.VILLAGER_NO, 1.0f, 1.0f);
            return;
        }
        player.giveExperiencePoints(-xpCost);
        int amplifier = Math.max(0, Math.min(minLevel, 4));
        int durationTicks = (12 + (minLevel * 2)) * 20;
        player.addEffect(new MobEffectInstance(ModEffects.SOUL_SACRIFICE.get(), durationTicks, amplifier, false, true, true));
        ServerLevel level = player.serverLevel();
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.VEX_CHARGE, SoundSource.PLAYERS, 1.2f, 0.5f);
        level.sendParticles(ParticleTypes.SOUL, player.getX(), player.getY() + 1, player.getZ(), 30, 0.5, 0.5, 0.5, 0.05);
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, player.getX(), player.getY() + 1, player.getZ(), 20, 0.4, 0.5, 0.4, 0.05);
        TrimSkillCooldowns.startCooldown(player, TrimPatterns.VEX, minLevel);
    }
}


