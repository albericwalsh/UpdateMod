package fr.broawz.updatemod.blocks;

import fr.broawz.updatemod.utils.References;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks extends Block {

    private final String unlocalizedName;
    private final String registeredName;

    public ModBlocks(String unlocalizedName, String registeredName) {
        super(Material.ROCK);
        this.unlocalizedName = unlocalizedName;
        this.registeredName = registeredName;
        initBlock();
        registerBlock(this);
        registerItemBlock();
        registerRender();

    }

    private void initBlock() {
        this.setRegistryName(new ResourceLocation(References.MODID, this.registeredName));
        this.setUnlocalizedName(this.unlocalizedName);
    }

    private void registerBlock(Block block) {
        GameRegistry.register(block);
    }

    private void registerItemBlock() {
        ItemBlock itemBlock = new ItemBlock(this);
        itemBlock.setRegistryName(getRegistryName());
        GameRegistry.register(itemBlock);
    }

    @SideOnly(Side.CLIENT)
    private void registerRender() {
        Item item = Item.getItemFromBlock(this);
        assert item != null;
        ModelLoader.setCustomModelResourceLocation(
                item,
                0,
                new ModelResourceLocation(getRegistryName(), "inventory")
        );
    }
}
