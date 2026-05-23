package com.xiyue.trimmod.common.trim.context;

import com.xiyue.trimmod.common.util.TrimUtils;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.armortrim.TrimPattern;

import java.util.HashMap;
import java.util.Map;

public final class PlayerTrimContext {
    private final Player player;
    private final int minUpgradeLevel;
    private final Holder<TrimPattern> fullSetPattern;
    private final Map<ResourceKey<TrimPattern>, Integer> trimCountCache = new HashMap<>();

    PlayerTrimContext(Player player, int minUpgradeLevel, Holder<TrimPattern> fullSetPattern) {
        this.player = player;
        this.minUpgradeLevel = minUpgradeLevel;
        this.fullSetPattern = fullSetPattern;
    }

    public Player player() {
        return player;
    }

    public int minUpgradeLevel() {
        return minUpgradeLevel;
    }

    public int trimCount(ResourceKey<TrimPattern> pattern) {
        return trimCountCache.computeIfAbsent(pattern, key -> TrimUtils.getTrimCount(player, key));
    }

    public boolean hasFullSet() {
        return fullSetPattern != null;
    }

    public boolean isFullSet(ResourceKey<TrimPattern> pattern) {
        return fullSetPattern != null && fullSetPattern.is(pattern);
    }
}
