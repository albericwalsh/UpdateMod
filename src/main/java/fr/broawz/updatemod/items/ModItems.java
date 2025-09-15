package fr.broawz.updatemod.items;

import fr.broawz.updatemod.utils.References;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public abstract class ModItems extends Item {

    private final String unlocalizedName;
    private final String registryName;
    private final boolean glowing;
    public String[] subTypes;

    public ModItems(String unlocalizedName, String registryName, boolean glowing, String[] subTypes) {
        super();
        this.subTypes = subTypes;
        this.unlocalizedName = unlocalizedName;
        this.registryName = registryName;
        this.glowing = glowing;
        initItem();
        registerItem(this);
        registerRenderers();
    }

    public ModItems(String unlocalizedName, String registryName, boolean glowing) {
        this(unlocalizedName, registryName, glowing, null);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return glowing;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (this.subTypes != null && this.subTypes.length > 0) {
            int metadata = stack.getItemDamage();
            if (metadata < 0 || metadata >= this.subTypes.length) {
                metadata = 0;
            }
            return super.getUnlocalizedName(stack) + "_" + this.subTypes[metadata];
        } else {
            return super.getUnlocalizedName(stack);
        }
    }


    @Override
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        if (this.getCreativeTab() == tab) {
            if (this.subTypes != null && this.subTypes.length > 0) {
                for (int i = 0; i < this.subTypes.length; i++) {
                    subItems.add(new ItemStack(this, 1, i));
                }
            } else {
                subItems.add(new ItemStack(this));
            }
        }
    }


    private void initItem() {
        this.setRegistryName(this.registryName);
        this.setUnlocalizedName(this.unlocalizedName);
        this.setHasSubtypes(this.subTypes != null && this.subTypes.length != 0); // pas de sous-types, pas de meta
    }

    @SideOnly(Side.CLIENT)
    public void registerRenderers() {
        if (this.subTypes == null || this.subTypes.length == 0) {
            registerSimpleRender(this);
        } else {
            for (int i = 0; i < this.subTypes.length; i++) {
                registerVariantRender(this, i);
            }
        }
    }

    private void registerItem(Item item) {
        GameRegistry.register(item);
    }

    private void registerSimpleRender(Item item) {
        ModelLoader.setCustomModelResourceLocation(
                item,
                0,
                new ModelResourceLocation(
                        new ResourceLocation(References.MODID, item.getUnlocalizedName().substring(5)),
                        "inventory"
                )
        );
    }

    private void registerVariantRender(Item item, int meta) {
        String variant = this.subTypes != null && meta < this.subTypes.length ? this.subTypes[meta] : "default";

        ModelLoader.setCustomModelResourceLocation(
                item,
                meta,
                new ModelResourceLocation(
                        new ResourceLocation(References.MODID, item.getRegistryName().getResourcePath() + "_" + variant),
                        "inventory"
                )
        );
    }

    public abstract ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn);
}
