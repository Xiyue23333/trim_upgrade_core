package com.xiyue.trimmod.common.trim.set.eye;

import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class EyeScreenData {
    public TrimTopDisplayData createTopDisplay(int level) {
        Component title = Component.translatable("gui.trimupgrade.trim_effect.top.title.eye", level);
        Component attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.eye",
                fmt1(1.25D - (0.1875D * level)),
                fmt1(0.85D - (0.15D * level)),
                fmt1(2.5D + (1.0D * level)));
        return new TrimTopDisplayData(title, attribute, 0x55FFFF, 0xAA00FF);
    }

    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.eye.title1"), 0x55FFFF, 0xAA00FF),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.eye.line1")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.eye.line2", 20 + (5 * minLevel))),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.eye.skill_title", skillKey), 0x55FFFF, 0xAA00FF),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.eye.skill_desc1")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.eye.skill_desc2")),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.eye.title2"), 0x55FFFF, 0xAA00FF),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.eye.line3")),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.eye.title3"), 0x55FFFF, 0xAA00FF),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.eye.line4", fmt1(6 + (2.5D * minLevel))))
        );
    }

    private static String fmt1(double value) { return String.format(Locale.ROOT, "%.1f", value); }
}
