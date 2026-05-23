package com.xiyue.trimmod.client.event;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.client.gui.TrimEffectScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = TrimMOD.MODID, value = Dist.CLIENT)
public class ClientScreenEvents {

    private static final ResourceLocation TIDE_ICON =
            ResourceLocation.fromNamespaceAndPath(TrimMOD.MODID, "textures/gui/tide.png");

    private static final Map<CreativeModeInventoryScreen, ImageButton> CREATIVE_BUTTONS = new WeakHashMap<>();

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        // 在原版背包界面注入
        if (event.getScreen() instanceof InventoryScreen screen) {
            int leftPos = screen.getGuiLeft();
            int topPos = screen.getGuiTop();

            int buttonX = leftPos + 148;
            int buttonY = topPos + 4;

            ImageButton tideButton = new ImageButton(
                    buttonX, buttonY, 16, 16, 0, 0, 16, TIDE_ICON, 16, 32,
                    (btn) -> {
                        Minecraft.getInstance().setScreen(new TrimEffectScreen());
                    }
            );

            event.addListener(tideButton);
        } else if (event.getScreen() instanceof CreativeModeInventoryScreen screen) {
            int leftPos = screen.getGuiLeft();
            int topPos = screen.getGuiTop();

            // 创造背包顶部靠右
            int buttonX = leftPos + 173;
            int buttonY = topPos + 6;

            // 重复 init（例如切换标签页/分辨率），先移除旧按钮，避免叠加
            ImageButton old = CREATIVE_BUTTONS.remove(screen);
            if (old != null) {
                event.removeListener(old);
            }

            ImageButton tideButton = new ImageButton(
                    buttonX, buttonY, 16, 16, 0, 0, 16, TIDE_ICON, 16, 32,
                    (btn) -> Minecraft.getInstance().setScreen(new TrimEffectScreen())
            );

            boolean show = screen.isInventoryOpen();
            tideButton.visible = show;
            tideButton.active = show;

            CREATIVE_BUTTONS.put(screen, tideButton);
            event.addListener(tideButton);
        }
    }

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof CreativeModeInventoryScreen screen)) return;
        ImageButton btn = CREATIVE_BUTTONS.get(screen);
        if (btn == null) return;

        boolean show = screen.isInventoryOpen();
        btn.visible = show;
        btn.active = show;
    }
}
