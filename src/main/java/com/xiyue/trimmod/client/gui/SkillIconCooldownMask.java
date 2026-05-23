package com.xiyue.trimmod.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

final class SkillIconCooldownMask {
    private static final int INNER_INSET = 3;

    private SkillIconCooldownMask() {
    }

    static void render(GuiGraphics guiGraphics, int x, int y, int iconSize, float cooldownPercent) {
        float clamped = Mth.clamp(cooldownPercent, 0.0f, 1.0f);
        if (clamped <= 0.0f) {
            return;
        }

        int innerX1 = x + INNER_INSET;
        int innerY1 = y + INNER_INSET;
        int innerX2 = x + iconSize - INNER_INSET;
        int innerY2 = y + iconSize - INNER_INSET;
        int innerHeight = innerY2 - innerY1;
        if (innerHeight <= 0) {
            return;
        }

        int overlayHeight = Mth.ceil(innerHeight * clamped);
        int overlayY = innerY2 - overlayHeight;

        guiGraphics.fill(innerX1, innerY1, innerX2, innerY2, 0x55D8D8D8);
        guiGraphics.fill(innerX1, overlayY, innerX2, innerY2, 0xA0F0F0F0);
    }
}
