package com.xiyue.trimmod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

final class DuneSkillIconRenderHelper {
    private DuneSkillIconRenderHelper() {
    }

    static void renderIfNeeded(GuiGraphics guiGraphics, Player player) {
        if (DuneSkillIconRenderer.isFullDuneSet(player)) {
            DuneSkillIconRenderer.renderIcon(guiGraphics, player);
        }
    }
}
