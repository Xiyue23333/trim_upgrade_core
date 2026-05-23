package com.xiyue.trimmod.common.trim.set.dune;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.network.ModMessages;
import com.xiyue.trimmod.network.PacketSyncDuneEnergy;
import net.minecraft.server.level.ServerPlayer;

public final class DuneTick {
    public static final String ENERGY_TAG = PersistentDataKeys.DUNE_ENERGY;
    public static final String TICK_TAG = PersistentDataKeys.DUNE_REGEN_TICKS;

    public void tick(ServerPlayer player, boolean hasFullSet) {
        if (hasFullSet && !player.getPersistentData().contains(ENERGY_TAG)) {
            player.getPersistentData().putInt(ENERGY_TAG, 100);
            player.getPersistentData().putInt(TICK_TAG, 0);
            ModMessages.sendToPlayer(new PacketSyncDuneEnergy(100), player);
        }

        int energy = player.getPersistentData().getInt(ENERGY_TAG);
        if (hasFullSet) {
            int fullRechargeTicks = Config.cooldownTicks(Config.cooldownDuneSeconds);
            if (fullRechargeTicks <= 0) {
                if (energy < 100) {
                    player.getPersistentData().putInt(ENERGY_TAG, 100);
                    player.getPersistentData().putInt(TICK_TAG, 0);
                    ModMessages.sendToPlayer(new PacketSyncDuneEnergy(100), player);
                }
            } else if (energy < 100) {
                int ticks = player.getPersistentData().getInt(TICK_TAG) + 1;
                int newEnergy = Math.min(100, (ticks * 100) / fullRechargeTicks);
                if (newEnergy != energy) {
                    player.getPersistentData().putInt(ENERGY_TAG, newEnergy);
                    ModMessages.sendToPlayer(new PacketSyncDuneEnergy(newEnergy), player);
                }
                if (newEnergy >= 100) {
                    ticks = 0;
                }
                player.getPersistentData().putInt(TICK_TAG, ticks);
            } else if (player.getPersistentData().getInt(TICK_TAG) > 0) {
                player.getPersistentData().putInt(TICK_TAG, 0);
            }
        } else if (energy > 0 || player.getPersistentData().getInt(TICK_TAG) > 0) {
            player.getPersistentData().putInt(ENERGY_TAG, 0);
            player.getPersistentData().putInt(TICK_TAG, 0);
            ModMessages.sendToPlayer(new PacketSyncDuneEnergy(0), player);
        }
    }
}


