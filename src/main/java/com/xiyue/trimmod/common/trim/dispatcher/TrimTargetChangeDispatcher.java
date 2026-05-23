package com.xiyue.trimmod.common.trim.dispatcher;

import com.xiyue.trimmod.common.trim.context.PlayerTrimContextFactory;
import com.xiyue.trimmod.common.trim.set.eye.EyeSet;
import com.xiyue.trimmod.common.trim.set.host.HostSet;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;

public final class TrimTargetChangeDispatcher {
    private TrimTargetChangeDispatcher() {
    }

    public static void dispatch(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof EnderMan && event.getNewTarget() instanceof Player player) {
            EyeSet.COMBAT.onTargetChange(event, PlayerTrimContextFactory.create(player));
        }

        if (event.getEntity() instanceof IronGolem && event.getNewTarget() instanceof Player player) {
            boolean hasHostFullSet = PlayerTrimContextFactory.create(player).isFullSet(TrimPatterns.HOST);
            HostSet.COMBAT.onTargetChange(event, hasHostFullSet);
        }
    }
}
