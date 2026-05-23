package com.xiyue.trimmod.common.trim.set.shaper;

import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class ShaperScreenData {
    public TrimTopDisplayData createTopDisplay(int level) {
        Component title = Component.translatable("gui.trimupgrade.trim_effect.top.title.shaper", level);
        Component attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.shaper",
                fmt1(1.0D + (0.5D * level)),
                fmt1(0.15D + (0.1875D * level)));
        return new TrimTopDisplayData(title, attribute, 0xFFD700, 0x8B4513);
    }

    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.shaper.title1"), 0xFFD700, 0x8B4513),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.shaper.line1", 1 + minLevel)),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.shaper.skill_title", skillKey), 0xFFD700, 0x8B4513),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.shaper.skill_desc1", fmt1(28.0D + (5.5D * minLevel)))),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.shaper.skill_desc2", 12 + (2 * minLevel))),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.shaper.cooldown", Config.cooldownShaperSeconds))
        );
    }

    private static String fmt1(double value) { return String.format(Locale.ROOT, "%.1f", value); }
}

