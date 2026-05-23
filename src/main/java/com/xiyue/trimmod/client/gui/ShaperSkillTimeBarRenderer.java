package com.xiyue.trimmod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xiyue.trimmod.TrimMOD;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public final class ShaperSkillTimeBarRenderer {
    private static final ResourceLocation BAR =
            ResourceLocation.fromNamespaceAndPath(TrimMOD.MODID, "textures/gui/shaper_skill_time.png");

    private ShaperSkillTimeBarRenderer() {
    }

    static void renderBar(GuiGraphics guiGraphics, int currentTicks, int maxTicks) {
        if (currentTicks <= 0 || maxTicks <= 0) {
            return;
        }

        int x = guiGraphics.guiWidth() / 2 - 8;
        int y = guiGraphics.guiHeight() - 32 - 26;

        RenderSystem.setShaderTexture(0, BAR);
        RenderSystem.enableBlend();

        guiGraphics.blit(BAR, x, y, 0, 0, 15, 4, 15, 6);

        int fillWidth = Math.max(0, Math.min(13, (int) Math.round(13.0D * currentTicks / maxTicks)));
        if (fillWidth > 0) {
            guiGraphics.blit(BAR, x, y + 1, 0, 4, fillWidth + 1, 2, 15, 6);
        }

        RenderSystem.disableBlend();
    }
}
