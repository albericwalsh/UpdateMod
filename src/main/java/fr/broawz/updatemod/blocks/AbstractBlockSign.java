package fr.broawz.updatemod.blocks;

import fr.broawz.updatemod.UpdateMod;
import fr.broawz.updatemod.utils.References;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import static fr.broawz.updatemod.blocks.blocksign.BlockBasicSign.getPresets;

public abstract class AbstractBlockSign extends BlockContainer {

    public static final AxisAlignedBB FULL_BLOCK = new AxisAlignedBB(0,0,0,1,1,1);
    public static final PropertyEnum<SignVariant> VARIANT = PropertyEnum.create("variant", SignVariant.class);

    protected AbstractBlockSign() {
        super(Material.WOOD);
        setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, SignVariant.BASIC));
        setCreativeTab(References.UPDATED_MOD);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        if (meta < 0 || meta >= SignVariant.values().length) meta = 0;
        return this.getDefaultState().withProperty(VARIANT, SignVariant.values()[meta]);
    }

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


    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

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

        SignVariant(String name) { this.name = name; }

        @Override
        public String getName() { return this.name; }
    }

    @Override
    public boolean isFullCube(IBlockState state) { return false; }

    @Override
    public boolean isOpaqueCube(IBlockState state) { return false; }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) { return true; }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileEntity te = source.getTileEntity(pos);
        if (!(te instanceof AbstractTileEntitySign)) return FULL_BLOCK;

        EnumFacing facing = ((AbstractTileEntitySign) te).getFacing();
        float thickness = 0.0625F;

        switch (facing) {
            case NORTH: return new AxisAlignedBB(0,0,1-thickness,1,1,1);
            case SOUTH: return new AxisAlignedBB(0,0,0,1,1,thickness);
            case WEST:  return new AxisAlignedBB(1-thickness,0,0,1,1,1);
            case EAST:  return new AxisAlignedBB(0,0,0,thickness,1,1);
            default:    return FULL_BLOCK;
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
                                EntityLivingBase placer, ItemStack stack) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof AbstractTileEntitySign)) return;

        AbstractTileEntitySign sign = (AbstractTileEntitySign) te;
        int meta = stack.getMetadata();

        // ⚡ Appliquer variant et facing côté serveur SANS modifier BlockState
        sign.setVariantNoUpdate(meta);
        sign.setFacingNoUpdate(placer.getHorizontalFacing().getOpposite());

        // ⚡ Appliquer preset
        SignPreset preset = sign.getCurrentPreset();
        if (preset != null) {
            sign.setLineColor(preset.getLineColors());
            sign.setLineHighlightColor(preset.getLineHighlightColors());
        }

        // ⚡ Synchroniser client sans toucher au BlockState
        sign.markDirty();
        if (!worldIn.isRemote) {
            worldIn.notifyBlockUpdate(pos, state, state, 3);
        }

        // ⚡ Ouvrir GUI côté client
        if (placer instanceof EntityPlayer && worldIn.isRemote) {
            ((EntityPlayer) placer).openGui(UpdateMod.instance, 1, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
    }
}

