package com.xiyue.trimmod.common.trim.set.snout;

import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import com.xiyue.trimmod.common.trim.api.TrimTopDisplayData;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class SnoutScreenData {
    public TrimTopDisplayData createTopDisplay(int level) {
        Component title = Component.translatable("gui.trimupgrade.trim_effect.top.title.snout", level);
        Component attribute = Component.translatable("gui.trimupgrade.trim_effect.top.attr.snout",
                fmt1(1.0D + 0.5625D * level), (int) ((0.03D + 0.02D * level) * 100));
        return new TrimTopDisplayData(title, attribute, 0xFFCC99, 0xFF8844);
    }

    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        String dashDistance = fmt1(8.0D + (minLevel * 2.0D));
        String dashDamage = fmt1(6.0D + (minLevel * 2.0D));
        String cooldown = fmt2(Math.max(0.05D, Config.cooldownSnoutSeconds - (minLevel * 0.25D)));
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.snout.title1"), 0xFFCC99, 0xFF8844),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.snout.line1")),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.snout.skill_title", skillKey), 0xFFCC99, 0xFF8844),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.snout.skill_desc1", dashDistance)),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.snout.skill_desc2", dashDamage)),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.snout.skill_desc3")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.snout.cooldown", cooldown))
        );
    }

    private static String fmt1(double value) { return String.format(Locale.ROOT, "%.1f", value); }
    private static String fmt2(double value) { return String.format(Locale.ROOT, "%.2f", value); }
}
