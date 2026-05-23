package com.xiyue.trimmod.common.trim.set.tide;

import com.xiyue.trimmod.common.util.TrimUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.armortrim.TrimPatterns;

public final class TideTick {
    public void tick(ServerPlayer player, boolean hasFullSet, int fullSetMinLevel) {
        if (hasFullSet) {
            TideSet.SKILL.tickActiveSkill(player, fullSetMinLevel);
            return;
        }
        TideSet.SKILL.clearActiveSkill(player);
    }

    public void applyPassiveEffects(Player player) {
        if (!player.level().isDay() && TrimUtils.getTrimCount(player, TrimPatterns.TIDE) >= 4) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
        }
    }
}
