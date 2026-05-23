package com.xiyue.trimmod.data.tag;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.common.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TrimMOD.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.TRIM_UPGRADE_TABLE.get());
        tag(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.TRIM_UPGRADE_TABLE.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.MAGIC_CRYSTAL_ORE.get());
        tag(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.MAGIC_CRYSTAL_ORE.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.DEEPSLATE_MAGIC_CRYSTAL_ORE.get());
        tag(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.DEEPSLATE_MAGIC_CRYSTAL_ORE.get());
    }
}
