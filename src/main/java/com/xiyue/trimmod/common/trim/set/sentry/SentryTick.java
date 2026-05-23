package com.xiyue.trimmod.common.trim.set.sentry;

import com.xiyue.trimmod.core.init.EntityInit;
import com.xiyue.trimmod.entity.SentryFloatingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.UUID;

public final class SentryTick {
    public void handleFloatingEntity(Player player, int sentryCount, int minSentryLevel) {
        if (player.level() == null || player.level().isClientSide) {
            return;
        }

        List<SentryFloatingEntity> sentries = player.level().getEntitiesOfClass(
                SentryFloatingEntity.class,
                player.getBoundingBox().inflate(64),
                entity -> {
                    UUID ownerUUID = entity.getOwnerUUID();
                    return ownerUUID != null && ownerUUID.equals(player.getUUID());
                }
        );

        if (sentryCount >= 4) {
            if (sentries.isEmpty()) {
                SentryFloatingEntity sentry = new SentryFloatingEntity(EntityInit.SENTRY_FLOATING.get(), player.level());
                sentry.setOwner(player);
                sentry.setSentryLevel(minSentryLevel);
                sentry.setPos(player.getX(), player.getY() + 2.0, player.getZ());
                player.level().addFreshEntity(sentry);
            } else {
                for (SentryFloatingEntity sentry : sentries) {
                    if (sentry.getSentryLevel() != minSentryLevel) {
                        sentry.setSentryLevel(minSentryLevel);
                    }
                }
            }
            return;
        }

        for (SentryFloatingEntity sentry : sentries) {
            sentry.discard();
        }
    }
}
