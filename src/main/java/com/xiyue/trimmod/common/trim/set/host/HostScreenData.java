package com.xiyue.trimmod.common.trim.set.host;

import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class HostScreenData {
    public TrimTopDisplayData createTopDisplay(int level, boolean contributorOverride) {
        Component title = Component.translatable("gui.trimupgrade.trim_effect.top.title.host", level);
        Component attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.host",
                fmt1(1.0D + 0.5D * level),
                (int) ((0.05D + 0.03D * level) * 100));
        return new TrimTopDisplayData(title, attribute, 0xFFD700, 0xFFFFFF);
    }

    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        int heroLevel = minLevel + 1;
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.host.title1"), 0xFFD700, 0xFFFFFF),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.host.line1")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.host.line2")),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.host.title2"), 0xFFD700, 0xFFFFFF),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.host.line3", heroLevel))
        );
    }

    private static String fmt1(double value) { return String.format(Locale.ROOT, "%.1f", value); }
}
