package com.xiyue.trimmod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

final class SwampSkillIconRenderHelper {
    private SwampSkillIconRenderHelper() {
    }

    static void renderIfNeeded(GuiGraphics guiGraphics, Player player) {
        if (SwampSkillIconRenderer.isSwampSet(player)) {
            SwampSkillIconRenderer.renderIcon(guiGraphics, player);
        }
    }
}
