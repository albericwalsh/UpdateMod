package fr.broawz.updatemod.blocks;

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
 * AbstractTileEntitySign
 * ----------------------
 * TileEntity de base pour tous les panneaux personnalisés.
 * Elle stocke l’état complet d’un panneau :
 * - Texte (4 lignes)
 * - Variant / metadata
 * - Alignement et span
 * - Couleurs / surlignage / taille / police
 *
 * Gère aussi :
 * - Synchronisation serveur → client
 * - Sauvegarde via NBT
 */
public abstract class AbstractTileEntitySign extends TileEntity {

    // --- Texte & état de base ---
    protected String[] lines = new String[]{"", "", "", ""}; // 4 lignes de texte
    protected EnumFacing facing = EnumFacing.NORTH;         // Orientation
    protected int variant = 0;                               // Variant / type de panneau

    private int textSpan = 1;                                // Largeur relative du texte
    private Align align = Align.CENTER;                      // Alignement du texte

    /** Alignement du texte */
    public enum Align {LEFT, CENTER, RIGHT}

    // --- Getters publics ---
    public int getTextSpan() { return textSpan; }
    public Align getAlign() { return align; }
    public String[] getLines() { return lines; }
    public EnumFacing getFacing() { return facing; }
    public int getVariant() { return variant; }

    // --- Setters publics ---
    // Ces setters sont destinés à être appelés par d’autres classes
    // et peuvent déclencher la synchronisation ou le markDirty si nécessaire.
    public void setTextSpan(int span) {
        if (this.textSpan != span) this.textSpan = span;
    }

    public void setAlign(Align align) {
        if (this.align != align) this.align = align;
    }

    public void setLine(int index, String text) {
        if (index >= 0 && index < 4) lines[index] = text;
    }

    public void setFacing(EnumFacing facing) {
        if (this.facing != facing) {
            this.facing = facing;
            syncToClient(); // Mise à jour client immédiate
        }
    }

    public void setVariant(int variant) {
        if (this.variant != variant) this.variant = variant;
    }

    // --- Setters internes (sans markDirty / sync) ---
    // Utilisés lors du chargement depuis NBT ou application de presets
    public void setTextSpanNoUpdate(int span) { this.textSpan = span; }
    public void setAlignNoUpdate(Align align) { this.align = align; }
    public void setLineNoUpdate(int index, String text) {
        if (index >= 0 && index < 4) lines[index] = text;
    }
    public void setFacingNoUpdate(EnumFacing facing) { this.facing = facing; }
    public void setVariantNoUpdate(int variant) { this.variant = variant; }

    // --- Rendu / style ---
    protected String[] lineFonts = new String[]{"Arial", "Arial", "Arial", "Arial"};
    protected int[] lineFontSizes = new int[]{25, 25, 25, 25};
    protected int[] lineColors = new int[]{0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF};
    protected int[] lineHighlightColor = new int[]{0, 0, 0, 0};
    protected int[] highlightHeight = new int[]{8, 8, 8, 8};
    protected float TextScale = 0.025F;

    // --- Getters pour le rendu ---
    public int[] getTextColor() { return lineColors; }
    public int[] getFontSize() { return lineFontSizes; }
    public String[] getFontName() { return lineFonts; }
    public int[] getLineHighlightColor() { return lineHighlightColor; }
    public int[] getHighlightHeight() { return highlightHeight; }
    public float getTextScale() { return TextScale; }

    /** Retourne le preset actuel en fonction du variant */
    public SignPreset getCurrentPreset() {
        return getPresets().get(this.getVariant());
    }

    // --- Méthodes pour modifier le style ---
    public void setLineFont(String font) { for (int i=0;i<4;i++) lineFonts[i]=font; }
    public void setLineFont(String[] fonts) { System.arraycopy(fonts,0,lineFonts,0,4); }

    public void setLineSize(int size) { for (int i=0;i<4;i++) lineFontSizes[i]=size; }
    public void setLineSize(int[] sizes) { System.arraycopy(sizes,0,lineFontSizes,0,4); }

    public void setLineColor(int color) { for (int i=0;i<4;i++) lineColors[i]=color; }
    public void setLineColor(int[] colors) { System.arraycopy(colors,0,lineColors,0,4); }

    public void setLineHighlightColor(int color) { for (int i=0;i<4;i++) lineHighlightColor[i]=color; }
    public void setLineHighlightColor(int[] colors) { System.arraycopy(colors,0,lineHighlightColor,0,4); }

    public void setHighlightHeight(int height) { for (int i=0;i<4;i++) highlightHeight[i]=height; }
    public void setHighlightHeight(int[] heights) { System.arraycopy(heights,0,highlightHeight,0,4); }

    // --- Synchronisation client ---
    private void syncToClient() {
        if (!worldObj.isRemote) {
            SPacketUpdateTileEntity packet = getUpdatePacket();
            // Envoie le packet à tous les joueurs dans le monde
            worldObj.getPlayers(EntityPlayer.class, p -> true).forEach(player -> {
                assert packet != null;
                ((EntityPlayerMP) player).connection.sendPacket(packet);
            });
        }
    }

    // --- Sauvegarde NBT ---
    @Override
    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        // Stockage de l’état général
        compound.setInteger("Facing", facing.getIndex());
        compound.setInteger("Variant", variant);

        switch (align) {
            case LEFT: compound.setInteger("Align", 0); break;
            case CENTER: compound.setInteger("Align", 1); break;
            case RIGHT: compound.setInteger("Align", 2); break;
        }

        compound.setInteger("TextSpan", textSpan);

        // Stockage du texte
        for (int i=0;i<4;i++) compound.setString("Line"+i, lines[i]);

        return compound;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        // Lecture état sans forcer la sync
        setFacingNoUpdate(EnumFacing.getFront(compound.getInteger("Facing")));
        setVariantNoUpdate(compound.getInteger("Variant"));

        switch(compound.getInteger("Align")) {
            case 0: setAlignNoUpdate(Align.LEFT); break;
            case 2: setAlignNoUpdate(Align.RIGHT); break;
            default: setAlignNoUpdate(Align.CENTER); break;
        }

        setTextSpanNoUpdate(compound.getInteger("TextSpan"));

        for (int i=0;i<4;i++) setLineNoUpdate(i, compound.getString("Line"+i));

        // Réapplique le preset si nécessaire
        SignPreset preset = getCurrentPreset();
        if (preset != null) {
            if (lineColors == null || lineColors.length==0 || lineColors[0]==0xFFFFFF) {
                setLineColor(preset.getLineColors());
                setLineHighlightColor(preset.getLineHighlightColors());
            }
        }
    }

    // --- Synchronisation réseau (Minecraft standard) ---
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
    }
}
