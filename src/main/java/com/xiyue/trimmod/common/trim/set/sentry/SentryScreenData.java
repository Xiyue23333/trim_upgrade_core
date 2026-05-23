package com.xiyue.trimmod.common.trim.set.sentry;

import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class SentryScreenData {
    public TrimTopDisplayData createTopDisplay(int level, boolean contributorOverride) {
        Component title = Component.translatable("gui.trimupgrade.trim_effect.top.title.sentry", level);
        Component attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.sentry",
                3 + 2 * level,
                fmt1(0.15D + 0.0875D * level));
        return new TrimTopDisplayData(title, attribute, 0x556B2F, 0xAAFFAA);
    }

    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.sentry.title1"), 0x556B2F, 0xAAFFAA),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.sentry.line1")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.sentry.line2", 16 + 4 * minLevel)),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.sentry.title2"), 0x556B2F, 0xAAFFAA),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.sentry.line3")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.sentry.line4", fmt1(3 + 1.5D * minLevel))),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.sentry.line5"))
        );
    }

    private static String fmt1(double value) { return String.format(Locale.ROOT, "%.1f", value); }
}
