package fr.broawz.updatemod.blocks;

/*
 * Imports internes au mod
 */
import fr.broawz.updatemod.blocks.blocksign.BlockBasicSign;        // Bloc panneau personnalisé
import fr.broawz.updatemod.blocks.tileentity.TileEntityBasicSign;  // TileEntity associée
import fr.broawz.updatemod.items.ItemBlockSign;                   // ItemBlock avec variantes

/*
 * Imports Minecraft / Forge
 */
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

/*
 * Classe d’initialisation des blocs "complexes"
 * ---------------------------------------------
 * Rôle :
 *  - Centraliser l’enregistrement des blocs spéciaux
 *  - Gérer les variantes (metadata)
 *  - Lier blocs ↔ items ↔ tile entities
 */
public class ModBlocksInit {

    /*
     * Instance globale du bloc panneau
     * → Utilisée ailleurs dans le mod (GUI, packets, etc.)
     */
    public static BlockBasicSign BASIC_SIGN;

    /*
     * Liste des variantes du panneau
     * --------------------------------
     * ⚠️ L’ordre est IMPORTANT :
     *  - index = metadata
     *  - doit correspondre aux textures / models JSON
     */
    private static final String[] BASIC_SIGN_VARIANTS = {
            "basic",
            "black_basic",
            "black",
            "blue",
            "green",
            "purple",
            "red",
            "yellow_basic",
            "yellow"
    };

    /*
     * Méthode appelée depuis ModContentRegistry.registerCustomBlocks()
     * ----------------------------------------------------------------
     * Enregistre :
     *  - le bloc
     *  - l’ItemBlock avec variantes
     *  - la TileEntity associée
     */
    public static void init() {

        // Création du bloc panneau
        BASIC_SIGN = new BlockBasicSign();

        // Enregistrement du bloc dans Forge
        GameRegistry.register(BASIC_SIGN);

        /*
         * Création de l’ItemBlock personnalisé
         * → gère plusieurs variantes via la metadata
         */
        Item itemBlock = new ItemBlockSign(
                BASIC_SIGN,
                BASIC_SIGN_VARIANTS
        )
                .setRegistryName(BASIC_SIGN.getRegistryName());

        // Enregistrement de l’ItemBlock
        GameRegistry.register(itemBlock);

        /*
         * Enregistrement de la TileEntity
         * → nécessaire pour stocker des données persistantes
         * → texte, NBT, état personnalisé, etc.
         */
        GameRegistry.registerTileEntity(
                TileEntityBasicSign.class,
                "tile_basic_sign"
        );
    }
}
