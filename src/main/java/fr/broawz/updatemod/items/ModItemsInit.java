package fr.broawz.updatemod.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItemsInit {
    public static Item TAB_ICON;

    public static void init() {
        TAB_ICON = new ItemTabIcon();
        GameRegistry.register(TAB_ICON);
    }
    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        ModelLoader.setCustomModelResourceLocation(
                TAB_ICON,
                0,
                new ModelResourceLocation("updatemod:tab_icon", "inventory")
        );
    }


}
