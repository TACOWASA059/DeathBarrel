package com.github.tacowasa059.deathbarrel.DeathBarrelBlocks;

import com.github.tacowasa059.deathbarrel.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class DeathBarrelBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    protected final Supplier<BlockEntityType<? extends DeathBarrelBlockEntity>> blockEntityType;

    public DeathBarrelBlock(BlockBehaviour.Properties p_49046_, Supplier<BlockEntityType<? extends DeathBarrelBlockEntity>> p_48678_) {
        super(p_49046_);
        this.blockEntityType = p_48678_;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, Boolean.FALSE));
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState blockState, Level level, @NotNull BlockPos p_49071_, @NotNull Player player, @NotNull InteractionHand interactionHand, @NotNull BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = level.getBlockEntity(p_49071_);
            if (blockentity instanceof DeathBarrelBlockEntity) {
                player.openMenu((DeathBarrelBlockEntity) blockentity);
                player.awardStat(Stats.OPEN_BARREL);
                PiglinAi.angerNearbyPiglins(player, true);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    public void onRemove(BlockState p_49076_, @NotNull Level p_49077_, @NotNull BlockPos p_49078_, BlockState p_49079_, boolean p_49080_) {
        if (!p_49076_.is(p_49079_.getBlock())) {
            BlockEntity blockentity = p_49077_.getBlockEntity(p_49078_);
            if (blockentity instanceof Container) {
                if(Config.dropItems.get()) Containers.dropContents(p_49077_, p_49078_, (Container)blockentity);
                p_49077_.updateNeighbourForOutputSignal(p_49078_, this);
            }

            super.onRemove(p_49076_, p_49077_, p_49078_, p_49079_, p_49080_);
        }
    }

    public BlockEntityType<? extends DeathBarrelBlockEntity> blockEntityType() {
        return this.blockEntityType.get();
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153055_, @NotNull BlockState blockState, @NotNull BlockEntityType<T> blockEntityType) {
        return p_153055_.isClientSide ? null :
                createTickerHelper(blockEntityType, this.blockEntityType(), DeathBarrelBlockEntity::tick);
    }

    @Override
    public void tick(@NotNull BlockState p_220758_, ServerLevel p_220759_, @NotNull BlockPos p_220760_, @NotNull RandomSource p_220761_) {
        BlockEntity blockentity = p_220759_.getBlockEntity(p_220760_);
        if (blockentity instanceof DeathBarrelBlockEntity) {
            ((DeathBarrelBlockEntity) blockentity).recheckOpen();
        }

    }
    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new DeathBarrelBlockEntity(blockPos, blockState);
    }
    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState p_49090_) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(@NotNull Level p_49052_, @NotNull BlockPos p_49053_, @NotNull BlockState p_49054_,
                            @Nullable LivingEntity p_49055_, ItemStack itemStack) {
        if (itemStack.hasCustomHoverName()) {
            BlockEntity blockentity = p_49052_.getBlockEntity(p_49053_);
            if (blockentity instanceof DeathBarrelBlockEntity) {
                ((DeathBarrelBlockEntity) blockentity).setCustomName(itemStack.getHoverName());
            }
        }

    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState p_49058_) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState p_49065_, Level p_49066_, @NotNull BlockPos p_49067_) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(p_49066_.getBlockEntity(p_49067_));
    }
    @Override
    public @NotNull BlockState rotate(BlockState p_49085_, Rotation p_49086_) {
        return p_49085_.setValue(FACING, p_49086_.rotate(p_49085_.getValue(FACING)));
    }
    @Override
    public @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49088_) {
        p_49088_.add(FACING, OPEN);
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_49048_) {
        return this.defaultBlockState().setValue(FACING, p_49048_.getNearestLookingDirection().getOpposite());
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        return false;
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
    }
}
