package com.github.tacowasa059.deathbarrel.DeathBarrelBlocks;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModifiedChestMenu extends ChestMenu {

    public ModifiedChestMenu(MenuType<?> type, int containerId, Inventory playerInventory, Container container, int rows) {
        super(type, containerId, playerInventory, container, rows);
    }

    public static ModifiedChestMenu sixRows(int containerId, Inventory playerInventory, Container container) {
        return new ModifiedChestMenu(MenuType.GENERIC_9x6, containerId, playerInventory, container, 6);
    }

    @Override
    protected @NotNull Slot addSlot(@NotNull Slot slot) {
        if(this.slots.size() < 54){
            return super.addSlot(new Slot(slot.container, slot.getContainerSlot(), slot.x, slot.y) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return false;
                }

            });
        }else{
            return super.addSlot(slot);
        }

    }
}
