package com.xiyue.trimmod.common.trim.dispatcher;

import com.xiyue.trimmod.common.trim.api.TrimModifierApplier;
import com.xiyue.trimmod.common.trim.set.coast.CoastAttributes;
import com.xiyue.trimmod.common.trim.set.coast.CoastSet;
import com.xiyue.trimmod.common.trim.set.contributor.ContributorAttributes;
import com.xiyue.trimmod.common.trim.set.contributor.ContributorSet;
import com.xiyue.trimmod.common.trim.set.dune.DuneAttributes;
import com.xiyue.trimmod.common.trim.set.dune.DuneSet;
import com.xiyue.trimmod.common.trim.set.eye.EyeAttributes;
import com.xiyue.trimmod.common.trim.set.eye.EyeSet;
import com.xiyue.trimmod.common.trim.set.host.HostAttributes;
import com.xiyue.trimmod.common.trim.set.host.HostSet;
import com.xiyue.trimmod.common.trim.set.raiser.RaiserAttributes;
import com.xiyue.trimmod.common.trim.set.raiser.RaiserSet;
import com.xiyue.trimmod.common.trim.set.rib.RibAttributes;
import com.xiyue.trimmod.common.trim.set.rib.RibSet;
import com.xiyue.trimmod.common.trim.set.sentry.SentryAttributes;
import com.xiyue.trimmod.common.trim.set.sentry.SentrySet;
import com.xiyue.trimmod.common.trim.set.shaper.ShaperAttributes;
import com.xiyue.trimmod.common.trim.set.shaper.ShaperSet;
import com.xiyue.trimmod.common.trim.set.silence.SilenceAttributes;
import com.xiyue.trimmod.common.trim.set.silence.SilenceSet;
import com.xiyue.trimmod.common.trim.set.snout.SnoutAttributes;
import com.xiyue.trimmod.common.trim.set.snout.SnoutSet;
import com.xiyue.trimmod.common.trim.set.spire.SpireAttributes;
import com.xiyue.trimmod.common.trim.set.spire.SpireSet;
import com.xiyue.trimmod.common.trim.set.tide.TideAttributes;
import com.xiyue.trimmod.common.trim.set.tide.TideSet;
import com.xiyue.trimmod.common.trim.set.vex.VexAttributes;
import com.xiyue.trimmod.common.trim.set.vex.VexSet;
import com.xiyue.trimmod.common.trim.set.ward.WardAttributes;
import com.xiyue.trimmod.common.trim.set.ward.WardSet;
import com.xiyue.trimmod.common.trim.set.wayfinder.WayfinderAttributes;
import com.xiyue.trimmod.common.trim.set.wayfinder.WayfinderSet;
import com.xiyue.trimmod.common.trim.set.wild.WildAttributes;
import com.xiyue.trimmod.common.trim.set.wild.WildSet;
import net.minecraft.world.entity.player.Player;

public final class TrimAttributeDispatcher {
    private TrimAttributeDispatcher() {
    }

    public static void applyTide(Player player, TideAttributes.Totals totals, boolean isDaytime, TrimModifierApplier applier) {
        TideSet.ATTRIBUTES.apply(player, totals, isDaytime, applier);
    }

    public static void applyVex(Player player, VexAttributes.Totals totals, TrimModifierApplier applier) {
        VexSet.ATTRIBUTES.apply(player, totals, applier);
    }

    public static void applyDune(Player player, DuneAttributes.Totals totals, TrimModifierApplier applier) {
        DuneSet.ATTRIBUTES.apply(player, totals, applier);
    }

    public static void applySnout(Player player, SnoutAttributes.Totals totals, TrimModifierApplier applier) {
        SnoutSet.ATTRIBUTES.apply(player, totals, applier);
    }

    public static void applySpire(Player player, SpireAttributes.Totals totals, TrimModifierApplier applier) {
        SpireSet.ATTRIBUTES.apply(player, totals, applier);
    }

    public static void applyEye(Player player, EyeAttributes.Totals totals, TrimModifierApplier applier) {
        EyeSet.ATTRIBUTES.apply(player, totals, applier);
    }

    public static void applyRib(Player player, RibAttributes.Totals totals, TrimModifierApplier applier) {
        RibSet.ATTRIBUTES.apply(player, totals, applier);
    }

    public static void applyWild(Player player, WildAttributes.Totals totals, TrimModifierApplier applier) {
        WildSet.ATTRIBUTES.apply(player, totals, applier);
    }

    public static void applyWayfinder(Player player, WayfinderAttributes.Totals totals, TrimModifierApplier applier) {
        WayfinderSet.ATTRIBUTES.apply(player, totals, applier);
    }

    public static void applyCoast(Player player, CoastAttributes.Totals totals, TrimModifierApplier applier) {
        CoastSet.ATTRIBUTES.apply(player, totals, applier);
    }

    public static void applyShaper(Player player, ShaperAttributes.Totals totals, TrimModifierApplier applier) {
        ShaperSet.ATTRIBUTES.apply(player, totals, applier);
    }

    public static void applyHost(Player player, HostAttributes.Totals totals, TrimModifierApplier applier) {
        HostSet.ATTRIBUTES.apply(player, totals, applier);
    }

    public static void applySentry(Player player, SentryAttributes.Totals totals, TrimModifierApplier applier) {
        SentrySet.ATTRIBUTES.apply(player, totals, applier);
    }

    public static void applyRaiser(Player player, RaiserAttributes.Totals totals, TrimModifierApplier applier) {
        RaiserSet.ATTRIBUTES.apply(player, totals, applier);
    }

    public static void applyWard(Player player, WardAttributes.Totals totals, TrimModifierApplier applier) {
        WardSet.ATTRIBUTES.apply(player, totals, applier);
    }

    public static void applySilence(Player player, SilenceAttributes.Totals totals, TrimModifierApplier applier) {
        SilenceSet.ATTRIBUTES.apply(player, totals, applier);
    }

    public static void applyContributor(Player player, ContributorAttributes.Totals totals, TrimModifierApplier applier) {
        ContributorSet.ATTRIBUTES.apply(player, totals, applier);
    }
}
