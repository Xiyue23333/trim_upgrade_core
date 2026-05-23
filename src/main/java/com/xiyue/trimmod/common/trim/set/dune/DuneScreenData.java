package com.xiyue.trimmod.common.trim.set.dune;

import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class DuneScreenData {
    public TrimTopDisplayData createTopDisplay(int level) {
        Component title = Component.translatable("gui.trimupgrade.trim_effect.top.title.dune", level);
        Component attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.dune",
                4 + 3 * level, fmt2(0.5D + 0.1875D * level));
        return new TrimTopDisplayData(title, attribute, 0xC2B280, 0xFFD700);
    }

    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        int attackSpeed = (int) (10 + minLevel * 7.5D);
        int curseDamage = 12 + (4 * Math.max(0, Math.min(minLevel, 4)));
        String duration = fmt1((100 + minLevel * 10) / 20.0D);
        String hitDamage = fmt1(1.8D + minLevel * 0.4D);
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.dune.title1"), 0xC2B280, 0xFFD700),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.dune.line1", attackSpeed)),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.dune.title2"), 0xC2B280, 0xFFD700),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.dune.line2")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.dune.line3", curseDamage)),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.dune.skill_title", skillKey), 0xC2B280, 0xFFD700),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.dune.skill_desc1", duration)),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.dune.skill_desc2")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.dune.skill_desc3", hitDamage, 10)),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.dune.skill_desc4")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.dune.cooldown", Config.cooldownDuneSeconds))
        );
    }

    private static String fmt1(double value) { return String.format(Locale.ROOT, "%.1f", value); }
    private static String fmt2(double value) { return String.format(Locale.ROOT, "%.2f", value); }
}
