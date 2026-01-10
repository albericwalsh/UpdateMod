package fr.broawz.updatemod.blocks;

import fr.broawz.updatemod.UpdateMod;
import fr.broawz.updatemod.utils.References;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * AbstractBlockSign
 * -----------------
 * Classe abstraite représentant la partie "bloc" des panneaux.
 *
 * Responsabilités :
 *  - Définir la forme physique du panneau
 *  - Gérer les variants (métadonnées)
 *  - Créer la TileEntity associée
 *  - Gérer le placement (orientation, preset, GUI)
 *
 * ⚠️ Toute la logique de contenu (texte, couleurs, etc.)
 * est volontairement déléguée à AbstractTileEntitySign.
 */
public abstract class AbstractBlockSign extends BlockContainer {

    /** Bounding box pleine (fallback / debug) */
    public static final AxisAlignedBB FULL_BLOCK = new AxisAlignedBB(0,0,0,1,1,1);

    /**
     * Propriété BlockState représentant le variant du panneau.
     * Sert uniquement pour :
     *  - l’ItemBlock
     *  - les modèles / textures
     */
    public static final PropertyEnum<SignVariant> VARIANT =
            PropertyEnum.create("variant", SignVariant.class);

    /**
     * Constructeur
     * ------------
     * Initialise les propriétés de base du bloc panneau
     */
    protected AbstractBlockSign() {
        super(Material.WOOD);

        // Variant par défaut
        setDefaultState(
                this.blockState.getBaseState()
                        .withProperty(VARIANT, SignVariant.BASIC)
        );

        // CreativeTab du mod
        setCreativeTab(References.UPDATED_MOD);
    }

    /**
     * Déclaration du BlockStateContainer
     * ---------------------------------
     * Contient uniquement la propriété VARIANT
     */
    @Override
    @MethodsReturnNonnullByDefault
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    /**
     * Conversion BlockState → metadata
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).ordinal();
    }

    /**
     * Conversion metadata → BlockState
     */
    @Override
    @MethodsReturnNonnullByDefault
    public IBlockState getStateFromMeta(int meta) {
        if (meta < 0 || meta >= SignVariant.values().length) meta = 0;
        return this.getDefaultState()
                .withProperty(VARIANT, SignVariant.values()[meta]);
    }

    /**
     * État réel du bloc côté client
     * ------------------------------
     * Utilisé pour transmettre des infos dynamiques au renderer
     * via la TileEntity (preset, couleurs, etc.)
     */
    @Override
    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof AbstractTileEntitySign) {
            AbstractTileEntitySign sign = (AbstractTileEntitySign) te;
            SignPreset preset = sign.getCurrentPreset();

            // Ici, le renderer peut récupérer les infos du preset
            // (couleurs, highlight, etc.)
        }
        return state;
    }

    /**
     * Metadata conservée lors du drop du bloc
     */
    @Override
    @ParametersAreNonnullByDefault
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    /**
     * Enum listant tous les variants de panneaux
     * ------------------------------------------
     * Chaque valeur correspond :
     *  - à une metadata
     *  - à une texture / modèle
     */
    public enum SignVariant implements IStringSerializable {
        BASIC("basic_sign"),
        BLACK_BASIC("black_basic_sign"),
        BLACK("black_sign"),
        BLUE("blue_sign"),
        GREEN("green_sign"),
        PURPLE("purple_sign"),
        RED("red_sign"),
        YELLOW_BASIC("yellow_basic_sign"),
        YELLOW("yellow_sign");

        private final String name;

        SignVariant(String name) {
            this.name = name;
        }

        /** Nom utilisé dans les blockstates JSON */
        @Override
        public String getName() {
            return this.name;
        }
    }

    // --- Propriétés physiques du bloc ---

    /** Le panneau n’est pas un cube plein */
    @Override
    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    /** Le panneau n’est pas opaque */
    @Override
    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    /** On peut traverser le panneau */
    @Override
    @ParametersAreNonnullByDefault
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    /** Pas de collision */
    @Override
    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    public AxisAlignedBB getCollisionBoundingBox(
            IBlockState state, World worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    /**
     * Bounding box visuelle dépendante de l’orientation
     */
    @Override
    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    public AxisAlignedBB getBoundingBox(
            IBlockState state, IBlockAccess source, BlockPos pos) {

        TileEntity te = source.getTileEntity(pos);
        if (!(te instanceof AbstractTileEntitySign)) return FULL_BLOCK;

        EnumFacing facing = ((AbstractTileEntitySign) te).getFacing();
        float thickness = 0.0625F; // épaisseur du panneau

        switch (facing) {
            case NORTH: return new AxisAlignedBB(0,0,1-thickness,1,1,1);
            case SOUTH: return new AxisAlignedBB(0,0,0,1,1,thickness);
            case WEST:  return new AxisAlignedBB(1-thickness,0,0,1,1,1);
            case EAST:  return new AxisAlignedBB(0,0,0,thickness,1,1);
            default:    return FULL_BLOCK;
        }
    }

    /**
     * Placement du bloc
     * -----------------
     * Point central où :
     *  - le variant est appliqué
     *  - l’orientation est définie
     *  - le preset est chargé
     *  - la GUI est ouverte
     */
    @Override
    @ParametersAreNonnullByDefault
    public void onBlockPlacedBy(
            World worldIn, BlockPos pos, IBlockState state,
            EntityLivingBase placer, ItemStack stack) {

        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof AbstractTileEntitySign)) return;

        AbstractTileEntitySign sign = (AbstractTileEntitySign) te;
        int meta = stack.getMetadata();

        // Appliquer variant + orientation SANS modifier le BlockState
        sign.setVariantNoUpdate(meta);
        sign.setFacingNoUpdate(placer.getHorizontalFacing().getOpposite());

        // Appliquer le preset correspondant au variant
        SignPreset preset = sign.getCurrentPreset();
        if (preset != null) {
            sign.setLineColor(preset.getLineColors());
            sign.setLineHighlightColor(preset.getLineHighlightColors());
        }

        // Synchronisation serveur → client
        sign.markDirty();
        if (!worldIn.isRemote) {
            worldIn.notifyBlockUpdate(pos, state, state, 3);
        }

        // Ouverture de la GUI côté client
        if (placer instanceof EntityPlayer && worldIn.isRemote) {
            ((EntityPlayer) placer).openGui(
                    UpdateMod.instance,
                    1,
                    worldIn,
                    pos.getX(), pos.getY(), pos.getZ()
            );
        }
    }
}
