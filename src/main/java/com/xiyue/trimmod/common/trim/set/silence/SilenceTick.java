package com.xiyue.trimmod.common.trim.set.silence;

import com.xiyue.trimmod.network.ModMessages;
import com.xiyue.trimmod.network.PacketSyncSilenceCharge;
import net.minecraft.server.level.ServerPlayer;

public final class SilenceTick {
    public void syncResetIfNeeded(ServerPlayer player, boolean hasFullSet) {
        int charges = player.getPersistentData().getInt(SilenceSkill.CHARGE_TAG);
        if (hasFullSet || charges <= 0) {
            return;
        }
        player.getPersistentData().putInt(SilenceSkill.CHARGE_TAG, 0);
        ModMessages.sendToPlayer(new PacketSyncSilenceCharge(0), player);
    }
}
