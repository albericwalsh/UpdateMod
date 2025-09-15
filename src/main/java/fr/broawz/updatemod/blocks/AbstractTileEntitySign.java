package fr.broawz.updatemod.blocks;

import fr.broawz.updatemod.blocks.blocksign.BlockBasicSign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * TileEntity de base pour tous les panneaux personnalisés
 */
public abstract class AbstractTileEntitySign extends TileEntity {

    // --- Texte & état ---
    protected String[] lines = new String[]{"", "", "", ""};
    protected EnumFacing facing = EnumFacing.NORTH;
    protected int variant = 0; // index du variant

    public String[] getLines() { return lines; }
    public EnumFacing getFacing() { return facing; }
    public int getVariant() { return variant; }

    // --- Rendu & style ---
    protected String[] lineFonts = new String[]{"Arial","Arial","Arial","Arial"};
    protected int[] lineFontSizes = new int[]{25,25,25,25};
    protected int[] lineColors = new int[]{0xFFFFFF,0xFFFFFF,0xFFFFFF,0xFFFFFF};
    protected int[] lineHighlightColor = new int[]{0,0,0,0};
    protected int[] highlightHeight = new int[]{8,8,8,8};
    protected float TextScale = 0.025F;

    public int[] getTextColor() { return lineColors; }
    public int[] getFontSize() { return lineFontSizes; }
    public String[] getFontName() { return lineFonts; }
    public int[] getLineHighlightColor() { return lineHighlightColor; }
    public int[] getHighlightHeight() { return highlightHeight; }
    public float getTextScale() { return TextScale; }

    // --- Méthodes texte ---
    public void setLine(int index, String text) {
        if (index >= 0 && index < 4) {
            lines[index] = text;
            markDirty();
        }
    }

    public String[] parseLineParts(int index) {
        return lines[index].split(" ");
    }

    // --- Méthodes rendu ---
    public void setFacing(EnumFacing facing) {
        this.facing = facing;
        markDirty();
        syncToClient();
    }

    public void setVariant(int variant) {
        this.variant = variant;
        markDirty();
        // ⚠️ Ne plus mettre à jour le BlockState ici
    }

    // ⚡ setter variant sans réécrire le block
    public void setVariantNoUpdate(int variant) {
        this.variant = variant;
    }

    // ⚡ setter facing sans réécrire le block
    public void setFacingNoUpdate(EnumFacing facing) {
        this.facing = facing;
    }

    public SignPreset getCurrentPreset() {
        if (variant == 0) return null; // BASIC ne reset pas le preset
        return BlockBasicSign.getPreset(variant);
    }

    // --- Font / taille / couleur ---
    public void setLineFont(String font) { for (int i=0;i<4;i++) lineFonts[i]=font; markDirty(); }
    public void setLineFont(String[] fonts) { for (int i=0;i<4;i++) lineFonts[i]=fonts[i]; markDirty(); }
    public void setLineSize(int size) { for (int i=0;i<4;i++) lineFontSizes[i]=size; markDirty(); }
    public void setLineSize(int[] sizes) { for (int i=0;i<4;i++) lineFontSizes[i]=sizes[i]; markDirty(); }
    public void setLineColor(int color) { for (int i=0;i<4;i++) lineColors[i]=color; markDirty(); }
    public void setLineColor(int[] colors) { for (int i=0;i<4;i++) lineColors[i]=colors[i]; markDirty(); }
    public void setLineHighlightColor(int color) { for (int i=0;i<4;i++) lineHighlightColor[i]=color; markDirty(); }
    public void setLineHighlightColor(int[] colors) { for (int i=0;i<4;i++) lineHighlightColor[i]=colors[i]; markDirty(); }
    public void setHighlightHeight(int height) { for (int i=0;i<4;i++) highlightHeight[i]=height; markDirty(); }
    public void setHighlightHeight(int[] heights) { for (int i=0;i<4;i++) highlightHeight[i]=heights[i]; markDirty(); }

    private void syncToClient() {
        if (!worldObj.isRemote) {
            SPacketUpdateTileEntity packet = getUpdatePacket();
            worldObj.getPlayers(EntityPlayer.class, p -> true).forEach(player ->
                    ((EntityPlayerMP) player).connection.sendPacket(packet)
            );
        }
    }

    // --- NBT & synchronisation ---
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Facing", facing.getIndex());
        compound.setInteger("Variant", variant);
        for (int i=0;i<4;i++) compound.setString("Line"+i, lines[i]);

        System.out.println("\u001B[34m[TILE WRITE] Variant=" + variant +
                " | Facing=" + facing +
                " | Lines=[" + String.join(",", lines) + "]\u001B[0m");
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        facing = EnumFacing.getFront(compound.getInteger("Facing"));
        variant = compound.getInteger("Variant");
        for (int i=0;i<4;i++) lines[i]=compound.getString("Line"+i);

        if (variant != 0) {
            SignPreset preset = BlockBasicSign.getPreset(variant);
            if (preset != null) {
                lineColors = preset.getLineColors();
                lineHighlightColor = preset.getLineHighlightColors();
            }
        }

        System.out.println("\u001B[36m[TILE READ] Variant=" + variant +
                " | Facing=" + facing +
                " | Preset Colors=[" + lineColors[0]+", "+lineColors[1]+", "+lineColors[2]+", "+lineColors[3]+"]\u001B[0m");
    }

    @Override
    public NBTTagCompound getUpdateTag() { return writeToNBT(new NBTTagCompound()); }
    @Override
    public void handleUpdateTag(NBTTagCompound tag) { readFromNBT(tag); }
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() { return new SPacketUpdateTileEntity(pos, 1, writeToNBT(new NBTTagCompound())); }
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) { readFromNBT(pkt.getNbtCompound()); }

    @Override
    public void markDirty() {
        super.markDirty();
        System.out.println("\u001B[33m[TILE MARKDIRTY] Variant=" + variant + " | Facing=" + facing + "\u001B[0m");
    }
}