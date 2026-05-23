package com.xiyue.trimmod.common.registry;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.common.block.entity.TrimUpgradeTableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TrimMOD.MODID);

    public static final RegistryObject<BlockEntityType<TrimUpgradeTableBlockEntity>> TRIM_UPGRADE_TABLE =
            BLOCK_ENTITIES.register("trim_upgrade_table",
                    () -> BlockEntityType.Builder.of(TrimUpgradeTableBlockEntity::new,
                            ModBlocks.TRIM_UPGRADE_TABLE.get()).build(null));
}