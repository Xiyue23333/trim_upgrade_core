package com.xiyue.trimmod.common.event;

import com.mojang.logging.LogUtils;
import com.xiyue.trimmod.TrimMOD;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = TrimMOD.MODID)
public class VanillaTrimCopyRecipeDisabler {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Set<ResourceLocation> VANILLA_TRIM_COPY_RECIPE_IDS = new HashSet<>(List.of(
            mc("coast_armor_trim_smithing_template"),
            mc("dune_armor_trim_smithing_template"),
            mc("eye_armor_trim_smithing_template"),
            mc("host_armor_trim_smithing_template"),
            mc("raiser_armor_trim_smithing_template"),
            mc("rib_armor_trim_smithing_template"),
            mc("sentry_armor_trim_smithing_template"),
            mc("shaper_armor_trim_smithing_template"),
            mc("silence_armor_trim_smithing_template"),
            mc("snout_armor_trim_smithing_template"),
            mc("spire_armor_trim_smithing_template"),
            mc("tide_armor_trim_smithing_template"),
            mc("vex_armor_trim_smithing_template"),
            mc("ward_armor_trim_smithing_template"),
            mc("wayfinder_armor_trim_smithing_template"),
            mc("wild_armor_trim_smithing_template")
    ));

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        disable(event.getServer(), "ServerStartedEvent");
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        disable(event.getPlayerList().getServer(), "OnDatapackSyncEvent");
    }

    private static void disable(MinecraftServer server, String reason) {
        RecipeManager recipeManager = server.getRecipeManager();

        List<Recipe<?>> allRecipes = new ArrayList<>(recipeManager.getRecipes());
        List<Recipe<?>> filtered = allRecipes.stream()
                .filter(r -> !VANILLA_TRIM_COPY_RECIPE_IDS.contains(r.getId()))
                .toList();

        int removed = allRecipes.size() - filtered.size();
        if (removed <= 0) {
            return;
        }

        recipeManager.replaceRecipes(filtered);
        LOGGER.info("[{}] Disabled {} vanilla trim template copy recipes.", reason, removed);
    }

    private static ResourceLocation mc(String path) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", path);
    }
}
