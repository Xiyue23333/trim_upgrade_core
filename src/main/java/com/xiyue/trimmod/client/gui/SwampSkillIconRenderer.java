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

public final class SwampSkillIconRenderer {
    private static final ResourceLocation SWAMP_ICON =
            ResourceLocation.fromNamespaceAndPath(TrimMOD.MODID, "textures/gui/swamp_skill.png");
    private static final int ICON_SIZE = 22;

    private SwampSkillIconRenderer() {
    }

    static void renderIcon(GuiGraphics guiGraphics, Player player) {
        int slotX = guiGraphics.guiWidth() / 2 + 91;
        int slotY = guiGraphics.guiHeight() - 23;
        int x = slotX + (29 - ICON_SIZE) / 2;
        int y = slotY + (24 - ICON_SIZE) / 2;

        RenderSystem.setShaderTexture(0, SWAMP_ICON);
        RenderSystem.enableBlend();
        guiGraphics.blit(SWAMP_ICON, x, y, 0, 0.0f, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

        SkillIconCooldownMask.render(guiGraphics, x, y, ICON_SIZE, TrimSkillCooldowns.getSwampCooldownPercent(player));

        RenderSystem.disableBlend();
    }

    static boolean isSwampSet(Player player) {
        int tideCount = 0;
        int wildCount = 0;
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.isEmpty()) {
                continue;
            }
            var trim = ArmorTrim.getTrim(player.level().registryAccess(), armor);
            if (trim.isEmpty()) {
                continue;
            }
            if (trim.get().pattern().is(TrimPatterns.TIDE)) {
                tideCount++;
            } else if (trim.get().pattern().is(TrimPatterns.WILD)) {
                wildCount++;
            }
        }
        return tideCount >= 2 && wildCount >= 2;
    }
}
