package com.xiyue.trimmod.common.trim.set.coast;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.network.ModMessages;
import com.xiyue.trimmod.network.PacketSyncCoastEnergy;
import net.minecraft.server.level.ServerPlayer;

public final class CoastTick {
    private static final String ENERGY_TAG = PersistentDataKeys.COAST_ENERGY;
    private static final String TICK_TAG = PersistentDataKeys.COAST_REGEN_TICKS;

    public void tickEnergy(ServerPlayer player, boolean hasFullSet) {
        if (hasFullSet && !player.getPersistentData().contains(ENERGY_TAG)) {
            player.getPersistentData().putInt(ENERGY_TAG, 100);
            player.getPersistentData().putInt(TICK_TAG, 0);
            ModMessages.sendToPlayer(new PacketSyncCoastEnergy(100), player);
        }

        int energy = player.getPersistentData().getInt(ENERGY_TAG);
        if (hasFullSet) {
            int fullRechargeTicks = Config.cooldownTicks(Config.cooldownCoastSeconds);
            if (fullRechargeTicks <= 0) {
                if (energy < 100) {
                    player.getPersistentData().putInt(ENERGY_TAG, 100);
                    player.getPersistentData().putInt(TICK_TAG, 0);
                    ModMessages.sendToPlayer(new PacketSyncCoastEnergy(100), player);
                }
            } else if (energy < 100) {
                int ticks = player.getPersistentData().getInt(TICK_TAG) + 1;
                int newEnergy = Math.min(100, (ticks * 100) / fullRechargeTicks);
                if (newEnergy != energy) {
                    player.getPersistentData().putInt(ENERGY_TAG, newEnergy);
                    ModMessages.sendToPlayer(new PacketSyncCoastEnergy(newEnergy), player);
                }
                if (newEnergy >= 100) {
                    ticks = 0;
                }
                player.getPersistentData().putInt(TICK_TAG, ticks);
            } else if (player.getPersistentData().getInt(TICK_TAG) > 0) {
                player.getPersistentData().putInt(TICK_TAG, 0);
            }
            return;
        }

        if (energy > 0 || player.getPersistentData().getInt(TICK_TAG) > 0) {
            player.getPersistentData().putInt(ENERGY_TAG, 0);
            player.getPersistentData().putInt(TICK_TAG, 0);
            ModMessages.sendToPlayer(new PacketSyncCoastEnergy(0), player);
        }
    }
}


