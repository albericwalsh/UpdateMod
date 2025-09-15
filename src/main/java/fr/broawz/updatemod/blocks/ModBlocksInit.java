package fr.broawz.updatemod.blocks;

import fr.broawz.updatemod.blocks.blocksign.BlockBasicSign;
import fr.broawz.updatemod.blocks.tileentity.TileEntityBasicSign;
import fr.broawz.updatemod.items.ItemBlockSign;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocksInit {

    public static BlockBasicSign BASIC_SIGN;

    // Liste de tes variantes (doit correspondre aux textures)
    private static final String[] BASIC_SIGN_VARIANTS = {
            "basic", "black_basic", "black", "blue", "green", "purple", "red", "yellow_basic", "yellow"
    };

    public static void init() {
        BASIC_SIGN = new BlockBasicSign();
        GameRegistry.register(BASIC_SIGN);

        // ItemBlock qui gère toutes les variantes
        Item itemBlock = new ItemBlockSign(BASIC_SIGN, BASIC_SIGN_VARIANTS)
                .setRegistryName(BASIC_SIGN.getRegistryName());

        GameRegistry.register(itemBlock);

        GameRegistry.registerTileEntity(TileEntityBasicSign.class, "tile_basic_sign");
    }
}
