package com.xiyue.trimmod.client.event;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.client.KeyInit;
import com.xiyue.trimmod.client.gui.TrimEffectScreen;
import com.xiyue.trimmod.network.ModMessages;
import com.xiyue.trimmod.network.PacketActiveSkill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TrimMOD.MODID, value = Dist.CLIENT)
public class ClientInputEvents {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (KeyInit.SKILL_KEY.consumeClick()) {
                ModMessages.sendToServer(new PacketActiveSkill());
            }

            while (KeyInit.OPEN_TRIM_STATS_KEY.consumeClick()) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player == null) continue;
                if (mc.screen instanceof ChatScreen) continue;

                if (mc.screen instanceof TrimEffectScreen) {
                    mc.setScreen(null);
                } else if (mc.screen == null) {
                    mc.setScreen(new TrimEffectScreen());
                }
            }
        }
    }
}
