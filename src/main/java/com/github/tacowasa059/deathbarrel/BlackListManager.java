package com.github.tacowasa059.deathbarrel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlackListManager {
    private static List<ResourceLocation> blacklist = new ArrayList<>();

    public static void loadBlacklist() {
        blacklist = Config.BLACKLIST_ITEMS.get().stream()
                .map(ResourceLocation::new)
                .collect(Collectors.toList());
    }

    public static boolean isBlacklisted(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return blacklist.contains(ForgeRegistries.ITEMS.getKey(stack.getItem()));
    }

    public static boolean addToBlacklist(String itemID) {
        List<String> currentList = new ArrayList<>(Config.BLACKLIST_ITEMS.get());
        if (currentList.contains(itemID)) {
            return false;
        }

        currentList.add(itemID);
        Config.BLACKLIST_ITEMS.set(currentList);
        loadBlacklist();
        return true;
    }

    public static boolean removeFromBlacklist(String itemID) {
        List<String> currentList = new ArrayList<>(Config.BLACKLIST_ITEMS.get());
        if (!currentList.contains(itemID)) {
            return false;
        }

        currentList.remove(itemID);
        Config.BLACKLIST_ITEMS.set(currentList);
        loadBlacklist();
        return true;
    }
}
