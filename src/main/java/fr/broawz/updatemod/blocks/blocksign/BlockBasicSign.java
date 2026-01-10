package fr.broawz.updatemod.blocks.blocksign;

/*
 * Imports internes au mod
 */
import fr.broawz.updatemod.blocks.AbstractBlockSign;          // Bloc panneau abstrait
import fr.broawz.updatemod.blocks.AbstractTileEntitySign;     // TileEntity abstraite
import fr.broawz.updatemod.blocks.SignPreset;                 // Preset visuel (couleurs, highlights)
import fr.broawz.updatemod.blocks.tileentity.TileEntityBasicSign;

/*
 * Imports Minecraft
 */
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;

/*
 * Bloc panneau "basique"
 * ---------------------
 * - Supporte plusieurs variantes (metadata)
 * - Chaque variante est associée à un SignPreset
 * - Le rendu réel dépend du contenu de la TileEntity
 */
public class BlockBasicSign extends AbstractBlockSign {

    /*
     * Map variant → preset
     * --------------------
     * Clé   : metadata / variant
     * Valeur: SignPreset (couleurs, highlights, etc.)
     */
    private static final Map<Integer, SignPreset> PRESETS = new HashMap<>();

    /*
     * Récupère le preset associé à un variant
     * → fallback sur le preset 0 si absent
     */
    public static SignPreset getPreset(int variant) {
        return PRESETS.getOrDefault(variant, PRESETS.get(0));
    }

    /*
     * Expose tous les presets
     * → utile pour debug, GUI, renderer
     */
    public static Map<Integer, SignPreset> getPresets() {
        return PRESETS;
    }

    /*
     * Initialisation statique des presets
     * -----------------------------------
     * S’exécute UNE SEULE FOIS au chargement de la classe
     */
    static {

        // Variant 0 : panneau basique (texte blanc)
        PRESETS.put(0, SignPreset.newPreset(0xFFFFFF));

        // Variant 1 : panneau noir basique (texte noir)
        PRESETS.put(1, SignPreset.newPreset(0x000000));

        // Variant 2 : panneau noir avec highlight sombre
        PRESETS.put(2, SignPreset.newPreset(
                new int[]{0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF},
                new int[]{0x88000000, 0, 0, 0}
        ));

        // Variant 3 : bleu
        PRESETS.put(3, SignPreset.newPreset(
                new int[]{0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF},
                new int[]{0x880000FF, 0, 0, 0}
        ));

        // Variant 4 : vert
        PRESETS.put(4, SignPreset.newPreset(
                new int[]{0x000000, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF},
                new int[]{0x8800FF00, 0, 0, 0}
        ));

        // Variant 5 : violet
        PRESETS.put(5, SignPreset.newPreset(
                new int[]{0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF},
                new int[]{0x88AA00FF, 0, 0, 0}
        ));

        // Variant 6 : rouge
        PRESETS.put(6, SignPreset.newPreset(
                new int[]{0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF},
                new int[]{0x88FF0000, 0, 0, 0}
        ));

        // Variant 7 : jaune basique
        PRESETS.put(7, SignPreset.newPreset(0xFFFF00));

        // Variant 8 : jaune avec highlight
        PRESETS.put(8, SignPreset.newPreset(
                new int[]{0x000000, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF},
                new int[]{0x88FFFF00, 0, 0, 0}
        ));
    }

    /*
     * Constructeur
     * ------------
     * Ne définit que l’identité du bloc
     * → la logique est héritée d’AbstractBlockSign
     */
    public BlockBasicSign() {
        super();
        setUnlocalizedName("basic_sign");
        setRegistryName("basic_sign");
    }

    /*
     * Création de la TileEntity associée
     * ---------------------------------
     * Appelée côté serveur ET client
     */
    @Override
    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityBasicSign();
    }

    /**
     * ⚡ Point clé du rendu
     * --------------------
     * Cette méthode est appelée côté CLIENT
     * Elle permet d’adapter l’état du bloc
     * en fonction des données de la TileEntity
     */
    @Override
    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public IBlockState getActualState(
            IBlockState state,
            IBlockAccess worldIn,
            BlockPos pos
    ) {

        TileEntity te = worldIn.getTileEntity(pos);

        if (te instanceof AbstractTileEntitySign) {

            AbstractTileEntitySign sign = (AbstractTileEntitySign) te;

            // Preset actuellement utilisé par ce panneau
            SignPreset preset = sign.getCurrentPreset();

            /*
             * ⚠️ IMPORTANT
             * Ici, tu NE MODIFIES PAS le blockstate
             * Tu récupères seulement les infos
             * que le renderer utilisera ensuite
             *
             * Exemple d’usage côté renderer :
             *  - preset.getLineColors()
             *  - preset.getLineHighlightColors()
             */
        }

        // Le blockstate reste inchangé
        return state;
    }
}
