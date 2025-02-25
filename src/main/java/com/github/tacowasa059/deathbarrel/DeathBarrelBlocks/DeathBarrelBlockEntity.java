package com.github.tacowasa059.deathbarrel.DeathBarrelBlocks;

import com.github.tacowasa059.deathbarrel.Config;
import com.github.tacowasa059.deathbarrel.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Unique;

public class DeathBarrelBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(54, ItemStack.EMPTY);

    String NBT_Key = "ElapsedTime";
    int elapsed_time = 0;
    private int tickCounter = 0;

    private static final int TICKS_PER_SECOND = 20;
    private static final int death_chest$CheckingTick = 5;

    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        protected void onOpen(@NotNull Level p_155062_, @NotNull BlockPos p_155063_, @NotNull BlockState p_155064_) {
            DeathBarrelBlockEntity.this.playSound(p_155064_, SoundEvents.BARREL_OPEN);
            DeathBarrelBlockEntity.this.updateBlockState(p_155064_, true);
        }

        protected void onClose(@NotNull Level p_155072_, @NotNull BlockPos p_155073_, @NotNull BlockState p_155074_) {
            DeathBarrelBlockEntity.this.playSound(p_155074_, SoundEvents.BARREL_CLOSE);
            DeathBarrelBlockEntity.this.updateBlockState(p_155074_, false);
        }

        protected void openerCountChanged(@NotNull Level p_155066_, @NotNull BlockPos p_155067_, @NotNull BlockState p_155068_, int p_155069_, int p_155070_) {
        }

        protected boolean isOwnContainer(Player player) {
            if (player.containerMenu instanceof ChestMenu) {
                Container container = ((ChestMenu)player.containerMenu).getContainer();
                return container == DeathBarrelBlockEntity.this;
            } else {
                return false;
            }
        }
    };

    public DeathBarrelBlockEntity(BlockPos p_155052_, BlockState p_155053_) {
        super(ModBlockEntities.DEATH_BARREL.get(), p_155052_, p_155053_);
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        if (!this.trySaveLootTable(compoundTag)) {
            ContainerHelper.saveAllItems(compoundTag, this.items);
        }

        compoundTag.putInt(NBT_Key, elapsed_time);

    }
    @Override
    public void load(@NotNull CompoundTag compoundTag) {
        super.load(compoundTag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(compoundTag)) {
            ContainerHelper.loadAllItems(compoundTag, this.items);
        }
        if(compoundTag.contains(NBT_Key)){
            elapsed_time = compoundTag.getInt(NBT_Key);
        }

    }
    @Override
    public int getContainerSize() {
        return 54;
    }
    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.items;
    }
    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> itemStacks) {
        this.items = itemStacks;
    }
    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.barrel");
    }
    @Override

    protected @NotNull AbstractContainerMenu createMenu(int p_58598_, @NotNull Inventory p_58599_) {
        return ModifiedChestMenu.sixRows(p_58598_, p_58599_, this);
    }
    @Override
    public void startOpen(@NotNull Player p_58616_) {
        if (!this.remove && !p_58616_.isSpectator()) {
            if(this.getLevel()==null) return;
            this.openersCounter.incrementOpeners(p_58616_, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }
    @Override
    public void stopOpen(@NotNull Player p_58614_) {
        if (!this.remove && !p_58614_.isSpectator()) {
            if(this.getLevel()==null) return;
            this.openersCounter.decrementOpeners(p_58614_, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }
    @Override
    public boolean canPlaceItem(int p_18952_, @NotNull ItemStack stack) { // ホッパーで入れることはできない
        return false;
    }


    public void recheckOpen() {
        if (!this.remove) {
            if(this.getLevel()==null) return;
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }

    }

    void updateBlockState(BlockState p_58607_, boolean b) {
        if(this.getLevel()==null) return;
        this.level.setBlock(this.getBlockPos(), p_58607_.setValue(DeathBarrelBlock.OPEN, b), 3);
    }

    void playSound(BlockState p_58601_, SoundEvent p_58602_) {
        Vec3i vec3i = p_58601_.getValue(DeathBarrelBlock.FACING).getNormal();
        double d0 = (double)this.worldPosition.getX() + 0.5D + (double)vec3i.getX() / 2.0D;
        double d1 = (double)this.worldPosition.getY() + 0.5D + (double)vec3i.getY() / 2.0D;
        double d2 = (double)this.worldPosition.getZ() + 0.5D + (double)vec3i.getZ() / 2.0D;
        if(this.level==null) return;
        this.level.playSound(null, d0, d1, d2, p_58602_, SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
    }

    public static void tick(Level level, BlockPos pos, BlockState blockState, DeathBarrelBlockEntity blockEntity){
        ServerLevel serverLevel = (ServerLevel) level;

        int tickCounter = blockEntity.tickCounter;
        tickCounter++;

        if (tickCounter >= TICKS_PER_SECOND) { // 規定時間が経過すると消える
            tickCounter = 0;

            int time = blockEntity.elapsed_time;
//            System.out.println("Chest at " + pos + " Elapsed Time: " + time);
            blockEntity.elapsed_time += 1;

            int threshold = Config.timeToErase.get();
            if (time + 1 >= threshold && threshold != -1) {
                removeBlocks(level, pos, serverLevel);
            }
        }

        if(blockEntity.elapsed_time > 0 && tickCounter % death_chest$CheckingTick == 0 && Config.EmptyErase.get()){
            if (blockEntity.isEmpty()) { // 空の時に消える
                removeBlocks(level, pos, serverLevel);
                return;
            }
        }
        blockEntity.tickCounter = tickCounter;
    }

    @Unique
    private static void removeBlocks(Level level, BlockPos pos, ServerLevel serverLevel) {
        serverLevel.removeBlockEntity(pos);
        serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3); // client and neighbor
        BlockPos pos_above = pos.above();
        if(level.getBlockState(pos_above).is(Blocks.PLAYER_HEAD)){
            serverLevel.setBlock(pos_above, Blocks.AIR.defaultBlockState(), 3); // client and neighbor
        }
    }


}