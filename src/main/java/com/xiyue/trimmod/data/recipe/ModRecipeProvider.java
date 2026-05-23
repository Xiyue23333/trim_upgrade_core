package com.xiyue.trimmod.data.recipe;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.common.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> recipeOutput) {
        buildCoreRecipes(recipeOutput);
        buildTrimTemplateCopyRecipes(recipeOutput);
    }

    private void buildCoreRecipes(Consumer<FinishedRecipe> out) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MIRACLE_CRYSTAL.get())
                .pattern(" # ")
                .pattern("XUY")
                .pattern(" Z ")
                .define('#', Items.IRON_INGOT)
                .define('X', Items.GOLD_INGOT)
                .define('U', ModItems.MAGIC_CRYSTAL.get())
                .define('Y', Items.DIAMOND)
                .define('Z', Items.NETHERITE_SCRAP)
                .unlockedBy(getHasName(ModItems.MAGIC_CRYSTAL.get()), has(ModItems.MAGIC_CRYSTAL.get()))
                .save(out, id("miracle_crystal"));

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModItems.TRIM_UPGRADE_TABLE.get())
                .pattern(" Y ")
                .pattern("X#X")
                .pattern("###")
                .define('#', Items.POLISHED_BLACKSTONE)
                .define('X', Items.GOLD_INGOT)
                .define('Y', ModItems.MIRACLE_CRYSTAL.get())
                .unlockedBy(getHasName(ModItems.MIRACLE_CRYSTAL.get()), has(ModItems.MIRACLE_CRYSTAL.get()))
                .save(out, id("trim_upgrade_table"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.UPGRADE_GEM_i.get(), 2)
                .pattern(" # ")
                .pattern("#X#")
                .pattern(" # ")
                .define('#', Items.IRON_INGOT)
                .define('X', ModItems.MAGIC_CRYSTAL.get())
                .unlockedBy(getHasName(ModItems.MAGIC_CRYSTAL.get()), has(ModItems.MAGIC_CRYSTAL.get()))
                .save(out, id("upgrade_gem_i"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.UPGRADE_GEM_ii.get(), 2)
                .pattern(" # ")
                .pattern("#X#")
                .pattern(" # ")
                .define('#', Items.GOLD_INGOT)
                .define('X', ModItems.UPGRADE_GEM_i.get())
                .unlockedBy(getHasName(ModItems.UPGRADE_GEM_i.get()), has(ModItems.UPGRADE_GEM_i.get()))
                .save(out, id("upgrade_gem_ii"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.UPGRADE_GEM_iii.get(), 2)
                .pattern(" # ")
                .pattern("#X#")
                .pattern(" # ")
                .define('#', Items.DIAMOND)
                .define('X', ModItems.UPGRADE_GEM_ii.get())
                .unlockedBy(getHasName(ModItems.UPGRADE_GEM_ii.get()), has(ModItems.UPGRADE_GEM_ii.get()))
                .save(out, id("upgrade_gem_iii"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.UPGRADE_GEM_iv.get(), 2)
                .pattern(" # ")
                .pattern("CXC")
                .pattern(" # ")
                .define('#', Items.NETHERITE_SCRAP)
                .define('C', ModItems.MAGIC_CRYSTAL.get())
                .define('X', ModItems.UPGRADE_GEM_iii.get())
                .unlockedBy(getHasName(ModItems.UPGRADE_GEM_iii.get()), has(ModItems.UPGRADE_GEM_iii.get()))
                .save(out, id("upgrade_gem_iv"));
    }

    private void buildTrimTemplateCopyRecipes(Consumer<FinishedRecipe> out) {
        copyTrimTemplate(out, "coast_armor_trim_smithing_template", "heart_of_the_sea", ModItems.UPGRADE_GEM_iv.get());
        copyTrimTemplate(out, "dune_armor_trim_smithing_template", "sandstone", ModItems.UPGRADE_GEM_ii.get());
        copyTrimTemplate(out, "eye_armor_trim_smithing_template", "end_crystal", ModItems.UPGRADE_GEM_iv.get());
        copyTrimTemplate(out, "host_armor_trim_smithing_template", "terracotta", ModItems.UPGRADE_GEM_i.get());
        copyTrimTemplate(out, "raiser_armor_trim_smithing_template", "terracotta", ModItems.UPGRADE_GEM_i.get());
        copyTrimTemplate(out, "rib_armor_trim_smithing_template", "cobblestone", ModItems.UPGRADE_GEM_iv.get());
        copyTrimTemplate(out, "sentry_armor_trim_smithing_template", "cobblestone", ModItems.UPGRADE_GEM_iii.get());
        copyTrimTemplate(out, "shaper_armor_trim_smithing_template", "terracotta", ModItems.UPGRADE_GEM_ii.get());
        copyTrimTemplate(out, "silence_armor_trim_smithing_template", "echo_shard", ModItems.MIRACLE_CRYSTAL.get());
        copyTrimTemplate(out, "snout_armor_trim_smithing_template", "blackstone", ModItems.UPGRADE_GEM_iii.get());
        copyTrimTemplate(out, "spire_armor_trim_smithing_template", "dragon_breath", ModItems.MIRACLE_CRYSTAL.get());
        copyTrimTemplate(out, "tide_armor_trim_smithing_template", "conduit", ModItems.MIRACLE_CRYSTAL.get());
        copyTrimTemplate(out, "vex_armor_trim_smithing_template", "cobblestone", ModItems.UPGRADE_GEM_iv.get());
        copyTrimTemplate(out, "ward_armor_trim_smithing_template", "echo_shard", ModItems.UPGRADE_GEM_iii.get());
        copyTrimTemplate(out, "wayfinder_armor_trim_smithing_template", "terracotta", ModItems.UPGRADE_GEM_iii.get());
        copyTrimTemplate(out, "wild_armor_trim_smithing_template", "mossy_cobblestone", ModItems.UPGRADE_GEM_ii.get());
    }

    private void copyTrimTemplate(Consumer<FinishedRecipe> out, String templateIdPath, String centerItemPath, Item gemItem) {
        Item templateItem = mcItem(templateIdPath);
        Item centerItem = mcItem(centerItemPath);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, templateItem, 2)
                .pattern("#S#")
                .pattern("#C#")
                .pattern("#X#")
                .define('#', Items.DIAMOND)
                .define('S', templateItem)
                .define('C', centerItem)
                .define('X', gemItem)
                .unlockedBy(getHasName(templateItem), has(templateItem))
                .save(out, id(templateIdPath));
    }

    private static Item mcItem(String path) {
        Item item = ForgeRegistries.ITEMS.getValue(ResourceLocation.fromNamespaceAndPath("minecraft", path));
        return Objects.requireNonNull(item, "Missing vanilla item: minecraft:" + path);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(TrimMOD.MODID, path);
    }
}
