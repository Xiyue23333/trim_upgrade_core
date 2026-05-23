package com.xiyue.trimmod;

import com.mojang.logging.LogUtils;
import com.xiyue.trimmod.common.registry.*;
import com.xiyue.trimmod.core.init.EntityInit;
import com.xiyue.trimmod.core.init.ModEffects;
import com.xiyue.trimmod.network.ModMessages;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib.GeckoLib;

import org.slf4j.Logger;

@Mod(TrimMOD.MODID)
public class TrimMOD {
    public static final String MODID = "trimupgrade";
    private static final Logger LOGGER = LogUtils.getLogger();

    public TrimMOD() {
        GeckoLib.initialize();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntityTypes.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        ModEnchantments.ENCHANTMENTS.register(modEventBus);
        ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);

        ModMessages.register();

        ModEffects.EFFECTS.register(modEventBus);

        EntityInit.ENTITIES.register(modEventBus);

        ModCreativeModeTabs.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Common setup complete.");
        });
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                MenuScreens.register(ModMenuTypes.TRIM_UPGRADE_MENU.get(), com.xiyue.trimmod.client.gui.TrimUpgradeScreen::new);
                LOGGER.info("Client setup complete.");
            });
        }
    }
}
