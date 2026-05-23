package com.xiyue.trimmod.common.trim.set.swamp;

import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.armortrim.TrimPatterns;

import java.util.Locale;

public final class SwampScreenData {
    public TrimTopDisplayData createTopDisplay(int level, Holder<net.minecraft.world.item.armortrim.TrimPattern> pattern) {
        Component title = Component.translatable("gui.trimupgrade.trim_effect.top.title.swamp", level);
        Component attribute;
        if (pattern.is(TrimPatterns.TIDE)) {
            attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.swamp.tide",
                    fmt1(1.0D + 1.0D * level),
                    fmt1(1.0D + 1.0D * level));
        } else if (pattern.is(TrimPatterns.WILD)) {
            attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.swamp.wild",
                    fmt2(0.75D + 0.25D * level),
                    fmt1(0.1D + 0.1D * level));
        } else {
            attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.swamp",
                    fmt1(1.0D + 1.0D * level),
                    fmt1(1.0D + 1.0D * level),
                    fmt2(0.75D + 0.25D * level),
                    fmt1(0.1D + 0.1D * level));
        }
        return new TrimTopDisplayData(title, attribute, 0x2E7D5B, 0x8FD9A8);
    }

    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        double regenInterval = 1.5D - 0.25D * Math.max(0, Math.min(4, minLevel));
        int attackSpeedPercent = 12 + 3 * Math.max(0, Math.min(4, minLevel));
        String damage = fmt1(3.0D + 1.5D * Math.max(0, Math.min(4, minLevel)));
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.swamp.title1"), 0x2E7D5B, 0x8FD9A8),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.swamp.line1")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.swamp.line2")),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.swamp.title2"), 0x2E7D5B, 0x8FD9A8),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.swamp.line5")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.swamp.line6", fmt2(regenInterval))),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.swamp.line7")),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.swamp.skill_title", skillKey), 0x2E7D5B, 0x8FD9A8),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.swamp.skill_desc1")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.swamp.skill_desc2", damage)),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.swamp.skill_desc3", attackSpeedPercent))
        );
    }

    private static String fmt1(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private static String fmt2(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }
}
