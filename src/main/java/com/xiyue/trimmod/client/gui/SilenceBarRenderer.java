package com.xiyue.trimmod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xiyue.trimmod.TrimMOD;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPatterns;

public class SilenceBarRenderer {
    private static final ResourceLocation ECHO_BAR =
            ResourceLocation.fromNamespaceAndPath(TrimMOD.MODID, "textures/gui/echo_energy.png");

    static void renderBar(GuiGraphics guiGraphics, int charges) {
        int x = guiGraphics.guiWidth() / 2 - 6;
        int y = guiGraphics.guiHeight() - 32 - 20;

        RenderSystem.setShaderTexture(0, ECHO_BAR);
        RenderSystem.enableBlend();

        int validCharges = Math.max(0, Math.min(charges, 4));
        int vOffset = validCharges * 16;

        guiGraphics.blit(ECHO_BAR, x, y, 0, (float) vOffset, 10, 16, 10, 80);
        RenderSystem.disableBlend();
    }

    static boolean isFullSet(Player player) {
        int count = 0;
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.isEmpty()) continue;
            var trim = ArmorTrim.getTrim(player.level().registryAccess(), stack);
            if (trim.isPresent() && trim.get().pattern().is(TrimPatterns.SILENCE)) {
                count++;
            }
        }
        return count == 4;
    }
}

