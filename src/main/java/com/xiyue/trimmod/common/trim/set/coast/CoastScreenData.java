package com.xiyue.trimmod.common.trim.set.coast;

import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class CoastScreenData {
    public TrimTopDisplayData createTopDisplay(int level, boolean contributorOverride) {
        Component title = Component.translatable("gui.trimupgrade.trim_effect.top.title.coast", level);
        Component attribute;
        if (contributorOverride) {
            double armorAdd = 0.5D + (0.375D * level);
            int armorPct = 2 + level;
            attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.contributor_override", fmt2(armorAdd), armorPct);
        } else {
            attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.coast",
                    fmt1(5.0D + (2.5D * level)),
                    fmt1(0.2D + (0.1375D * level)));
        }
        return new TrimTopDisplayData(title, attribute, 0xB0E0E6, 0x1E90FF);
    }

    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        int skillDmg = 12 + (minLevel * 3);
        int slowSec = 5 + (minLevel * 2);
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.coast.title1"), 0xB0E0E6, 0x1E90FF),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.coast.line1")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.coast.line2")),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.coast.skill_title", skillKey), 0xB0E0E6, 0x1E90FF),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.coast.skill_desc1", skillDmg)),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.coast.skill_desc2", slowSec)),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.coast.cooldown", Config.cooldownCoastSeconds)),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.coast.title2"), 0xB0E0E6, 0x1E90FF),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.coast.life_water"))
        );
    }

    private static String fmt1(double value) { return String.format(Locale.ROOT, "%.1f", value); }
    private static String fmt2(double value) { return String.format(Locale.ROOT, "%.2f", value); }
}

