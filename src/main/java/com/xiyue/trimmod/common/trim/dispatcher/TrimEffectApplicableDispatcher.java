package com.xiyue.trimmod.common.trim.dispatcher;

import com.xiyue.trimmod.common.trim.context.PlayerTrimContext;
import com.xiyue.trimmod.common.trim.set.wild.WildSet;
import net.minecraftforge.event.entity.living.MobEffectEvent;

public final class TrimEffectApplicableDispatcher {
    private TrimEffectApplicableDispatcher() {
    }

    public static void dispatch(MobEffectEvent.Applicable event, PlayerTrimContext context) {
        WildSet.COMBAT.onEffectApplicable(event, context);
    }
}
