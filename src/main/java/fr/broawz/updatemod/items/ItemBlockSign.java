package fr.broawz.updatemod.items;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockSign extends ItemBlock {

    private final String[] variants;

    public ItemBlockSign(Block block, String[] variants) {
        super(block);
        this.variants = variants;
        setHasSubtypes(true); // Indique que ce ItemBlock a des métadonnées
        setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int meta = stack.getMetadata();
        if (meta < 0 || meta >= variants.length) meta = 0;
        return super.getUnlocalizedName() + "." + variants[meta];
    }

    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> items) {
        for (int i = 0; i < variants.length; i++) {
            items.add(new ItemStack(itemIn, 1, i));
        }
    }
}
