package com.xiyue.trimmod.common.trim.dispatcher;

import com.xiyue.trimmod.common.trim.set.coast.CoastSet;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import com.xiyue.trimmod.common.trim.set.dune.DuneSet;
import com.xiyue.trimmod.common.trim.set.eye.EyeSet;
import com.xiyue.trimmod.common.trim.set.host.HostSet;
import com.xiyue.trimmod.common.trim.set.raiser.RaiserSet;
import com.xiyue.trimmod.common.trim.set.rib.RibSet;
import com.xiyue.trimmod.common.trim.set.sentry.SentrySet;
import com.xiyue.trimmod.common.trim.set.shaper.ShaperSet;
import com.xiyue.trimmod.common.trim.set.silence.SilenceSet;
import com.xiyue.trimmod.common.trim.set.snout.SnoutSet;
import com.xiyue.trimmod.common.trim.set.spire.SpireSet;
import com.xiyue.trimmod.common.trim.set.tide.TideSet;
import com.xiyue.trimmod.common.trim.set.vex.VexSet;
import com.xiyue.trimmod.common.trim.set.ward.WardSet;
import com.xiyue.trimmod.common.trim.set.wayfinder.WayfinderSet;
import com.xiyue.trimmod.common.trim.set.wild.WildSet;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;

public final class TrimScreenDataDispatcher {
    private TrimScreenDataDispatcher() {
    }

    public static TrimTopDisplayData resolveTopDisplay(Holder<TrimPattern> pattern, int level, boolean contributorOverride) {
        if (pattern.is(TrimPatterns.TIDE)) {
            return TideSet.SCREEN.createTopDisplay(level, contributorOverride);
        }
        if (pattern.is(TrimPatterns.VEX)) {
            return VexSet.SCREEN.createTopDisplay(level);
        }
        if (pattern.is(TrimPatterns.DUNE)) {
            return DuneSet.SCREEN.createTopDisplay(level);
        }
        if (pattern.is(TrimPatterns.SNOUT)) {
            return SnoutSet.SCREEN.createTopDisplay(level);
        }
        if (pattern.is(TrimPatterns.WILD)) {
            return WildSet.SCREEN.createTopDisplay(level, contributorOverride);
        }
        if (pattern.is(TrimPatterns.HOST)) {
            return HostSet.SCREEN.createTopDisplay(level, contributorOverride);
        }
        if (pattern.is(TrimPatterns.SENTRY)) {
            return SentrySet.SCREEN.createTopDisplay(level, contributorOverride);
        }
        if (pattern.is(TrimPatterns.RAISER)) {
            return RaiserSet.SCREEN.createTopDisplay(level, contributorOverride);
        }
        if (pattern.is(TrimPatterns.WARD)) {
            return WardSet.SCREEN.createTopDisplay(level, contributorOverride);
        }
        if (pattern.is(TrimPatterns.EYE)) {
            return EyeSet.SCREEN.createTopDisplay(level);
        }
        if (pattern.is(TrimPatterns.SPIRE)) {
            return SpireSet.SCREEN.createTopDisplay(level);
        }
        if (pattern.is(TrimPatterns.RIB)) {
            return RibSet.SCREEN.createTopDisplay(level);
        }
        if (pattern.is(TrimPatterns.WAYFINDER)) {
            return WayfinderSet.SCREEN.createTopDisplay(level);
        }
        if (pattern.is(TrimPatterns.COAST)) {
            return CoastSet.SCREEN.createTopDisplay(level, contributorOverride);
        }
        if (pattern.is(TrimPatterns.SHAPER)) {
            return ShaperSet.SCREEN.createTopDisplay(level);
        }
        if (pattern.is(TrimPatterns.SILENCE)) {
            return SilenceSet.SCREEN.createTopDisplay(level);
        }
        return null;
    }

    public static TrimBottomSectionData resolveBottomSection(ResourceKey<TrimPattern> pattern, int minLevel, String skillKey) {
        if (pattern.equals(TrimPatterns.TIDE)) {
            return TideSet.SCREEN.createBottomSection(minLevel, skillKey);
        }
        if (pattern.equals(TrimPatterns.VEX)) {
            return VexSet.SCREEN.createBottomSection(minLevel, skillKey);
        }
        if (pattern.equals(TrimPatterns.DUNE)) {
            return DuneSet.SCREEN.createBottomSection(minLevel, skillKey);
        }
        if (pattern.equals(TrimPatterns.SNOUT)) {
            return SnoutSet.SCREEN.createBottomSection(minLevel, skillKey);
        }
        if (pattern.equals(TrimPatterns.WILD)) {
            return WildSet.SCREEN.createBottomSection(minLevel, skillKey);
        }
        if (pattern.equals(TrimPatterns.HOST)) {
            return HostSet.SCREEN.createBottomSection(minLevel, skillKey);
        }
        if (pattern.equals(TrimPatterns.SENTRY)) {
            return SentrySet.SCREEN.createBottomSection(minLevel, skillKey);
        }
        if (pattern.equals(TrimPatterns.RAISER)) {
            return RaiserSet.SCREEN.createBottomSection(minLevel, skillKey);
        }
        if (pattern.equals(TrimPatterns.WARD)) {
            return WardSet.SCREEN.createBottomSection(minLevel, skillKey);
        }
        if (pattern.equals(TrimPatterns.EYE)) {
            return EyeSet.SCREEN.createBottomSection(minLevel, skillKey);
        }
        if (pattern.equals(TrimPatterns.SPIRE)) {
            return SpireSet.SCREEN.createBottomSection(minLevel, skillKey);
        }
        if (pattern.equals(TrimPatterns.RIB)) {
            return RibSet.SCREEN.createBottomSection(minLevel, skillKey);
        }
        if (pattern.equals(TrimPatterns.WAYFINDER)) {
            return WayfinderSet.SCREEN.createBottomSection(minLevel, skillKey);
        }
        if (pattern.equals(TrimPatterns.COAST)) {
            return CoastSet.SCREEN.createBottomSection(minLevel, skillKey);
        }
        if (pattern.equals(TrimPatterns.SHAPER)) {
            return ShaperSet.SCREEN.createBottomSection(minLevel, skillKey);
        }
        if (pattern.equals(TrimPatterns.SILENCE)) {
            return SilenceSet.SCREEN.createBottomSection(minLevel, skillKey);
        }
        return null;
    }
}
