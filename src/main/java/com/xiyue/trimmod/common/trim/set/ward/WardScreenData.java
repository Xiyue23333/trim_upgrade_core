package com.xiyue.trimmod.common.trim.set.ward;

import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class WardScreenData {
    public TrimTopDisplayData createTopDisplay(int level, boolean contributorOverride) {
        Component title = Component.translatable("gui.trimupgrade.trim_effect.top.title.ward", level);
        Component attribute;
        if (contributorOverride) {
            double armorAdd = 0.5D + 0.375D * level;
            int armorPct = 2 + level;
            attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.contributor_override", fmt2(armorAdd), armorPct);
        } else {
            attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.ward",
                    fmt1(1.0D + 0.4375D * level),
                    (int) ((0.05D + 0.03D * level) * 100));
        }
        return new TrimTopDisplayData(title, attribute, 0x114444, 0x00A0A0);
    }

    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.ward.title1"), 0x114444, 0x00A0A0),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.ward.line1", 14 + 8 * minLevel)),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.ward.title2"), 0x114444, 0x00A0A0),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.ward.line2")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.ward.line3")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.ward.line4", 24 + 6 * minLevel))
        );
    }

    private static String fmt1(double value) { return String.format(Locale.ROOT, "%.1f", value); }
    private static String fmt2(double value) { return String.format(Locale.ROOT, "%.2f", value); }
}
