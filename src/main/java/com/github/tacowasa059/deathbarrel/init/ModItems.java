package com.github.tacowasa059.deathbarrel.init;

import com.github.tacowasa059.deathbarrel.DeathBarrel;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, DeathBarrel.MODID);

    public static final RegistryObject<Item> DEATH_BARREL = ITEMS.register("death_barrel",
            () -> new BlockItem(ModBlocks.DEATH_BARREL.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

