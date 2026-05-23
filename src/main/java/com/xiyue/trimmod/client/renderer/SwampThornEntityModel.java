package com.xiyue.trimmod.client.renderer;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.entity.SwampThornEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SwampThornEntityModel extends GeoModel<SwampThornEntity> {
    @Override
    public ResourceLocation getModelResource(SwampThornEntity animatable) {
        return new ResourceLocation(TrimMOD.MODID, "geo/swamp_thorn.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SwampThornEntity animatable) {
        return new ResourceLocation(TrimMOD.MODID, "textures/entity/swamp_thorn.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SwampThornEntity animatable) {
        return new ResourceLocation(TrimMOD.MODID, "animations/swamp_thorn.animation.json");
    }
}
