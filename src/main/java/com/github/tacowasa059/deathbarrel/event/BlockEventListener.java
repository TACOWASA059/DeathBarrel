package com.github.tacowasa059.deathbarrel.event;

import com.github.tacowasa059.deathbarrel.Config;
import com.github.tacowasa059.deathbarrel.DeathBarrel;
import com.github.tacowasa059.deathbarrel.DeathBarrelBlocks.DeathBarrelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeathBarrel.MODID)
public class BlockEventListener {
    @SubscribeEvent
    public static void onChestBreak(BlockEvent.BreakEvent event){
        BlockPos pos = event.getPos();
        if(event.getPlayer() == null) return;
        if(Config.Breakable.get()) return;
        if(event.getPlayer().level() instanceof ServerLevel level){
            if(level.getBlockEntity(pos) instanceof DeathBarrelBlockEntity){
                event.setCanceled(true);
            }
        }
    }
}
