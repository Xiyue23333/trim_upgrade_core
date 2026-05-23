package com.xiyue.trimmod.common.trim.context;

import com.xiyue.trimmod.common.util.TrimUtils;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.armortrim.TrimPattern;

public final class PlayerTrimContextFactory {
    private PlayerTrimContextFactory() {
    }

    public static PlayerTrimContext create(Player player) {
        int minUpgradeLevel = TrimUtils.getMinUpgradeLevel(player);
        Holder<TrimPattern> fullSetPattern = TrimUtils.getFullSetPatternHolder(player);
        return new PlayerTrimContext(player, minUpgradeLevel, fullSetPattern);
    }
}
