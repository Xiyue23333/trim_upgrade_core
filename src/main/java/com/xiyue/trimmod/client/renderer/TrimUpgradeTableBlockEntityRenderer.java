package com.xiyue.trimmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.xiyue.trimmod.common.block.entity.TrimUpgradeTableBlockEntity;
import com.xiyue.trimmod.common.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class TrimUpgradeTableBlockEntityRenderer implements BlockEntityRenderer<TrimUpgradeTableBlockEntity> {
    private final net.minecraft.client.renderer.entity.ItemRenderer itemRenderer;

    public TrimUpgradeTableBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(TrimUpgradeTableBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (blockEntity.getLevel() == null) return;

        float time = (blockEntity.getLevel().getGameTime() + partialTick);
        float bob = Mth.sin(time * 0.1F) * 0.04F;
        float rot = (time * 4.0F) % 360.0F;

        ItemStack stack = new ItemStack(ModItems.MIRACLE_CRYSTAL.get());

        long gt = blockEntity.getLevel().getGameTime();
        if (blockEntity.getLevel().isClientSide && ((gt + blockEntity.getBlockPos().asLong()) % 12L == 0L)) {
            if (blockEntity.getLevel().random.nextFloat() < 0.10f) {
                double cx = blockEntity.getBlockPos().getX() + 0.5D;
                double cy = blockEntity.getBlockPos().getY() + 1.05D;
                double cz = blockEntity.getBlockPos().getZ() + 0.5D;

                double px = cx + (blockEntity.getLevel().random.nextDouble() - 0.5D) * 0.8D;
                double py = cy + blockEntity.getLevel().random.nextDouble() * 0.40D;
                double pz = cz + (blockEntity.getLevel().random.nextDouble() - 0.5D) * 0.8D;

                double vx = (blockEntity.getLevel().random.nextDouble() - 0.5D) * 0.02D;
                double vz = (blockEntity.getLevel().random.nextDouble() - 0.5D) * 0.02D;
                blockEntity.getLevel().addParticle(ParticleTypes.END_ROD, px, py, pz, vx, 0.01D, vz);
                if (blockEntity.getLevel().random.nextFloat() < 0.06f) {
                    blockEntity.getLevel().addParticle(ParticleTypes.ENCHANT, px, py, pz, vx, 0.02D, vz);
                }
            }
        }

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.12D + bob, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(rot));
        poseStack.scale(0.6F, 0.6F, 0.6F);

        itemRenderer.renderStatic(
                stack,
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                blockEntity.getLevel(),
                blockEntity.getBlockPos().hashCode()
        );

        poseStack.popPose();
    }
}
