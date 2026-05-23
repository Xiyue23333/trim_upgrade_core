package com.xiyue.trimmod.common.trim.dispatcher;

import com.xiyue.trimmod.common.trim.set.coast.CoastSet;
import com.xiyue.trimmod.common.trim.set.dune.DuneSet;
import com.xiyue.trimmod.common.trim.set.host.HostSet;
import com.xiyue.trimmod.common.trim.set.raiser.RaiserSet;
import com.xiyue.trimmod.common.trim.set.sentry.SentrySet;
import com.xiyue.trimmod.common.trim.set.shaper.ShaperSet;
import com.xiyue.trimmod.common.trim.set.snout.SnoutSet;
import com.xiyue.trimmod.common.trim.set.tide.TideSet;
import com.xiyue.trimmod.common.trim.set.vex.VexSet;
import com.xiyue.trimmod.common.trim.set.ward.WardSet;
import com.xiyue.trimmod.common.trim.set.wayfinder.WayfinderSet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;

public final class TrimTickDispatcher {
    private TrimTickDispatcher() {
    }

    public static void emitSetAuras(ServerPlayer player) {
        VexSet.TICK.emitSoulSacrificeAura(player);
    }

    public static void tickDashState(ServerPlayer player) {
        SnoutSet.TICK.tickDash(player);
    }

    public static void tickSetState(ServerPlayer player, boolean hasTideSet, boolean hasVexSet, boolean hasDuneSet,
                                    boolean hasSnoutSet, boolean hasShaperSet, boolean hasWayfinderSet, boolean hasCoastSet,
                                    int fullSetMinLevel) {
        TideSet.TICK.tick(player, hasTideSet, fullSetMinLevel);
        ShaperSet.TICK.tickEnergy(player, hasShaperSet);
        if (!hasSnoutSet) {
            SnoutSet.TICK.clearDashState(player);
        }
    }

    public static void applyPassiveEffects(Player player) {
        TideSet.TICK.applyPassiveEffects(player);
    }

    public static void applyHostPassive(Player player, boolean hasFullSet, int minLevel) {
        HostSet.TICK.applyPassiveEffects(player, hasFullSet, minLevel);
    }

    public static void applyRaiserPassive(Player player, boolean hasFullSet, int minLevel) {
        RaiserSet.TICK.tick(player, hasFullSet, minLevel);
    }

    public static void onWardSneakClientTick(TickEvent.PlayerTickEvent event, boolean hasFullSet) {
        if (!hasFullSet) {
            return;
        }
        WardSet.TICK.onSneakClientTick(event);
    }

    public static void handleSentryFloatingEntity(Player player, int sentryCount, int minLevel) {
        SentrySet.TICK.handleFloatingEntity(player, sentryCount, minLevel);
    }
}
