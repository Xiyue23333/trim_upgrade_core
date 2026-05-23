package com.xiyue.trimmod.compat.jei;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.client.gui.TrimUpgradeScreen;
import com.xiyue.trimmod.common.registry.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@JeiPlugin
public class TrimUpgradeJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_UID = ResourceLocation.fromNamespaceAndPath(TrimMOD.MODID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new TrimUpgradeJeiCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(TrimUpgradeJeiCategory.RECIPE_TYPE, createRecipes());

        registration.addIngredientInfo(
                new ItemStack(ModItems.TRIM_UPGRADE_TABLE.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.trimupgrade.trim_upgrade.info.1"),
                Component.translatable("jei.trimupgrade.trim_upgrade.info.2"),
                Component.translatable("jei.trimupgrade.trim_upgrade.info.3")
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModItems.TRIM_UPGRADE_TABLE.get()), TrimUpgradeJeiCategory.RECIPE_TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        // 能量条位置：x+75, y+12；给一点余量方便点击
        registration.addRecipeClickArea(TrimUpgradeScreen.class, 73, 10, 24, 10, TrimUpgradeJeiCategory.RECIPE_TYPE);
    }

    private static List<TrimUpgradeJeiRecipe> createRecipes() {
        ItemStack energyItem = new ItemStack(ModItems.MAGIC_CRYSTAL.get());
        int energyPerItem = Math.max(1, Config.fuelEnergyPerCrystal);

        return List.of(
                new TrimUpgradeJeiRecipe(0, 1, Config.energyCostForUpgradeFromLevel(0), new ItemStack(ModItems.UPGRADE_GEM_i.get()), energyItem, energyPerItem),
                new TrimUpgradeJeiRecipe(1, 2, Config.energyCostForUpgradeFromLevel(1), new ItemStack(ModItems.UPGRADE_GEM_ii.get()), energyItem, energyPerItem),
                new TrimUpgradeJeiRecipe(2, 3, Config.energyCostForUpgradeFromLevel(2), new ItemStack(ModItems.UPGRADE_GEM_iii.get()), energyItem, energyPerItem),
                new TrimUpgradeJeiRecipe(3, 4, Config.energyCostForUpgradeFromLevel(3), new ItemStack(ModItems.UPGRADE_GEM_iv.get()), energyItem, energyPerItem)
        );
    }
}
