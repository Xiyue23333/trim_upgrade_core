package com.xiyue.trimmod.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.xiyue.trimmod.TrimMOD;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = TrimMOD.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    private static ShaderInstance blackHoleShader;

    @SubscribeEvent
    public static void onShaderRegister(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(
                event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(TrimMOD.MODID, "black_hole"),
                DefaultVertexFormat.POSITION_COLOR_TEX
        ), shaderInstance -> {
            blackHoleShader = shaderInstance;
        });
    }

    public static ShaderInstance getBlackHoleShader() {
        return blackHoleShader;
    }
}
