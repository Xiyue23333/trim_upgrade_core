package com.xiyue.trimmod.common.block.entity;

import com.xiyue.trimmod.Config;
import com.xiyue.trimmod.common.block.container.TrimUpgradeMenu;
import com.xiyue.trimmod.common.registry.ModBlockEntityTypes;
import com.xiyue.trimmod.common.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TrimUpgradeTableBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.is(ModItems.MAGIC_CRYSTAL.get());
                case 1 -> level != null && net.minecraft.world.item.armortrim.ArmorTrim.getTrim(level.registryAccess(), stack).isPresent();
                case 2 -> stack.is(ModItems.UPGRADE_GEM_i.get()) ||
                        stack.is(ModItems.UPGRADE_GEM_ii.get()) ||
                        stack.is(ModItems.UPGRADE_GEM_iii.get()) ||
                        stack.is(ModItems.UPGRADE_GEM_iv.get());
                case 3 -> false;
                default -> false;
            };
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (!isItemValid(slot, stack)) {
                return stack;
            }
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 3 && !simulate) {
                consumeResources();
            }
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) { setChanged(); }
    };

    // Upgrade table energy storage.
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private int energy = 0;
    private int maxEnergy = 1500; // Maximum stored energy.

    public final ContainerData data = new ContainerData() {
        @Override public int get(int index) { return index == 0 ? energy : maxEnergy; }
        @Override public void set(int index, int value) { if(index == 0) energy = value; else maxEnergy = value; }
        @Override public int getCount() { return 2; }
    };

    public TrimUpgradeTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.TRIM_UPGRADE_TABLE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() { return Component.translatable("container.trim_upgrade_table"); }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new TrimUpgradeMenu(id, inv, this.inventory, this.data, this.worldPosition);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TrimUpgradeTableBlockEntity pEntity) {
        if (level.isClientSide) return;

        int cfgMaxEnergy = Math.max(1, Config.upgradeTableMaxEnergy);
        if (pEntity.maxEnergy != cfgMaxEnergy) {
            pEntity.maxEnergy = cfgMaxEnergy;
            if (pEntity.energy > pEntity.maxEnergy) {
                pEntity.energy = pEntity.maxEnergy;
            }
            pEntity.setChanged();
        }

        ItemStack fuelStack = pEntity.inventory.getStackInSlot(0);
        int fuelGain = Math.max(0, Config.fuelEnergyPerCrystal);
        if (fuelGain > 0 && fuelStack.is(ModItems.MAGIC_CRYSTAL.get()) && pEntity.energy + fuelGain <= pEntity.maxEnergy) {
            pEntity.energy += fuelGain;
            pEntity.inventory.extractItem(0, 1, false);
            pEntity.setChanged();
        }

        ItemStack armor = pEntity.inventory.getStackInSlot(1);
        ItemStack material = pEntity.inventory.getStackInSlot(2);
        ItemStack output = pEntity.inventory.getStackInSlot(3);

        if (!armor.isEmpty() && output.isEmpty() && !material.isEmpty()) {
            int currentLvl = getUpgradeLevel(armor);
            if (pEntity.canProduceOutput(armor, material, currentLvl)) {
                ItemStack preview = armor.copy();
                setUpgradeLevel(preview, currentLvl + 1);
                pEntity.inventory.setStackInSlot(3, preview);
            }
        }

        if (!output.isEmpty()) {
            int currentLvl = getUpgradeLevel(armor);
            if (armor.isEmpty() || material.isEmpty() || !pEntity.canProduceOutput(armor, material, currentLvl)) {
                pEntity.inventory.setStackInSlot(3, ItemStack.EMPTY);
            }
        }
    }

    // Reads the internal trim upgrade tag.
    public static int getUpgradeLevel(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("Trim")) {
            return stack.getTag().getCompound("Trim").getInt("TrimUpgradeLevel");
        }
        return 0;
    }

    private static void setUpgradeLevel(ItemStack stack, int level) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("Trim")) {
            tag.getCompound("Trim").putInt("TrimUpgradeLevel", level);
        }
    }

    private static int getRequiredEnergy(int currentLvl) {
        return Config.energyCostForUpgradeFromLevel(currentLvl);
    }

    private boolean canProduceOutput(ItemStack armor, ItemStack material, int currentLvl) {
        if (armor.isEmpty() || material.isEmpty() || currentLvl < 0 || currentLvl >= 4) {
            return false;
        }
        Item reqGem = getRequiredGem(currentLvl);
        int reqEnergy = getRequiredEnergy(currentLvl);
        return this.energy >= reqEnergy && material.is(reqGem);
    }

    // Required upgrade gem for the current level.
    private static Item getRequiredGem(int currentLvl) {
        return switch (currentLvl) {
            case 0 -> ModItems.UPGRADE_GEM_i.get();
            case 1 -> ModItems.UPGRADE_GEM_ii.get();
            case 2 -> ModItems.UPGRADE_GEM_iii.get();
            case 3 -> ModItems.UPGRADE_GEM_iv.get();
            default -> ModItems.UPGRADE_GEM_i.get();
        };
    }

/**
 * Consumes the resources required by the current upgrade.
 * Removes the input armor, gem, and matching energy cost.
 */
    public void consumeResources() {
        ItemStack armor = this.inventory.getStackInSlot(1);
        ItemStack material = this.inventory.getStackInSlot(2);
        int currentLvl = getUpgradeLevel(armor);
        if (!canProduceOutput(armor, material, currentLvl)) {
            return;
        }

        this.energy -= getRequiredEnergy(currentLvl);
        this.inventory.extractItem(1, 1, false);
        this.inventory.extractItem(2, 1, false);
        this.setChanged();

        if (this.level != null) {
            this.level.playSound(null, this.worldPosition, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 0.9F + this.level.random.nextFloat() * 0.2F);
        }
    }

    @Override public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return lazyItemHandler.cast();
        return super.getCapability(cap, side);
    }
    @Override public void onLoad() { super.onLoad(); lazyItemHandler = LazyOptional.of(() -> inventory); }
    @Override public void invalidateCaps() { super.invalidateCaps(); lazyItemHandler.invalidate(); }
    @Override protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", inventory.serializeNBT());
        tag.putInt("energy", energy);
        super.saveAdditional(tag);
    }
    @Override public void load(CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("inventory"));
        energy = tag.getInt("energy");
    }
}
