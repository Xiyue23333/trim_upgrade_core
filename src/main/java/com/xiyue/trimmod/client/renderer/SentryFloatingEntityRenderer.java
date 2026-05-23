package com.xiyue.trimmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.xiyue.trimmod.entity.SentryFloatingEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SentryFloatingEntityRenderer extends EntityRenderer<SentryFloatingEntity> {
    private final net.minecraft.client.renderer.entity.ItemRenderer itemRenderer;
    private static final ItemStack NETHER_STAR_STACK = new ItemStack(Items.NETHER_STAR);

    public SentryFloatingEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(SentryFloatingEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        float age = entity.tickCount + partialTicks;
        float bob = Mth.sin(age * 0.1F) * 0.05F;
        poseStack.translate(0.0D, 0.15D + bob, 0.0D);

        poseStack.mulPose(Axis.YP.rotationDegrees(age * 4.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.scale(0.7F, 0.7F, 0.7F);

        itemRenderer.renderStatic(
                NETHER_STAR_STACK,
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                entity.level(),
                entity.getId()
        );

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SentryFloatingEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

