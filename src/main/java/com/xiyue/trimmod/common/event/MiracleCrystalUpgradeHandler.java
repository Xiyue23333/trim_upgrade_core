package com.xiyue.trimmod.common.event;

import com.xiyue.trimmod.TrimMOD;
import com.xiyue.trimmod.common.registry.ModBlocks;
import com.xiyue.trimmod.common.registry.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TrimMOD.MODID)
public class MiracleCrystalUpgradeHandler {
    @SubscribeEvent
    public static void onRightClickEnchantmentTable(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide) {
            return;
        }
        if (!level.getBlockState(event.getPos()).is(Blocks.ENCHANTING_TABLE)) {
            return;
        }

        ItemStack crystalStack = event.getEntity().getItemInHand(event.getHand());
        if (!crystalStack.is(ModItems.MIRACLE_CRYSTAL.get())) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (!event.getEntity().getAbilities().instabuild) {
            crystalStack.shrink(1);
        }
        level.setBlock(event.getPos(), ModBlocks.TRIM_UPGRADE_TABLE.get().defaultBlockState(), 3);
        level.playSound(null, event.getPos(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 0.95F + level.random.nextFloat() * 0.1F);
    }
}
