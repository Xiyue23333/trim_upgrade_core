package com.xiyue.trimmod.common.trim.set.wild;

import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class WildScreenData {
    public TrimTopDisplayData createTopDisplay(int level, boolean contributorOverride) {
        Component title = Component.translatable("gui.trimupgrade.trim_effect.top.title.wild", level);
        Component attribute;
        if (contributorOverride) {
            double armorAdd = 0.5D + 0.375D * level;
            int armorPct = 2 + level;
            attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.contributor_override", fmt2(armorAdd), armorPct);
        } else {
            attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.wild",
                    fmt1(1.0D + 0.4375D * level),
                    fmt1(0.2D + 0.15D * level));
        }
        return new TrimTopDisplayData(title, attribute, 0x55FF55, 0xAAFFAA);
    }

    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        String poisonRank = (minLevel <= 1) ? "I" : (minLevel <= 3 ? "II" : "III");
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.wild.title1"), 0x55FF55, 0xAAFFAA),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.wild.line1")),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.wild.title2"), 0x55FF55, 0xAAFFAA),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.wild.line2", poisonRank)),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.wild.line3"))
        );
    }

    private static String fmt1(double value) { return String.format(Locale.ROOT, "%.1f", value); }
    private static String fmt2(double value) { return String.format(Locale.ROOT, "%.2f", value); }
}
