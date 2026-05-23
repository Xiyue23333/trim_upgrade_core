package com.xiyue.trimmod.common.trim.set.rib;

import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class RibScreenData {
    public TrimTopDisplayData createTopDisplay(int level) {
        Component title = Component.translatable("gui.trimupgrade.trim_effect.top.title.rib", level);
        Component attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.rib",
                fmt1(1.0D + (0.5D * level)),
                fmt1(0.4D + (0.1125D * level)));
        return new TrimTopDisplayData(title, attribute, 0xFFD700, 0x8A0031);
    }

    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.rib.title1"), 0xFFD700, 0x8A0031),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.rib.line1")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.rib.line2")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.rib.line3")),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.rib.title2"), 0xFFD700, 0x8A0031),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.rib.line4")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.rib.line5")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.rib.line6")),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.rib.skill_title", skillKey), 0xFFD700, 0x8A0031),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.rib.skill_desc1"))
        );
    }

    private static String fmt1(double value) { return String.format(Locale.ROOT, "%.1f", value); }
}
