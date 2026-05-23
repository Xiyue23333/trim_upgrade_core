package com.xiyue.trimmod.client.gui;

import com.xiyue.trimmod.common.data.PersistentDataKeys;
import com.xiyue.trimmod.TrimMOD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TrimMOD.MODID, value = Dist.CLIENT)
public final class HudEnergyBarsOverlay {
    private HudEnergyBarsOverlay() {}

    private static final ResourceLocation CHAT_PANEL_OVERLAY_ID =
            ResourceLocation.fromNamespaceAndPath("minecraft", "chat_panel");

    private static boolean renderedViaChatPanel = false;

    @SubscribeEvent
    public static void onRenderGuiPre(RenderGuiEvent.Pre event) {
        renderedViaChatPanel = false;
    }

    @SubscribeEvent
    public static void onRenderGuiOverlayPre(RenderGuiOverlayEvent.Pre event) {
        if (!CHAT_PANEL_OVERLAY_ID.equals(event.getOverlay().id())) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        if (mc.screen instanceof ChatScreen) return;

        renderAll(event.getGuiGraphics(), mc.player);
        renderedViaChatPanel = true;
    }

    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        if (mc.screen instanceof ChatScreen) return;
        if (renderedViaChatPanel) return;

        renderAll(event.getGuiGraphics(), mc.player);
    }

    private static void renderAll(net.minecraft.client.gui.GuiGraphics guiGraphics, Player player) {
        if (TideEnergyBarRenderer.isFullTideSet(player)) {
            TideEnergyBarRenderer.renderBar(guiGraphics, player);
        }

        if (SpireEnergyBarRenderer.isFullSpireSet(player)) {
            SpireEnergyBarRenderer.renderBar(guiGraphics, player);
        }

        if (WayfinderEnergyBarRenderer.isFullWayfinderSet(player)) {
            WayfinderEnergyBarRenderer.renderBar(guiGraphics, player);
        }

        if (VexEnergyBarRenderer.isFullVexSet(player)) {
            VexEnergyBarRenderer.renderBar(guiGraphics, player);
        }

        SnoutSkillIconRenderHelper.renderIfNeeded(guiGraphics, player);
        CoastSkillIconRenderHelper.renderIfNeeded(guiGraphics, player);
        DuneSkillIconRenderHelper.renderIfNeeded(guiGraphics, player);
        SwampSkillIconRenderHelper.renderIfNeeded(guiGraphics, player);

        int vexSkillTicks = VexSkillTimeBarRenderer.getCurrentTicks(player);
        int vexSkillMaxTicks = VexSkillTimeBarRenderer.getMaxTicks(player);
        if (vexSkillTicks > 0 && vexSkillMaxTicks > 0) {
            VexSkillTimeBarRenderer.renderBar(guiGraphics, vexSkillTicks, vexSkillMaxTicks);
        }

        boolean hasShaperSet = ShaperEnergyBarRenderer.isFullShaperSet(player);
        int shaperEnergy = player.getPersistentData().contains(PersistentDataKeys.SHAPER_ENERGY)
                ? player.getPersistentData().getInt(PersistentDataKeys.SHAPER_ENERGY)
                : (hasShaperSet ? 100 : 0);
        if (hasShaperSet || shaperEnergy > 0) {
            ShaperEnergyBarRenderer.renderBar(guiGraphics, shaperEnergy);
        }

        int shaperSkillTicks = player.getPersistentData().getInt(PersistentDataKeys.IRON_WALL_TIMER);
        int shaperSkillMaxTicks = player.getPersistentData().getInt(PersistentDataKeys.IRON_WALL_MAX_TIMER);
        if (shaperSkillTicks > 0 && shaperSkillMaxTicks > 0) {
            ShaperSkillTimeBarRenderer.renderBar(guiGraphics, shaperSkillTicks, shaperSkillMaxTicks);
        }

        if (EnderEnergyBarRenderer.isFullEyeSet(player)) {
            int charges = player.getPersistentData().getInt(PersistentDataKeys.EYE_CHARGE_COUNT);
            EnderEnergyBarRenderer.renderBar(guiGraphics, charges);
        }

        int ribCharges = player.getPersistentData().getInt(PersistentDataKeys.RIB_CHARGE_COUNT);
        if (RibBarRenderer.isFullSet(player) || ribCharges > 0) {
            RibBarRenderer.renderBar(guiGraphics, ribCharges);
        }

        int silenceCharges = player.getPersistentData().getInt(PersistentDataKeys.SILENCE_CHARGE_COUNT);
        if (SilenceBarRenderer.isFullSet(player) || silenceCharges > 0) {
            SilenceBarRenderer.renderBar(guiGraphics, silenceCharges);
        }
    }
}


