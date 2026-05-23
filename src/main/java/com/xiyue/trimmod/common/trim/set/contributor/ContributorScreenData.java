package com.xiyue.trimmod.common.trim.set.contributor;

import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.common.trim.api.TrimBottomLine;
import com.xiyue.trimmod.common.trim.api.TrimBottomSectionData;
import net.minecraft.network.chat.Component;

public final class ContributorScreenData {
    public TrimBottomSectionData createBottomSection(int minLevel, String skillKey) {
        return TrimBottomSectionData.of(
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.contributor.header"), 0xFFD700, 0x55FF55),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.contributor.combo")),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.contributor.level", minLevel)),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.contributor.crouch_title"), 0xFFD700, 0x55FF55),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.contributor.crouch_desc")),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.contributor.cap_title"), 0xFFD700, 0xFF5555),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.contributor.cap_desc")),
                TrimBottomLine.streamer(Component.translatable("gui.trimupgrade.trim_effect.desc.contributor.skill_title", skillKey), 0xFFD700, 0xFF5555),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.contributor.skill_desc1", 8 + (4 * minLevel))),
                TrimBottomLine.body(Component.translatable("gui.trimupgrade.trim_effect.desc.contributor.skill_desc2", Config.cooldownContributorSeconds))
        );
    }
}

