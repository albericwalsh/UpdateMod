package fr.broawz.updatemod.proxy;

import fr.broawz.updatemod.blocks.AbstractTileEntitySign;
import fr.broawz.updatemod.blocks.ModBlocks;
import fr.broawz.updatemod.blocks.ModBlocksInit;
import fr.broawz.updatemod.client.render.SignIcons;
import fr.broawz.updatemod.client.render.TileEntityCustomSignRenderer;
import fr.broawz.updatemod.items.ItemDebugStick;
import fr.broawz.updatemod.items.ModItemsInit;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Proxy côté CLIENT
 * Gère tout ce qui est spécifique au client (rendu, GUI, etc.)
 */
public class ClientProxy extends CommonProxy {

    // Liste des variantes (⚠️ doit correspondre aux noms de tes fichiers JSON dans models/item/)
    private static final String[] BASIC_SIGN_VARIANTS = {
            "basic_sign",
            "black_basic_sign",
            "black_sign",
            "blue_sign",
            "green_sign",
            "purple_sign",
            "red_sign",
            "yellow_basic_sign",
            "yellow_sign"
    };

    @Override
    public void preInit() {
        System.out.println("[UpdateMod] ClientProxy preInit called!");

        // ✅ Enregistrement de l'ItemDebugStick (CLIENT ONLY)
        new ItemDebugStick();

        // Rendu du TileEntity
        ClientRegistry.bindTileEntitySpecialRenderer(
                AbstractTileEntitySign.class,
                new TileEntityCustomSignRenderer()
        );

        // Rendu de l'item avec toutes les variantes
        Item item = Item.getItemFromBlock(ModBlocksInit.BASIC_SIGN);
        for (int i = 0; i < BASIC_SIGN_VARIANTS.length; i++) {
            ModelLoader.setCustomModelResourceLocation(
                    item,
                    i,
                    new ModelResourceLocation("updatemod:" + BASIC_SIGN_VARIANTS[i], "inventory")
            );
        }
        ModItemsInit.registerModels();
        SignIcons.loadFromJSON();
    }

    @Override
    public void init() {
        System.out.println("[UpdateMod] ClientProxy init called!");
        ModBlocks.registerRenders();
    }
}