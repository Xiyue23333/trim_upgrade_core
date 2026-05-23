package com.xiyue.trimmod.client;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.client.particle.NightStarParticle;
import com.xiyue.trimmod.client.particle.TideStarParticle;
import com.xiyue.trimmod.client.particle.TideStarTrailParticle;
import com.xiyue.trimmod.client.renderer.SentryFloatingEntityRenderer;
import com.xiyue.trimmod.client.renderer.SwampThornEntityRenderer;
import com.xiyue.trimmod.client.renderer.TrimUpgradeTableBlockEntityRenderer;
import com.xiyue.trimmod.common.registry.ModBlockEntityTypes;
import com.xiyue.trimmod.common.registry.ModParticles;
import com.xiyue.trimmod.core.init.EntityInit;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TrimMOD.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEventSubscriber {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {

        event.registerEntityRenderer(EntityInit.COAST_WAVE.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntityInit.DUNE_TORNADO.get(), NoopRenderer::new);
        event.registerEntityRenderer(EntityInit.SWAMP_THORN.get(), SwampThornEntityRenderer::new);

        event.registerEntityRenderer(EntityInit.SENTRY_FLOATING.get(), SentryFloatingEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntityTypes.TRIM_UPGRADE_TABLE.get(), TrimUpgradeTableBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.TIDE_STAR.get(), TideStarParticle.Provider::new);
        event.registerSpriteSet(ModParticles.NIGHT_STAR.get(), NightStarParticle.Provider::new);
        event.registerSpriteSet(ModParticles.TIDE_STAR_TRAIL.get(), TideStarTrailParticle.Provider::new);
    }
}
