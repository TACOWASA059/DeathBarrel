/*
 * このファイルは Death Chest Mod (LGPL 2.1) を基に改変されています。
 * 改変者: tacowasa059
 * 改変日: 2025/02/25
 * 変更内容: チェストをmod固有の樽に変更、スロット数の変更
 */
package com.github.tacowasa059.deathbarrel.event;

import com.github.tacowasa059.deathbarrel.Config;
import com.github.tacowasa059.deathbarrel.DeathBarrel;
import com.github.tacowasa059.deathbarrel.DeathBarrelBlocks.DeathBarrelBlockEntity;
import com.github.tacowasa059.deathbarrel.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = DeathBarrel.MODID)
public class DeathBarrelEvent {

    @SubscribeEvent
    public static void onDeathEvent(LivingDropsEvent event) {
        Level level = event.getEntity().level();
        if(level.isClientSide)return; // only in client side
        if (event.getEntity() instanceof Player player) { // player
            List<NonNullList<ItemStack>> items = new ArrayList<>();
            for (ItemEntity item : event.getDrops()) {

                if (items.isEmpty()) items.add(NonNullList.create());
                if (items.get(items.size()-1).size() >= 54) items.add(NonNullList.create()); // 54個を超えると新しく作る
                items.get(items.size()-1).add(item.getItem());

            }
            if (items.size()>0) {
                BlockPos pos = BlockPos.containing(player.position());
                for (int i = pos.getY(); i < level.getMaxBuildHeight() - items.size(); i++) { // 上に積む
                    if (!canPlace(level, pos, items.size())){
                        pos = pos.above();
                        continue;
                    }
                    for (int j = 0; j < items.size(); j++) { //　アイテムの追加
                        setChest(level, pos.above(j), player, items.get(j));
                    }
                    if (Config.hasSkull.get()) { // プレイヤーヘッドの追加
                        if(level.isEmptyBlock(pos.above(items.size()))) {
                            level.setBlockAndUpdate(pos.above(items.size()), Blocks.PLAYER_HEAD.defaultBlockState());
                            SkullBlockEntity skull = new SkullBlockEntity(pos.above(items.size()), Blocks.PLAYER_HEAD.defaultBlockState());
                            skull.setOwner(player.getGameProfile());
                            level.setBlockEntity(skull);
                        }
                    }
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }


    private static boolean canPlace(Level level, BlockPos pos, int size) {
        for (int i = 0; i < size; i++) {
            Block block = level.getBlockState(pos.above(i)).getBlock();
            if (!(level.isEmptyBlock(pos)|| block instanceof BushBlock ||
                    block instanceof LiquidBlock || block instanceof BaseFireBlock)) return false;
        }
        return true;
    }


    private static void setChest(Level level, BlockPos pos, Player player, NonNullList<ItemStack> items) {
        BlockState blockState = ModBlocks.DEATH_BARREL.get().defaultBlockState();
        level.setBlockAndUpdate(pos, blockState);
        DeathBarrelBlockEntity te = new DeathBarrelBlockEntity(pos, blockState);

        if(Config.lockChest.get()) {
            CompoundTag nbt=te.saveWithoutMetadata();
            nbt.putString("Lock", player.getStringUUID());

            te.load(nbt);
            te.setChanged();
        }

        te.setCustomName(MutableComponent.create(new LiteralContents(player.getDisplayName().getString()+"'s Loot")));
        for (int slot = 0; slot<items.size(); slot++) te.setItem(slot, items.get(slot));
        level.setBlockEntity(te);

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRespawnEvent(PlayerEvent.Clone event) {
        Player player = event.getOriginal();

        if (!player.level().isClientSide) {
            if (event.isWasDeath() && Config.giveJournal.get()) {
                BlockPos pos = player.blockPosition();
                long time = player.level().getGameTime();

                //本のnbt
                CompoundTag nbt = new CompoundTag();
                nbt.putInt("generation", 3);
                nbt.putString("title", "Death Journal");
                nbt.putString("author", player.getDisplayName().getString());

                //本の中身
                StringBuilder contents = new StringBuilder("{\"text\":\"Death Time: "+time);
                contents.append("\\n\\nDimension: ").append(player.level().dimension().location()).append("\\n");
                if (Config.journalPos.get()) contents.append("\\nPosition: ").append(pos.getX()).append(", ").append(pos.getY()).append(", ").append(pos.getZ());
                contents.append("\\n\\nLocked: ").append(Config.lockChest.get());
                contents.append("\"}");

                ListTag list = new ListTag();
                list.add(StringTag.valueOf(contents.toString()));
                nbt.put("pages", list);
                ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);
                if (Config.lockChest.get()) nbt.putString("Key", player.getStringUUID()); //key情報をnbtに追加
                stack.setTag(nbt);


                if (!event.getEntity().getInventory().add(stack)) event.getEntity().drop(stack, true);
                event.getEntity().containerMenu.sendAllDataToRemote();
                event.getEntity().getInventory().setChanged();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void interactBlockEvent(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        Level level = player.level();
        if (!level.isClientSide) {
            BlockPos pos = event.getPos();
            if (level.getBlockEntity(pos) instanceof DeathBarrelBlockEntity deathBarrelBlockEntity) {

                CompoundTag nbt = deathBarrelBlockEntity.saveWithoutMetadata();
                String lock = nbt.getString("Lock");

                if (player.getStringUUID().equals(lock)) {
                    nbt.remove("Lock");
                    deathBarrelBlockEntity.load(nbt);
                    deathBarrelBlockEntity.setChanged();
                    player.displayClientMessage(MutableComponent.create(
                            new LiteralContents("Chest has been unlocked.")), true);
                    return;
                }
                ItemStack stack = player.getItemInHand(event.getHand());
                if (stack.getItem() == Items.WRITTEN_BOOK) {
                    CompoundTag stackNBT = stack.getTag();
                    if(stackNBT == null) return;

                    int gen = stackNBT.getInt("generation");
                    if (gen == 3) {
                        String key = stackNBT.getString("Key");
                        if (key.equals(lock)) {
                            nbt.remove("Lock");
                            deathBarrelBlockEntity.load(nbt);
                            deathBarrelBlockEntity.setChanged();
                            player.displayClientMessage(MutableComponent.create(
                                    new LiteralContents("Chest has been unlocked.")), true);
                        }
                    }
                }

            }
        }
    }
}
