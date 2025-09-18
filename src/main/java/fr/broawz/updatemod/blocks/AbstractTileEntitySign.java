package fr.broawz.updatemod.blocks;

import fr.broawz.updatemod.blocks.blocksign.BlockBasicSign;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import javax.annotation.ParametersAreNonnullByDefault;

import static fr.broawz.updatemod.blocks.blocksign.BlockBasicSign.getPresets;

/**
 * TileEntity de base pour tous les panneaux personnalisés
 */
public abstract class AbstractTileEntitySign extends TileEntity {

    // --- Texte & état ---
    protected String[] lines = new String[]{"", "", "", ""};
    protected EnumFacing facing = EnumFacing.NORTH;
    protected int variant = 0; // index du variant

    private int textSpan = 1;
    private Align align = Align.CENTER;

    public enum Align {LEFT, CENTER, RIGHT}

    // --- Getters ---
    public int getTextSpan() { return textSpan; }
    public Align getAlign() { return align; }
    public String[] getLines() { return lines; }
    public EnumFacing getFacing() { return facing; }
    public int getVariant() { return variant; }

    // --- Setters publics (avec dirty/sync) ---
    public void setTextSpan(int span) {
        if (this.textSpan != span) {
            this.textSpan = span;
//            markDirty();
        }
    }

    public void setAlign(Align align) {
        if (this.align != align) {
            this.align = align;
//            markDirty();
        }
    }

    public void setLine(int index, String text) {
        if (index >= 0 && index < 4) {
            lines[index] = text;
//            markDirty();
        }
    }

    public void setFacing(EnumFacing facing) {
        if (this.facing != facing) {
            this.facing = facing;
//            markDirty();
            syncToClient();
        }
    }

    public void setVariant(int variant) {
        if (this.variant != variant) {
            this.variant = variant;
//            markDirty();
            // ⚠️ Ne plus forcer le BlockState ici
        }
    }

    // --- Setters internes (sans dirty/sync, utilisés pour NBT/presets) ---
    public void setTextSpanNoUpdate(int span) { this.textSpan = span; }
    public void setAlignNoUpdate(Align align) { this.align = align; }
    public void setLineNoUpdate(int index, String text) {
        if (index >= 0 && index < 4) lines[index] = text;
    }
    public void setFacingNoUpdate(EnumFacing facing) { this.facing = facing; }
    public void setVariantNoUpdate(int variant) { this.variant = variant; }

    // --- Rendu & style ---
    protected String[] lineFonts = new String[]{"Arial", "Arial", "Arial", "Arial"};
    protected int[] lineFontSizes = new int[]{25, 25, 25, 25};
    protected int[] lineColors = new int[]{0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF};
    protected int[] lineHighlightColor = new int[]{0, 0, 0, 0};
    protected int[] highlightHeight = new int[]{8, 8, 8, 8};
    protected float TextScale = 0.025F;

    public int[] getTextColor() { return lineColors; }
    public int[] getFontSize() { return lineFontSizes; }
    public String[] getFontName() { return lineFonts; }
    public int[] getLineHighlightColor() { return lineHighlightColor; }
    public int[] getHighlightHeight() { return highlightHeight; }
    public float getTextScale() { return TextScale; }
    public SignPreset getCurrentPreset() {
        return getPresets().get(this.getVariant());
    }


    // --- Font / taille / couleur ---
    public void setLineFont(String font) {
        for (int i = 0; i < 4; i++) lineFonts[i] = font;
//        markDirty();
    }

    public void setLineFont(String[] fonts) {
        System.arraycopy(fonts, 0, lineFonts, 0, 4);
//        markDirty();
    }

    public void setLineSize(int size) {
        for (int i = 0; i < 4; i++) lineFontSizes[i] = size;
//        markDirty();
    }

    public void setLineSize(int[] sizes) {
        System.arraycopy(sizes, 0, lineFontSizes, 0, 4);
//        markDirty();
    }

    public void setLineColor(int color) {
        for (int i = 0; i < 4; i++) lineColors[i] = color;
//        markDirty();
    }

    public void setLineColor(int[] colors) {
        System.arraycopy(colors, 0, lineColors, 0, 4);
//        markDirty();
    }

    public void setLineHighlightColor(int color) {
        for (int i = 0; i < 4; i++) lineHighlightColor[i] = color;
//        markDirty();
    }

    public void setLineHighlightColor(int[] colors) {
        System.arraycopy(colors, 0, lineHighlightColor, 0, 4);
//        markDirty();
    }

    public void setHighlightHeight(int height) {
        for (int i = 0; i < 4; i++) highlightHeight[i] = height;
//        markDirty();
    }

    public void setHighlightHeight(int[] heights) {
        System.arraycopy(heights, 0, highlightHeight, 0, 4);
//        markDirty();
    }

    // --- NBT & synchronisation ---
    private void syncToClient() {
        if (!worldObj.isRemote) {
            SPacketUpdateTileEntity packet = getUpdatePacket();
            worldObj.getPlayers(EntityPlayer.class, p -> true).forEach(player -> {
                assert packet != null;
                ((EntityPlayerMP) player).connection.sendPacket(packet);
            });
        }
    }

    @Override
    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Facing", facing.getIndex());
        compound.setInteger("Variant", variant);

        switch (align) {
            case LEFT: compound.setInteger("Align", 0); break;
            case CENTER: compound.setInteger("Align", 1); break;
            case RIGHT: compound.setInteger("Align", 2); break;
        }

        compound.setInteger("TextSpan", textSpan);
        for (int i = 0; i < 4; i++) compound.setString("Line" + i, lines[i]);

        System.out.println("\u001B[34m[TILE WRITE] Variant=" + variant +
                " | Facing=" + facing +
                " | Align=" + align +
                " | Text Span=" + textSpan + "\u001B[0m");
        return compound;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        setFacingNoUpdate(EnumFacing.getFront(compound.getInteger("Facing")));
        setVariantNoUpdate(compound.getInteger("Variant"));

        switch (compound.getInteger("Align")) {
            case 0: setAlignNoUpdate(Align.LEFT); break;
            case 2: setAlignNoUpdate(Align.RIGHT); break;
            default: setAlignNoUpdate(Align.CENTER); break;
        }

        setTextSpanNoUpdate(compound.getInteger("TextSpan"));
        for (int i = 0; i < 4; i++) setLineNoUpdate(i, compound.getString("Line" + i));

        // ✅ Réappliquer preset si les valeurs n'ont pas encore été customisées
        SignPreset preset = getCurrentPreset();
        if (preset != null) {
            if (lineColors == null || lineColors.length == 0 || lineColors[0] == 0xFFFFFF) {
                setLineColor(preset.getLineColors());
                setLineHighlightColor(preset.getLineHighlightColors());
            }
        }

        System.out.println("\u001B[36m[TILE READ] Variant=" + variant +
                " | Facing=" + facing +
                " | Align=" + align +
                " | Text Span=" + textSpan + "\u001B[0m");
    }

    @Override
    @MethodsReturnNonnullByDefault
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    @ParametersAreNonnullByDefault
    public void handleUpdateTag(NBTTagCompound tag) {
        readFromNBT(tag);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 1, writeToNBT(new NBTTagCompound()));
    }

    @Override
    @ParametersAreNonnullByDefault
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        System.out.println("\u001B[36m[TILE MANDATORY] markDirty called\u001B[0m");
    }
}
