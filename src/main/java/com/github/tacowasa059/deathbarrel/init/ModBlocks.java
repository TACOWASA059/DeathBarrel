package com.github.tacowasa059.deathbarrel.init;

import com.github.tacowasa059.deathbarrel.DeathBarrel;
import com.github.tacowasa059.deathbarrel.DeathBarrelBlocks.DeathBarrelBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, DeathBarrel.MODID);

    public static final RegistryObject<Block> DEATH_BARREL = BLOCKS.register("death_barrel",
            () -> new DeathBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL).destroyTime(1f).explosionResistance(Float.MAX_VALUE), ModBlockEntities.DEATH_BARREL::get));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}