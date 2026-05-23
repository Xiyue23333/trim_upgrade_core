package com.xiyue.trimmod.compat.jei;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.common.registry.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class TrimUpgradeJeiCategory implements IRecipeCategory<TrimUpgradeJeiRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TrimMOD.MODID, "trim_upgrade");
    public static final RecipeType<TrimUpgradeJeiRecipe> RECIPE_TYPE = new RecipeType<>(UID, TrimUpgradeJeiRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableStatic arrow;

    public TrimUpgradeJeiCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(168, 66);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModItems.TRIM_UPGRADE_TABLE.get()));
        this.arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public @NotNull RecipeType<TrimUpgradeJeiRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.trimupgrade.trim_upgrade.title");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TrimUpgradeJeiRecipe recipe, IFocusGroup focuses) {
        Ingredient armorIngredient = Ingredient.of(ItemTags.TRIMMABLE_ARMOR);

        builder.addSlot(RecipeIngredientRole.INPUT, 6, 38)
                .addIngredients(armorIngredient)
                .addTooltipCallback((slotView, tooltip) -> tooltip.add(
                        Component.translatable("jei.trimupgrade.trim_upgrade.tooltip.need_trim")));

        builder.addSlot(RecipeIngredientRole.INPUT, 54, 38)
                .addItemStack(recipe.requiredGem());

        int minItems = Mth.ceil((float) recipe.requiredEnergy() / (float) recipe.energyPerItem());
        ItemStack energyStack = recipe.energyItem().copy();
        energyStack.setCount(Math.max(1, minItems));
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 6)
                .addItemStack(energyStack)
                .addTooltipCallback((slotView, tooltip) -> {
                    tooltip.add(Component.translatable("jei.trimupgrade.trim_upgrade.tooltip.energy.cost", recipe.requiredEnergy()));
                    tooltip.add(Component.translatable("jei.trimupgrade.trim_upgrade.tooltip.energy.source", recipe.energyItem().getHoverName(), recipe.energyPerItem()));
                });

        ItemStack outputPreview = new ItemStack(Items.DIAMOND_CHESTPLATE);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 126, 38)
                .addItemStack(outputPreview)
                .addTooltipCallback((slotView, tooltip) -> tooltip.add(
                        Component.translatable("jei.trimupgrade.trim_upgrade.tooltip.level", recipe.fromLevel(), recipe.toLevel())));
    }

    @Override
    public void draw(TrimUpgradeJeiRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        guiGraphics.fill(2, 2, 166, 64, 0x22FFFFFF);
        guiGraphics.drawString(font,
                Component.translatable("jei.trimupgrade.trim_upgrade.line.level", recipe.fromLevel(), recipe.toLevel()),
                28, 6, 0x404040, false);
        guiGraphics.drawString(font,
                Component.translatable("jei.trimupgrade.trim_upgrade.line.energy", recipe.requiredEnergy()),
                28, 18, 0x404040, false);

        // Use JEI's standard recipe arrow drawable for consistent visuals
        int leftInputX = 54;
        int leftInputY = 38;
        int rightOutputX = 126;

        int arrowX = (leftInputX + 18) + ((rightOutputX - (leftInputX + 18) - arrow.getWidth()) / 2);
        int arrowY = (leftInputY + 9) - (arrow.getHeight() / 2);
        arrow.draw(guiGraphics, arrowX, arrowY);
    }
}
