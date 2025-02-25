package com.github.tacowasa059.deathbarrel.init;

import com.github.tacowasa059.deathbarrel.DeathBarrel;
import com.github.tacowasa059.deathbarrel.DeathBarrelBlocks.DeathBarrelBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DeathBarrel.MODID);

    public static final RegistryObject<BlockEntityType<DeathBarrelBlockEntity>> DEATH_BARREL =
            BLOCK_ENTITIES.register("death_barrel",
                    () -> BlockEntityType.Builder.of(DeathBarrelBlockEntity::new, ModBlocks.DEATH_BARREL.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
