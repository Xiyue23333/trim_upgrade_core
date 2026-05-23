package com.xiyue.trimmod.common.trim.set.coast;

import com.xiyue.trimmod.common.util.TrimSkillCooldowns;
import com.xiyue.trimmod.core.init.EntityInit;
import com.xiyue.trimmod.entity.CoastWaveEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.armortrim.TrimPatterns;

import java.util.function.Consumer;

public final class CoastSkill {
    public void activate(ServerPlayer player, int minLevel, Consumer<Component> actionBar) {
        if (TrimSkillCooldowns.isOnCooldown(player, TrimPatterns.COAST)) {
            return;
        }

        ServerLevel level = player.serverLevel();
        float damage = 12.0f + (minLevel * 3.0f);
        int slowDuration = (5 + (minLevel * 2)) * 20;

        CoastWaveEntity wave = new CoastWaveEntity(EntityInit.COAST_WAVE.get(), level, player, damage, slowDuration);
        level.addFreshEntity(wave);

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_RIPTIDE_3, SoundSource.PLAYERS, 1.0f, 0.8f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_SPLASH, SoundSource.PLAYERS, 0.5f, 1.2f);
        TrimSkillCooldowns.startCooldown(player, TrimPatterns.COAST, minLevel);
    }
}


