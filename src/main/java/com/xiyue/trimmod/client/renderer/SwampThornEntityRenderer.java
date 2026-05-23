package com.xiyue.trimmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xiyue.trimmod.entity.SwampThornEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SwampThornEntityRenderer extends GeoEntityRenderer<SwampThornEntity> {
    public SwampThornEntityRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SwampThornEntityModel());
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(SwampThornEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        float age = entity.tickCount + partialTick;
        float pulse = 1.0F + Mth.sin(age * 0.15F) * 0.02F;
        poseStack.scale(1.1F * pulse, 1.1F * pulse, 1.1F * pulse);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(SwampThornEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation(SwampThornEntity entity) {
        return new ResourceLocation("trimupgrade", "textures/entity/swamp_thorn.png");
    }
}
