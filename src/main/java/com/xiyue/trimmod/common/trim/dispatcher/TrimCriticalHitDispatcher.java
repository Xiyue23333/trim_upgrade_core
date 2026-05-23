package com.xiyue.trimmod.common.trim.dispatcher;

import com.xiyue.trimmod.common.trim.context.PlayerTrimContext;
import com.xiyue.trimmod.common.trim.set.eye.EyeSet;
import com.xiyue.trimmod.common.trim.set.tide.TideSet;
import com.xiyue.trimmod.common.trim.set.vex.VexSet;
import net.minecraftforge.event.entity.player.CriticalHitEvent;

public final class TrimCriticalHitDispatcher {
    private TrimCriticalHitDispatcher() {
    }

    public static void dispatch(CriticalHitEvent event, PlayerTrimContext context) {
        VexSet.COMBAT.onCriticalHit(event, context.player());
        TideSet.COMBAT.onCriticalHit(event, context.player());
        EyeSet.COMBAT.onCriticalHit(event, context.player());
    }
}
