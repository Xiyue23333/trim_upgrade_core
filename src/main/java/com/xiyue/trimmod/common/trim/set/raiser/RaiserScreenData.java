package com.xiyue.trimmod.common.trim.set.raiser;

import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class RaiserScreenData {
    public TrimTopDisplayData createTopDisplay(int level, boolean contributorOverride) {
        Component title = Component.translatable("gui.trimupgrade.trim_effect.top.title.raiser", level);
        Component attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.raiser",
                fmt1(1.0D + 0.375D * level),
                fmt1(0.04D + 0.01D * level));
        return new TrimTopDisplayData(title, attribute, 0xFFD557, 0xFFFFFF);
    }

    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.raiser.title1"), 0xFFD557, 0xFFFFFF),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.raiser.line1", 2 + minLevel)),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.raiser.line2", 4 + 2 * minLevel)),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.raiser.title2"), 0xFFD557, 0xFFFFFF),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.raiser.line3")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.raiser.line4"))
        );
    }

    private static String fmt1(double value) { return String.format(Locale.ROOT, "%.1f", value); }
}
