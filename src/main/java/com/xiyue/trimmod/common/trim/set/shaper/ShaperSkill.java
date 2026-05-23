package com.xiyue.trimmod.common.trim.set.shaper;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.network.ModMessages;
import com.xiyue.trimmod.network.PacketSyncShaperEnergy;
import com.xiyue.trimmod.network.PacketSyncShaperSkillTime;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.function.Consumer;

public final class ShaperSkill {
    public void activate(ServerPlayer player, int level, Consumer<Component> actionBar) {
        if (player.getPersistentData().getInt(PersistentDataKeys.IRON_WALL_TIMER) > 0) {
            return;
        }

        int energy = player.getPersistentData().getInt(PersistentDataKeys.SHAPER_ENERGY);
        if (energy < 100) {
            return;
        }

        player.getPersistentData().putInt(PersistentDataKeys.SHAPER_ENERGY, 0);
        player.getPersistentData().putInt(PersistentDataKeys.SHAPER_REGEN_TICKS, 0);
        ModMessages.sendToPlayer(new PacketSyncShaperEnergy(0), player);

        int duration = 240 + (level * 40);
        player.getPersistentData().putInt(PersistentDataKeys.IRON_WALL_TIMER, duration);
        player.getPersistentData().putInt(PersistentDataKeys.IRON_WALL_MAX_TIMER, duration);
        ModMessages.sendToPlayer(new PacketSyncShaperSkillTime(duration, duration), player);

        ServerLevel levelObj = player.serverLevel();
        for (int i = 0; i < 360; i += 15) {
            double rad = Math.toRadians(i);
            levelObj.sendParticles(ParticleTypes.CLOUD,
                    player.getX() + Math.cos(rad) * 0.5D,
                    player.getY() + 0.1D,
                    player.getZ() + Math.sin(rad) * 0.5D,
                    1, 0, 0, 0, 0.05D);
        }
        levelObj.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ANVIL_USE, SoundSource.PLAYERS, 0.8f, 1.2f);
    }
}


