package com.xiyue.trimmod.common.trim.set.wayfinder;

import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class WayfinderScreenData {
    public TrimTopDisplayData createTopDisplay(int level) {
        Component title = Component.translatable("gui.trimupgrade.trim_effect.top.title.wayfinder", level);
        Component attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.wayfinder",
                fmt1(2.0D + (1.5D * level)),
                4 + (2 * level));
        return new TrimTopDisplayData(title, attribute, 0xFFCC33, 0xFFD700);
    }

    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        double range = 20.0D + (minLevel * 2.5D);
        double duration = 10.0D + (minLevel * 2.5D);
        int dmgBonusPct = 10 + (minLevel * 4);
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.wayfinder.title1"), 0xFFCC33, 0xFFD700),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.wayfinder.line1")),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.wayfinder.skill_title", skillKey), 0xFFCC33, 0xFFD700),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.wayfinder.skill_desc1", fmt1(range))),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.wayfinder.skill_desc2", fmt1(duration))),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.wayfinder.skill_desc3", dmgBonusPct)),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.wayfinder.cooldown", Config.cooldownWayfinderSeconds))
        );
    }

    private static String fmt1(double value) { return String.format(Locale.ROOT, "%.1f", value); }
}

