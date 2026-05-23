package com.xiyue.trimmod.common.trim.set.wayfinder;

import com.xiyue.trimmod.common.util.HighlightUtils;
import com.xiyue.trimmod.common.util.MarkUtils;
import com.xiyue.trimmod.common.util.TrimSkillCooldowns;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.phys.AABB;

import java.util.function.Consumer;

public final class WayfinderSkill {
    public void activate(ServerPlayer player, int minLevel, Consumer<Component> actionBar) {
        if (TrimSkillCooldowns.isOnCooldown(player, TrimPatterns.WAYFINDER)) {
            return;
        }

        double radius = 20.0D + (minLevel * 2.5D);
        int durationTicks = 200 + (minLevel * 50);
        double bonusPct = 0.10D + (Math.max(0, Math.min(minLevel, 4)) * 0.04D);

        AABB area = player.getBoundingBox().inflate(radius);
        player.serverLevel().getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity != player && entity.isAlive() && !(entity instanceof Player)).forEach(target -> {
            HighlightUtils.applyColoredHighlight(target, player.serverLevel(), "tu_hl_wf", ChatFormatting.GOLD, durationTicks);
            MarkUtils.applyWayfinderDamageMark(target, player.serverLevel(), bonusPct, durationTicks);
        });

        player.serverLevel().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.8f);
        TrimSkillCooldowns.startCooldown(player, TrimPatterns.WAYFINDER, minLevel);
    }
}


