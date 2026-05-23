package com.xiyue.trimmod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xiyue.trimmod.TrimMOD;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPatterns;

public class RibBarRenderer {
    private static final ResourceLocation RIB_BAR =
            ResourceLocation.fromNamespaceAndPath(TrimMOD.MODID, "textures/gui/rib_bar.png");

    static void renderBar(GuiGraphics guiGraphics, int charges) {
        int x = guiGraphics.guiWidth() / 2 - 4;
        int y = guiGraphics.guiHeight() - 32 - 20;
        x -= 2;

        RenderSystem.setShaderTexture(0, RIB_BAR);
        RenderSystem.enableBlend();

        int validCharges = Math.max(0, Math.min(charges, 8));
        int vOffset = (8 - validCharges) * 16;
        guiGraphics.blit(RIB_BAR, x, y, 0, (float) vOffset, 10, 16, 10, 144);

        RenderSystem.disableBlend();
    }

    static boolean isFullSet(Player player) {
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.isEmpty()) return false;
            var trim = ArmorTrim.getTrim(player.level().registryAccess(), stack);
            if (trim.isEmpty() || !trim.get().pattern().is(TrimPatterns.RIB)) {
                return false;
            }
        }
        return true;
    }
}

