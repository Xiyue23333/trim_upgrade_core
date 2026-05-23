package com.xiyue.trimmod.common.trim.set.spire;

import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class SpireScreenData {
    public TrimTopDisplayData createTopDisplay(int level) {
        Component title = Component.translatable("gui.trimupgrade.trim_effect.top.title.spire", level);
        double damagePct = 2.0D + (1.5D * level);
        double reach = 0.05D + (0.05D * level);
        Component attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.spire", fmt1(damagePct), fmt2(reach));
        return new TrimTopDisplayData(title, attribute, 0x1A1A1A, 0xAA00AA);
    }

    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.spire.skill_title", skillKey), 0x1A1A1A, 0xAA00AA),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.spire.skill_desc1")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.spire.skill_desc2")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.spire.cooldown", Config.cooldownSpireSeconds)),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.spire.title1"), 0x1A1A1A, 0xAA00AA),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.spire.line3")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.spire.line4", fmt1(10 + 2.5 * minLevel))),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.spire.title2"), 0x1A1A1A, 0xAA00AA),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.spire.line5")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.spire.line6")),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.spire.title3"), 0x1A1A1A, 0xAA00AA),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.spire.line7"))
        );
    }

    private static String fmt1(double value) { return String.format(Locale.ROOT, "%.1f", value); }
    private static String fmt2(double value) { return String.format(Locale.ROOT, "%.2f", value); }
}
