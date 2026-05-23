package com.xiyue.trimmod.common.trim.set.shaper;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.network.ModMessages;
import com.xiyue.trimmod.network.PacketSyncShaperEnergy;
import net.minecraft.server.level.ServerPlayer;

public final class ShaperTick {
    private static final String ENERGY_TAG = PersistentDataKeys.SHAPER_ENERGY;
    private static final String TICK_TAG = PersistentDataKeys.SHAPER_REGEN_TICKS;

    public void tickEnergy(ServerPlayer player, boolean hasFullSet) {
        if (hasFullSet && !player.getPersistentData().contains(ENERGY_TAG)) {
            player.getPersistentData().putInt(ENERGY_TAG, 100);
            player.getPersistentData().putInt(TICK_TAG, 0);
            ModMessages.sendToPlayer(new PacketSyncShaperEnergy(100), player);
        }

        int energy = player.getPersistentData().getInt(ENERGY_TAG);
        if (hasFullSet) {
            int fullRechargeTicks = Config.cooldownTicks(Config.cooldownShaperSeconds);
            if (fullRechargeTicks <= 0) {
                if (energy < 100) {
                    player.getPersistentData().putInt(ENERGY_TAG, 100);
                    player.getPersistentData().putInt(TICK_TAG, 0);
                    ModMessages.sendToPlayer(new PacketSyncShaperEnergy(100), player);
                }
            } else if (energy < 100) {
                int intervalTicks = Math.max(1, fullRechargeTicks / 5);
                int ticks = player.getPersistentData().getInt(TICK_TAG) + 1;
                if (ticks >= intervalTicks) {
                    int newEnergy = Math.min(100, energy + 20);
                    player.getPersistentData().putInt(ENERGY_TAG, newEnergy);
                    ModMessages.sendToPlayer(new PacketSyncShaperEnergy(newEnergy), player);
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
            ModMessages.sendToPlayer(new PacketSyncShaperEnergy(0), player);
        }
    }
}


