package com.xiyue.trimmod.common.trim.set.spire;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public final class SpireCombat {
    public void onFall(LivingFallEvent event, Player player, boolean hasFullSet) {
        if (!hasFullSet) {
            return;
        }
        event.setDamageMultiplier(0.0F);
        event.setDistance(0.0F);
        player.fallDistance = 0.0F;
    }
}
