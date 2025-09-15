package fr.broawz.updatemod.blocks.blocksign;

import fr.broawz.updatemod.blocks.AbstractBlockSign;
import fr.broawz.updatemod.blocks.AbstractTileEntitySign;
import fr.broawz.updatemod.blocks.SignPreset;
import fr.broawz.updatemod.blocks.tileentity.TileEntityBasicSign;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class BlockBasicSign extends AbstractBlockSign {

    private static final Map<Integer, SignPreset> PRESETS = new HashMap<>();

    public static SignPreset getPreset(int variant) {
        return PRESETS.getOrDefault(variant, PRESETS.get(0));
    }

    public static Map<Integer, SignPreset> getPresets() {
        return PRESETS;
    }

    static {
        // Définition des presets de couleurs pour chaque variant
        PRESETS.put(0, SignPreset.newPreset(0xFFFFFF)); // basic_sign → texte blanc
        PRESETS.put(1, SignPreset.newPreset(0x000000)); // black_basic_sign → texte noir
        PRESETS.put(2, SignPreset.newPreset(
                new int[]{0xFFFFFF,0xFFFFFF,0xFFFFFF,0xFFFFFF},
                new int[]{0xFF000000, 0, 0, 0})); // black_sign
        PRESETS.put(3, SignPreset.newPreset(
                new int[]{0xFFFFFF,0xFFFFFF,0xFFFFFF,0xFFFFFF},
                new int[]{0x0000FF, 0, 0, 0})); // blue_sign
        PRESETS.put(4, SignPreset.newPreset(
                new int[]{0x000000,0xFFFFFF,0xFFFFFF,0xFFFFFF},
                new int[]{0x00FF00, 0, 0, 0})); // green_sign
        PRESETS.put(5, SignPreset.newPreset(
                new int[]{0xFFFFFF,0xFFFFFF,0xFFFFFF,0xFFFFFF},
                new int[]{0xAA00FF, 0, 0, 0})); // purple_sign
        PRESETS.put(6, SignPreset.newPreset(
                new int[]{0xFFFFFF,0xFFFFFF,0xFFFFFF,0xFFFFFF},
                new int[]{0xFF0000, 0, 0, 0})); // red_sign
        PRESETS.put(7, SignPreset.newPreset(0xFFFF00)); // yellow_basic_sign
        PRESETS.put(8, SignPreset.newPreset(
                new int[]{0x000000,0xFFFFFF,0xFFFFFF,0xFFFFFF},
                new int[]{0xFFFF00, 0, 0, 0})); // yellow_sign
    }

    public BlockBasicSign() {
        super();
        setUnlocalizedName("basic_sign");
        setRegistryName("basic_sign");
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityBasicSign();
    }

    /**
     * ⚡ Important : côté client, retourne le blockstate correct
     * selon le variant stocké dans le TileEntity
     */
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof AbstractTileEntitySign) {
            AbstractTileEntitySign sign = (AbstractTileEntitySign) te;
            SignPreset preset = sign.getCurrentPreset();
            // Tu peux passer preset au renderer pour afficher la bonne couleur / highlight
            // Exemple pseudo-code :
            // renderer.setLineColors(preset.getLineColors());
            // renderer.setHighlightColors(preset.getLineHighlightColors());
        }
        return state;
    }


}
