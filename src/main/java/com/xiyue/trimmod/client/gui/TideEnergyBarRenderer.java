package com.xiyue.trimmod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.common.util.TrimSkillCooldowns;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPatterns;

public class TideEnergyBarRenderer {
    private static final ResourceLocation TIDE_BAR = ResourceLocation.fromNamespaceAndPath(TrimMOD.MODID, "textures/gui/tide_energy.png");
    private static final int ICON_SIZE = 22;

    static void renderForPlayer(GuiGraphics guiGraphics, Player player) {
        if (isFullTideSet(player)) {
            renderBar(guiGraphics, player);
        }
    }

    static void renderBar(GuiGraphics guiGraphics, Player player) {
        int slotX = guiGraphics.guiWidth() / 2 + 91;
        int slotY = guiGraphics.guiHeight() - 23;
        int x = slotX + (29 - ICON_SIZE) / 2;
        int y = slotY + (24 - ICON_SIZE) / 2;

        RenderSystem.setShaderTexture(0, TIDE_BAR);
        RenderSystem.enableBlend();

        guiGraphics.blit(TIDE_BAR, x, y, 0, 0.0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        float cooldownPercent = Mth.clamp(TrimSkillCooldowns.getCooldownPercent(player, TrimPatterns.TIDE), 0.0f, 1.0f);
        SkillIconCooldownMask.render(guiGraphics, x, y, ICON_SIZE, cooldownPercent);
        RenderSystem.disableBlend();
    }

    static boolean isFullTideSet(Player player) {
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.isEmpty()) return false;
            var trim = ArmorTrim.getTrim(player.level().registryAccess(), armor);
            if (trim.isEmpty() || !trim.get().pattern().is(TrimPatterns.TIDE)) {
                return false;
            }
        }
        return true;
    }
}


