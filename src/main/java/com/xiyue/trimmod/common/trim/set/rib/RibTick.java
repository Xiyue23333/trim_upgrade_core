package com.xiyue.trimmod.common.trim.set.rib;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.network.ModMessages;
import com.xiyue.trimmod.network.PacketSyncRibCharge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public final class RibTick {
    public static final String CHARGE_TAG = PersistentDataKeys.RIB_CHARGE_COUNT;
    private static final UUID RIB_DAMAGE_UUID = UUID.fromString("f470a16c-3832-443b-8c88-29479634d31d");

    public void applyAttackBonus(ServerPlayer player, boolean hasFullSet) {
        var attack = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack == null) {
            return;
        }

        attack.removeModifier(RIB_DAMAGE_UUID);
        if (hasFullSet) {
            int charges = player.getPersistentData().getInt(CHARGE_TAG);
            if (charges > 0) {
                attack.addTransientModifier(new AttributeModifier(
                        RIB_DAMAGE_UUID, "Rib Attack Bonus", charges * 0.035D, AttributeModifier.Operation.MULTIPLY_BASE));
            }
            return;
        }

        int charges = player.getPersistentData().getInt(CHARGE_TAG);
        if (charges > 0) {
            player.getPersistentData().putInt(CHARGE_TAG, 0);
            ModMessages.sendToPlayer(new PacketSyncRibCharge(0), player);
        }
    }

    public void onKill(ServerPlayer player, int minLevel) {
        player.heal(1.0F + (0.5F * minLevel));
        int current = player.getPersistentData().getInt(CHARGE_TAG);
        if (current < 8) {
            int next = current + 1;
            player.getPersistentData().putInt(CHARGE_TAG, next);
            ModMessages.sendToPlayer(new PacketSyncRibCharge(next), player);
        }
    }
}


