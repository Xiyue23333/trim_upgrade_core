package com.xiyue.trimmod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xiyue.trimmod.TrimMOD;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPatterns;

public class ShaperEnergyBarRenderer {
    private static final ResourceLocation SHAPER_BAR =
            ResourceLocation.fromNamespaceAndPath(TrimMOD.MODID, "textures/gui/shaper_energy.png");

    static void renderBar(GuiGraphics guiGraphics, int energy) {
        int x = guiGraphics.guiWidth() / 2 - 5;
        int y = guiGraphics.guiHeight() - 32 - 20;

        RenderSystem.setShaderTexture(0, SHAPER_BAR);
        RenderSystem.enableBlend();

        int validEnergy = Math.max(0, Math.min(energy, 100));
        int frameIndex = 5 - (validEnergy / 20);
        int frameSize = 16;
        int vOffset = frameIndex * frameSize;

        guiGraphics.blit(SHAPER_BAR, x, y, 0, (float) vOffset, 10, frameSize, 10, 96);
        RenderSystem.disableBlend();
    }

    static boolean isFullShaperSet(Player player) {
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.isEmpty()) return false;
            var trim = ArmorTrim.getTrim(player.level().registryAccess(), armor);
            if (trim.isEmpty() || !trim.get().pattern().is(TrimPatterns.SHAPER)) {
                return false;
            }
        }
        return true;
    }
}
