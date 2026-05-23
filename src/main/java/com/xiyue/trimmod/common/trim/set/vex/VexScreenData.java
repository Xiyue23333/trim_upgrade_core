package com.xiyue.trimmod.common.trim.set.vex;

import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class VexScreenData {
    public TrimTopDisplayData createTopDisplay(int level) {
        Component title = Component.translatable("gui.trimupgrade.trim_effect.top.title.vex", level);
        Component attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.vex", fmt2(5.0D + 1.25D * level), fmt2(5.0D + 1.25D * level));
        return new TrimTopDisplayData(title, attribute, 0xFFD700, 0xFF5555);
    }

    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        double damageReduction = 15.0D + 7.25D * minLevel;
        int xpCost = 400 + (150 * minLevel);
        int duration = 12 + (2 * minLevel);
        double critChance = 50.0D + (12.5D * minLevel);
        double critDamageBonus = 50.0D + (12.5D * minLevel);
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.vex.title1"), 0xFFD700, 0xFF5555),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.vex.line1")),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.vex.title2"), 0xFFD700, 0xFF5555),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.vex.line2", fmt2(damageReduction))),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.vex.skill_title", skillKey, xpCost), 0xFFD700, 0xFF5555),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.vex.skill_desc1", duration, fmt1(critChance), fmt1(critDamageBonus))),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.vex.skill_desc2")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.vex.cooldown", Config.cooldownVexSeconds)),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.vex.xp_cost", xpCost))
        );
    }

    private static String fmt1(double value) { return String.format(Locale.ROOT, "%.1f", value); }
    private static String fmt2(double value) { return String.format(Locale.ROOT, "%.2f", value); }
}
