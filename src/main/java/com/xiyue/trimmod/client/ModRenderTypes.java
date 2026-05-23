package com.xiyue.trimmod.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.xiyue.trimmod.TrimMOD;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ModRenderTypes extends RenderType {
    public ModRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static final RenderType BLACK_HOLE = create("black_hole_render",
            DefaultVertexFormat.POSITION_COLOR_TEX, // 必须匹配 Shader 的 attributes
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    // 1. 指定 ClientModEvents 里 Shader
                    .setShaderState(new RenderStateShard.ShaderStateShard(ClientModEvents::getBlackHoleShader))
                    // 2. 指定黑洞的底图 (end_portal.png)
                    .setTextureState(new RenderStateShard.TextureStateShard(ResourceLocation.fromNamespaceAndPath(TrimMOD.MODID, "textures/entity/black_hole_stars.png"), false, false))
                    // 3. 混合模式：如果是黑洞，建议使用 TRANSLUCENT（半透明）
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(NO_DEPTH_TEST)
                    // 4. 关闭剔除：让黑洞转到背面时也能看到
                    .setCullState(NO_CULL)
                    // 5. 写入深度限制：防止黑洞穿模或被奇怪地遮挡
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false));
}
