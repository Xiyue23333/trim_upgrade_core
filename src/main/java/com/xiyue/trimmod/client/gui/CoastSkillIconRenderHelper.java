package com.xiyue.trimmod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

final class CoastSkillIconRenderHelper {
    private CoastSkillIconRenderHelper() {
    }

    static void renderIfNeeded(GuiGraphics guiGraphics, Player player) {
        if (CoastSkillIconRenderer.isFullCoastSet(player)) {
            CoastSkillIconRenderer.renderIcon(guiGraphics, player);
        }
    }
}
