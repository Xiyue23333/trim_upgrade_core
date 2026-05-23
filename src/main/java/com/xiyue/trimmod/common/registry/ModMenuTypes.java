package com.xiyue.trimmod.common.registry;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.common.block.container.TrimUpgradeMenu;
import com.xiyue.trimmod.common.block.entity.TrimUpgradeTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, TrimMOD.MODID);

    public static final RegistryObject<MenuType<TrimUpgradeMenu>> TRIM_UPGRADE_MENU = MENUS.register("trim_upgrade_menu",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                BlockEntity be = inv.player.level().getBlockEntity(pos);
                if (be instanceof TrimUpgradeTableBlockEntity tableBe) {
                    return new TrimUpgradeMenu(windowId, inv, tableBe.inventory, tableBe.data, pos);
                }
                return null;
            }));
}