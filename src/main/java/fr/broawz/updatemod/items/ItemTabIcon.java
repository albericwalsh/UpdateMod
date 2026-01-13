package fr.broawz.updatemod.items;

import net.minecraft.item.Item;

public class ItemTabIcon extends Item {

    public ItemTabIcon() {
        setRegistryName("tab_icon");
        setUnlocalizedName("tab_icon");
        setMaxStackSize(1);
        // ⚠️ PAS de setCreativeTab ici
    }
}
