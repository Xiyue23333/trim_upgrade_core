package com.xiyue.trimmod.common.block.container;

import com.xiyue.trimmod.common.block.entity.TrimUpgradeTableBlockEntity;
import com.xiyue.trimmod.common.registry.ModBlocks;
import com.xiyue.trimmod.common.registry.ModItems;
import com.xiyue.trimmod.common.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class TrimUpgradeMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final BlockPos pos;
    private final Inventory playerInventory;

    public TrimUpgradeMenu(int containerId, Inventory inv, IItemHandler inventory, ContainerData data, BlockPos pos) {
        super(ModMenuTypes.TRIM_UPGRADE_MENU.get(), containerId);
        this.data = data;
        this.pos = pos;
        this.playerInventory = inv;

        // Slot 0: fuel slot (magic crystal only)
        this.addSlot(new SlotItemHandler(inventory, 0, 76, 19) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(ModItems.MAGIC_CRYSTAL.get());
            }
        });

        // Slot 1: armor slot (trimmed armor only)
        this.addSlot(new SlotItemHandler(inventory, 1, 27, 47) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) {
                return ArmorTrim.getTrim(inv.player.level().registryAccess(), stack).isPresent();
            }
        });

        // Slot 2: material slot (upgrade gems only)
        this.addSlot(new SlotItemHandler(inventory, 2, 76, 47) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(ModItems.UPGRADE_GEM_i.get()) ||
                        stack.is(ModItems.UPGRADE_GEM_ii.get()) ||
                        stack.is(ModItems.UPGRADE_GEM_iii.get()) ||
                        stack.is(ModItems.UPGRADE_GEM_iv.get());
            }
        });

        // Slot 3: output slot (no manual insert; consumes resources on take)
        this.addSlot(new SlotItemHandler(inventory, 3, 134, 47) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) { return false; }

            @Override
            public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
                super.onTake(player, stack);
            }
        });

        // Player inventory (slots 4-30)
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(inv, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
        // Hotbar (slots 31-39)
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inv, i, 8 + i * 18, 142));
        }

        addDataSlots(data);
    }

    public int getEnergyScaled() {
        int energy = this.data.get(0);
        int maxEnergy = this.data.get(1);
        return (maxEnergy != 0 && energy != 0) ? energy * 18 / maxEnergy : 0;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), pos), player, ModBlocks.TRIM_UPGRADE_TABLE.get());
    }

    /**
     * Handles shift-click item transfer.
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            // Move from machine slots into the player inventory.
            if (index < 4) {
                // Try moving the stack into the player inventory.
                if (!this.moveItemStackTo(itemstack1, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                // Mark quick craft when taking from the output slot.
                if (index == 3) {
                    slot.onQuickCraft(itemstack1, itemstack);
                    consumeUpgradeResources(player);
                }
            }
            // Move matching player inventory items into machine slots.
            else {
                // Fuel slot for magic crystals.
                if (itemstack1.is(ModItems.MAGIC_CRYSTAL.get())) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // Armor slot for trimmed armor.
                else if (ArmorTrim.getTrim(player.level().registryAccess(), itemstack1).isPresent()) {
                    if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // Material slot for upgrade gems.
                else if (isUpgradeGem(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 2, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // Move between main inventory and hotbar.
                else if (index >= 4 && index < 31) {
                    if (!this.moveItemStackTo(itemstack1, 31, 40, false)) return ItemStack.EMPTY;
                } else if (index >= 31 && index < 40) {
                    if (!this.moveItemStackTo(itemstack1, 4, 31, false)) return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    private boolean isUpgradeGem(ItemStack stack) {
        return stack.is(ModItems.UPGRADE_GEM_i.get()) ||
                stack.is(ModItems.UPGRADE_GEM_ii.get()) ||
                stack.is(ModItems.UPGRADE_GEM_iii.get()) ||
                stack.is(ModItems.UPGRADE_GEM_iv.get());
    }

    private void consumeUpgradeResources(Player player) {
        if (player.level().getBlockEntity(this.pos) instanceof TrimUpgradeTableBlockEntity be) {
            be.consumeResources();
        }
    }
}
