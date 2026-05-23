package com.xiyue.trimmod.client;

import com.xiyue.trimmod.TrimMOD;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = TrimMOD.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyInit {
    // General skill key, default V
    public static final KeyMapping SKILL_KEY = new KeyMapping(
            "key.trimmod.skill",
            GLFW.GLFW_KEY_V,
            "key.categories.trimmod"
    );

    // Open trim stats, default Z
    public static final KeyMapping OPEN_TRIM_STATS_KEY = new KeyMapping(
            "key.trimmod.open_trim_stats",
            GLFW.GLFW_KEY_Z,
            "key.categories.trimmod"
    );

    @SubscribeEvent
    public static void onRegister(RegisterKeyMappingsEvent event) {
        event.register(SKILL_KEY);
        event.register(OPEN_TRIM_STATS_KEY);
    }
}
