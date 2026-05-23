package com.xiyue.trimmod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xiyue.trimmod.TrimMOD;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPatterns;

public class EnderEnergyBarRenderer {
    private static final ResourceLocation ENDER_BAR =
            ResourceLocation.fromNamespaceAndPath(TrimMOD.MODID, "textures/gui/ender_energy.png");

    static void renderBar(GuiGraphics guiGraphics, int charges) {
        int x = guiGraphics.guiWidth() / 2 - 9;
        int y = guiGraphics.guiHeight() - 32 - 22;

        RenderSystem.setShaderTexture(0, ENDER_BAR);
        RenderSystem.enableBlend();

        int validCharges = Math.max(0, Math.min(charges, 100));
        int frameIndex = 10 - (validCharges / 10);
        int vOffset = frameIndex * 17;

        guiGraphics.blit(ENDER_BAR, x, y, 0, (float) vOffset, 13, 17, 16, 187);
        RenderSystem.disableBlend();
    }

    static boolean isFullEyeSet(Player player) {
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.isEmpty()) return false;
            var trim = ArmorTrim.getTrim(player.level().registryAccess(), stack);
            if (trim.isEmpty() || !trim.get().pattern().is(TrimPatterns.EYE)) return false;
        }
        return true;
    }
}

