package com.xiyue.trimmod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Display;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "trimupgrade", value = Dist.CLIENT)
public class ClientShaderHandler {
    private static final double SEARCH_RADIUS_XZ = 96.0;
    private static final double SEARCH_RADIUS_Y = 64.0;

    private static long lastQueryGameTime = Long.MIN_VALUE;
    private static Vec3 lastCamPos = Vec3.ZERO;
    private static final List<Display.ItemDisplay> cachedBlackHoles = new ArrayList<>();

    private static boolean isBlackHoleMarker(Entity entity) {
        if (entity.getTags().contains("SpireBlackHole")) return true;
        if (entity.getCustomName() == null) return false;
        return "SpireBlackHole".equals(entity.getCustomName().getString());
    }

    private static void refreshCache(Minecraft mc, Vec3 cam) {
        cachedBlackHoles.clear();
        AABB search = new AABB(cam.x, cam.y, cam.z, cam.x, cam.y, cam.z).inflate(SEARCH_RADIUS_XZ, SEARCH_RADIUS_Y, SEARCH_RADIUS_XZ);
        cachedBlackHoles.addAll(mc.level.getEntitiesOfClass(Display.ItemDisplay.class, search, ClientShaderHandler::isBlackHoleMarker));
    }

    @SubscribeEvent
    public static void renderBlackHole(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        PoseStack stack = event.getPoseStack();
        Vec3 cam = event.getCamera().getPosition();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();

        long gameTime = mc.level.getGameTime();
        if (gameTime != lastQueryGameTime || cam.distanceToSqr(lastCamPos) > 4.0) {
            refreshCache(mc, cam);
            lastQueryGameTime = gameTime;
            lastCamPos = cam;
        }

        for (Display.ItemDisplay entity : cachedBlackHoles) {
            if (entity.isRemoved()) continue;
            int age = entity.tickCount;
            float scale = Math.min(5.0f, 0.5f + age * 0.05f);

            stack.pushPose();
            stack.translate(entity.getX() - cam.x, entity.getY() - cam.y + 1.5, entity.getZ() - cam.z);
            stack.mulPose(event.getCamera().rotation());
            stack.scale(scale, scale, scale);

            VertexConsumer vc = buffer.getBuffer(ModRenderTypes.BLACK_HOLE);
            Matrix4f mat = stack.last().pose();
            vc.vertex(mat, -1, -1, 0).color(1f, 1f, 1f, 1f).uv(0, 1).endVertex();
            vc.vertex(mat, 1, -1, 0).color(1f, 1f, 1f, 1f).uv(1, 1).endVertex();
            vc.vertex(mat, 1, 1, 0).color(1f, 1f, 1f, 1f).uv(1, 0).endVertex();
            vc.vertex(mat, -1, 1, 0).color(1f, 1f, 1f, 1f).uv(0, 0).endVertex();

            stack.popPose();
        }
        buffer.endBatch(ModRenderTypes.BLACK_HOLE);
    }
}
