package fr.broawz.updatemod.creativetabs;

import fr.broawz.updatemod.blocks.ModBlocksInit;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UpdatedCreativeTabs extends CreativeTabs {

    public UpdatedCreativeTabs(String label) {
        super(label);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return Items.DIAMOND; // Change this to the item you want as the tab icon
    }

    @Override
    public String getTabLabel() {
        return "Update Mod"; // Nom affiché dans l’interface
    }
}
