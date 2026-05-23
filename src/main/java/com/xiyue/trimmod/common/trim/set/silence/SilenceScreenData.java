package com.xiyue.trimmod.common.trim.set.silence;

import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class SilenceScreenData {
    public TrimTopDisplayData createTopDisplay(int level) {
        Component title = Component.translatable("gui.trimupgrade.trim_effect.top.title.silence", level);
        Component attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.silence",
                fmt1(1.5D + (0.5D * level)),
                fmt1(0.5D + (0.25D * level)));
        return new TrimTopDisplayData(title, attribute, 0x003838, 0x00A0A0);
    }

    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        int slowPct = 45 + (minLevel * 5);
        int slowSeconds = 5;
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.silence.title1"), 0x003838, 0x00A0A0),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.silence.line1")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.silence.line2")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.silence.line3")),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.silence.skill1_title", skillKey), 0x003838, 0x00A0A0),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.silence.skill1_desc1")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.silence.skill1_desc2", slowPct, slowSeconds)),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.silence.skill2_title", skillKey), 0x003838, 0x00A0A0),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.silence.skill2_desc1")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.silence.skill2_desc2", slowPct, slowSeconds))
        );
    }

    private static String fmt1(double value) { return String.format(Locale.ROOT, "%.1f", value); }
}
