package com.xiyue.trimmod.common.trim.set.host;

import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;

public final class HostCombat {
    public void onTargetChange(LivingChangeTargetEvent event, boolean playerHasHostFullSet) {
        if (!playerHasHostFullSet) {
            return;
        }
        if (event.getEntity() instanceof IronGolem) {
            event.setCanceled(true);
        }
        if (event.getEntity() instanceof EnderMan) {
            // keep untouched for host, this branch is intentionally no-op
        }
    }

    public void clearGolemTargetIfNeeded(IronGolem golem, Player player, boolean playerHasHostFullSet) {
        if (playerHasHostFullSet && golem.getTarget() == player) {
            golem.setTarget(null);
        }
    }
}
