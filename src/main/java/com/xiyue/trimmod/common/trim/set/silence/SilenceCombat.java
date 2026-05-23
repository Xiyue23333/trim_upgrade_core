package com.xiyue.trimmod.common.trim.set.silence;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.common.util.TrimUtils;
import com.xiyue.trimmod.network.ModMessages;
import com.xiyue.trimmod.network.PacketSyncSilenceCharge;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public final class SilenceCombat {
    private static final long CHARGE_INTERVAL_TICKS = 60L; // 3 seconds

    public void onEffectHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (TrimUtils.getTrimCount(player, TrimPatterns.SILENCE) != 4) {
            return;
        }

        int charges = player.getPersistentData().getInt(SilenceSkill.CHARGE_TAG);
        if (charges > 0) {
            float reduction = charges * 0.075f;
            event.setAmount(event.getAmount() * (1 - reduction));
        }

        if (charges >= 4) {
            return;
        }

        long now = player.level().getGameTime();
        long lastChargeTick = player.getPersistentData().getLong(PersistentDataKeys.SILENCE_LAST_CHARGE_TICK);
        if (now - lastChargeTick < CHARGE_INTERVAL_TICKS) {
            return;
        }

        int next = charges + 1;
        player.getPersistentData().putInt(SilenceSkill.CHARGE_TAG, next);
        player.getPersistentData().putLong(PersistentDataKeys.SILENCE_LAST_CHARGE_TICK, now);
        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            ModMessages.sendToPlayer(new PacketSyncSilenceCharge(next), serverPlayer);
        }
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SCULK_BLOCK_CHARGE, SoundSource.PLAYERS, 0.5F, 0.8F + next * 0.2F);
    }
}
