package com.xiyue.trimmod.common.trim.set.tide;

import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class TideScreenData {
    public TrimTopDisplayData createTopDisplay(int level, boolean contributorOverride) {
        Component title = Component.translatable("gui.trimupgrade.trim_effect.top.title.tide", level);
        Component attribute;
        if (contributorOverride) {
            double armorAdd = 0.5D + 0.375D * level;
            int armorPct = 2 + level;
            attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.contributor_override", fmt2(armorAdd), armorPct);
        } else {
            attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.tide", fmt1(1.5D + 1.125D * level), fmt2(4.0D + 1.25D * level), fmt1(1.5D + 0.75D * level));
        }
        return new TrimTopDisplayData(title, attribute, 0x00FFFF, 0x1E90FF);
    }

    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        String dayRate = switch (minLevel) {
            case 0 -> "17%";
            case 1 -> "25%";
            case 2 -> "32%";
            case 3 -> "40%";
            default -> "50%";
        };
        String nightDamage = switch (minLevel) {
            case 0 -> "15%";
            case 1 -> "25.5%";
            case 2 -> "37.5%";
            case 3 -> "48%";
            default -> "60%";
        };
        double skillDamage = 4.0D + (minLevel * 1.5D);
        double attackBonus = 1.0D + 0.5D * minLevel;
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.tide.yinyang_title"), 0xFFD700, 0xBF55EC),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.tide.day_title"), 0xFFD700, 0xFFE8A0),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.tide.day", dayRate, fmt1(attackBonus))),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.tide.night_title"), 0xA330C9, 0x55FFFF),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.tide.night", nightDamage)),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.tide.skill_title", skillKey), 0x00FFFF, 0x1E90FF),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.tide.skill_desc1", fmt1(skillDamage))),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.tide.skill_desc2")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.tide.skill_desc3")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.tide.cooldown", Config.cooldownTideSeconds)),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.tide.sunmoon_title"), 0xFFD700, 0xFFE8A0),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.tide.sunmoon_desc1")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.tide.sunmoon_desc2"))
        );
    }

    private static String fmt1(double value) { return String.format(Locale.ROOT, "%.1f", value); }
    private static String fmt2(double value) { return String.format(Locale.ROOT, "%.2f", value); }
}
