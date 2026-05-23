package com.xiyue.trimmod.common.registry;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.common.block.TrimUpgradeTableBlock;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.level.material.MapColor;

public class ModBlocks {
    public static final String MOD_ID = TrimMOD.MODID;
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

    public static final RegistryObject<Block> TRIM_UPGRADE_TABLE = BLOCKS.register("trim_upgrade_table",
            TrimUpgradeTableBlock::new);

    public static final RegistryObject<Block> MAGIC_CRYSTAL_ORE = BLOCKS.register("magic_crystal_ore",
            () -> new DropExperienceBlock(
                    Block.Properties.of()
                            .mapColor(MapColor.STONE)
                            .strength(3.0F, 3.0F)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.STONE),
                    UniformInt.of(1, 4)
            ));

    public static final RegistryObject<Block> DEEPSLATE_MAGIC_CRYSTAL_ORE = BLOCKS.register("deepslate_magic_crystal_ore",
            () -> new DropExperienceBlock(
                    Block.Properties.of()
                            .mapColor(MapColor.DEEPSLATE)
                            .strength(4.5F, 3.0F)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.DEEPSLATE),
                    UniformInt.of(1, 4)
            ));
}
