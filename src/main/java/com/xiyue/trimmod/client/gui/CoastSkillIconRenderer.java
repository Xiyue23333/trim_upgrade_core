package com.xiyue.trimmod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.common.util.TrimSkillCooldowns;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPatterns;

public final class CoastSkillIconRenderer {
    private static final ResourceLocation COAST_ICON =
            ResourceLocation.fromNamespaceAndPath(TrimMOD.MODID, "textures/gui/coast_skill.png");
    private static final int ICON_SIZE = 22;

    private CoastSkillIconRenderer() {
    }

    static void renderIcon(GuiGraphics guiGraphics, Player player) {
        int slotX = guiGraphics.guiWidth() / 2 + 91;
        int slotY = guiGraphics.guiHeight() - 23;
        int x = slotX + (29 - ICON_SIZE) / 2;
        int y = slotY + (24 - ICON_SIZE) / 2;

        RenderSystem.setShaderTexture(0, COAST_ICON);
        RenderSystem.enableBlend();
        guiGraphics.blit(COAST_ICON, x, y, 0, 0.0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

        SkillIconCooldownMask.render(guiGraphics, x, y, ICON_SIZE, getCooldownPercent(player));

        RenderSystem.disableBlend();
    }

    static float getCooldownPercent(Player player) {
        return TrimSkillCooldowns.getCooldownPercent(player, TrimPatterns.COAST);
    }

    static boolean isFullCoastSet(Player player) {
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.isEmpty()) return false;
            var trim = ArmorTrim.getTrim(player.level().registryAccess(), armor);
            if (trim.isEmpty() || !trim.get().pattern().is(TrimPatterns.COAST)) {
                return false;
            }
        }
        return true;
    }
}


