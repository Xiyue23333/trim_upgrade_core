package com.xiyue.trimmod.common.trim.set.eye;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.network.ModMessages;
import com.xiyue.trimmod.network.PacketSyncEyeCharge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public final class EyeTick {
    public static final String CHARGE_TAG = PersistentDataKeys.EYE_CHARGE_COUNT;
    private static final UUID ATTACK_DAMAGE_UUID = UUID.fromString("5bafc7e6-ac02-4954-b8d5-1f514e6f40d1");

    public void tickCharge(ServerPlayer player, boolean hasFullSet) {
        int charges = player.getPersistentData().getInt(CHARGE_TAG);
        if (hasFullSet) {
            if (player.tickCount % 20 == 0 && charges < 100) {
                int newCharge = Math.min(100, charges + 10);
                player.getPersistentData().putInt(CHARGE_TAG, newCharge);
                ModMessages.sendToPlayer(new PacketSyncEyeCharge(newCharge), player);
            }
            return;
        }

        if (charges > 0) {
            player.getPersistentData().putInt(CHARGE_TAG, 0);
            ModMessages.sendToPlayer(new PacketSyncEyeCharge(0), player);
        }
    }

    public void applyAttackDamageBonus(ServerPlayer player, boolean hasFullSet, int minLevel) {
        var attack = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack == null) {
            return;
        }
        attack.removeModifier(ATTACK_DAMAGE_UUID);
        if (!hasFullSet) {
            return;
        }
        double bonusPercent = 0.06D + (minLevel * 0.025D);
        attack.addTransientModifier(new AttributeModifier(
                ATTACK_DAMAGE_UUID, "Eye Attack Bonus", bonusPercent, AttributeModifier.Operation.MULTIPLY_BASE));
    }
}


