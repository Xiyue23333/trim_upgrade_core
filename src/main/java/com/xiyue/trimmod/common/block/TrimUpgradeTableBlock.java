package com.xiyue.trimmod.common.block;

import com.xiyue.trimmod.common.block.entity.TrimUpgradeTableBlockEntity;
import com.xiyue.trimmod.common.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter; // 新增
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext; // 新增
import net.minecraft.world.phys.shapes.VoxelShape; // 新增
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class TrimUpgradeTableBlock extends Block implements EntityBlock {

    // 自定义碰撞箱
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);

    public TrimUpgradeTableBlock() {
        super(Properties.of()
                .mapColor(MapColor.METAL)
                .strength(1.5F, 7.0F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL)
                .noOcclusion()
                .lightLevel((state) -> 10));
    }

    // 应用碰撞箱形状
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TrimUpgradeTableBlockEntity tableBe) {
                NetworkHooks.openScreen((ServerPlayer) player, tableBe, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TrimUpgradeTableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return type == ModBlockEntityTypes.TRIM_UPGRADE_TABLE.get() ?
                (lvl, p, st, be) -> TrimUpgradeTableBlockEntity.tick(lvl, p, st, (TrimUpgradeTableBlockEntity) be) : null;
    }
}
