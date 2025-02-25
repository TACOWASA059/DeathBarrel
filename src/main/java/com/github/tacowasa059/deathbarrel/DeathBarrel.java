package com.github.tacowasa059.deathbarrel;

import com.github.tacowasa059.deathbarrel.init.ModBlockEntities;
import com.github.tacowasa059.deathbarrel.init.ModBlocks;
import com.github.tacowasa059.deathbarrel.init.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DeathBarrel.MODID)
public class DeathBarrel {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "deathbarrel";

    @SuppressWarnings("removal")
    public DeathBarrel() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ModBlocks.register(eventBus);
        ModItems.register(eventBus);
        ModBlockEntities.register(eventBus);

        eventBus.addListener(this::onConfigLoad);
    }

    private void onConfigLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == Config.SPEC) {
            BlackListManager.loadBlacklist();
        }
    }
}
