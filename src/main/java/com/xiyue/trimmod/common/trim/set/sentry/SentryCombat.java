package com.xiyue.trimmod.common.trim.set.sentry;

import com.xiyue.trimmod.common.util.TrimUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public final class SentryCombat {
    public void onRemoteDamage(LivingHurtEvent event) {
        Entity directEntity = event.getSource().getDirectEntity();
        Entity shooter = event.getSource().getEntity();
        if (!(shooter instanceof Player player)) {
            return;
        }

        boolean isRemote = directEntity instanceof AbstractArrow || directEntity instanceof ThrownTrident;
        if (!isRemote) {
            return;
        }

        int count = TrimUtils.getTrimCount(player, TrimPatterns.SENTRY);
        if (count < 4) {
            return;
        }

        int minLevel = TrimUtils.getMinUpgradeLevel(player);
        float multiplier = 1.16F + (minLevel * 0.04F);
        event.setAmount(event.getAmount() * multiplier);
    }
}
