package com.xiyue.trimmod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

final class SnoutSkillIconRenderHelper {
    private SnoutSkillIconRenderHelper() {
    }

    static void renderIfNeeded(GuiGraphics guiGraphics, Player player) {
        if (SnoutSkillIconRenderer.isFullSnoutSet(player)) {
            SnoutSkillIconRenderer.renderIcon(guiGraphics, player);
        }
    }
}
