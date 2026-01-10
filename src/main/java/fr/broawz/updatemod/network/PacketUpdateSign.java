package fr.broawz.updatemod.network;

/*
 * Imports internes
 */
import fr.broawz.updatemod.blocks.AbstractTileEntitySign;
import fr.broawz.updatemod.blocks.SignPreset;
import fr.broawz.updatemod.blocks.blocksign.BlockBasicSign;

/*
 * Imports réseau / Minecraft
 */
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/*
 * Packet réseau : mise à jour d’un panneau
 * ---------------------------------------
 * Sens principal :
 *  CLIENT → SERVEUR
 * (édition via GUI)
 *
 * Synchronisation secondaire :
 *  SERVEUR → CLIENT
 */
public class PacketUpdateSign implements IMessage {

    /*
     * Position du panneau dans le monde
     */
    private BlockPos pos;

    /*
     * Contenu des 4 lignes
     */
    private String[] lines = new String[4];

    /*
     * Variant / metadata du panneau
     */
    private int variant;

    /*
     * Options supplémentaires de rendu
     */
    private int textSpan;
    private int align;

    /*
     * Constructeur vide
     * -----------------
     * OBLIGATOIRE pour Forge (désérialisation)
     */
    public PacketUpdateSign() {}

    /*
     * Constructeur utilisé côté client
     * --------------------------------
     * Envoi des données éditées
     */
    public PacketUpdateSign(
            BlockPos pos,
            String[] lines,
            int variant,
            int textSpan,
            int align
    ) {
        this.pos = pos;
        this.lines = lines;
        this.variant = variant;
        this.textSpan = textSpan;
        this.align = align;
    }

    /*
     * Sérialisation → ByteBuf
     * ----------------------
     * Ordre STRICT (doit correspondre à fromBytes)
     */
    @Override
    public void toBytes(ByteBuf buf) {

        // Position compacte
        buf.writeLong(pos.toLong());

        buf.writeInt(variant);
        buf.writeInt(textSpan);
        buf.writeInt(align);

        // 4 lignes de texte
        for (int i = 0; i < 4; i++) {
            String s = lines[i] == null ? "" : lines[i];
            ByteBufUtils.writeUTF8String(buf, s);
        }
    }

    /*
     * Désérialisation ← ByteBuf
     */
    @Override
    public void fromBytes(ByteBuf buf) {

        pos = BlockPos.fromLong(buf.readLong());

        variant = buf.readInt();
        textSpan = buf.readInt();
        align = buf.readInt();

        for (int i = 0; i < 4; i++) {
            lines[i] = ByteBufUtils.readUTF8String(buf);
        }
    }

    /*
     * Handler du packet
     * -----------------
     * Exécuté côté SERVEUR ou CLIENT
     * selon le contexte
     */
    public static class Handler
            implements IMessageHandler<PacketUpdateSign, IMessage> {

        @Override
        public IMessage onMessage(PacketUpdateSign message, MessageContext ctx) {

            /*
             * =====================
             * CÔTÉ SERVEUR
             * =====================
             */
            if (ctx.side.isServer()) {

                // Monde serveur du joueur ayant envoyé le packet
                World world = ctx.getServerHandler()
                        .playerEntity
                        .worldObj;

                TileEntity te = world.getTileEntity(message.pos);

                if (te instanceof AbstractTileEntitySign) {

                    AbstractTileEntitySign sign =
                            (AbstractTileEntitySign) te;

                    // Mise à jour du texte
                    for (int i = 0; i < 4; i++) {
                        sign.setLine(i, message.lines[i]);
                    }

                    // Variant (metadata logique)
                    sign.setVariant(message.variant);

                    // Application du preset visuel
                    SignPreset preset =
                            BlockBasicSign.getPreset(message.variant);

                    if (preset != null) {
                        sign.setLineColor(preset.getLineColors());
                        sign.setLineHighlightColor(
                                preset.getLineHighlightColors()
                        );
                    }

                    // Options de rendu
                    sign.setTextSpan(message.textSpan);

                    switch (message.align) {
                        case 0:
                            sign.setAlignNoUpdate(
                                    AbstractTileEntitySign.Align.LEFT
                            );
                            break;
                        case 2:
                            sign.setAlignNoUpdate(
                                    AbstractTileEntitySign.Align.RIGHT
                            );
                            break;
                        default:
                            sign.setAlignNoUpdate(
                                    AbstractTileEntitySign.Align.CENTER
                            );
                            break;
                    }

                    // Marque la TileEntity comme modifiée
                    sign.markDirty();

                    // Force la mise à jour du rendu
                    world.markBlockRangeForRenderUpdate(
                            te.getPos(),
                            te.getPos()
                    );
                }

                /*
                 * =====================
                 * CÔTÉ CLIENT
                 * =====================
                 */
            } else {

                // Important : exécution sur le thread client
                Minecraft.getMinecraft().addScheduledTask(() -> {

                    World world =
                            Minecraft.getMinecraft().theWorld;
                    if (world == null) return;

                    TileEntity te =
                            world.getTileEntity(message.pos);

                    if (te instanceof AbstractTileEntitySign) {

                        AbstractTileEntitySign sign =
                                (AbstractTileEntitySign) te;

                        for (int i = 0; i < 4; i++) {
                            sign.setLine(i, message.lines[i]);
                        }

                        sign.setVariant(message.variant);

                        SignPreset preset =
                                BlockBasicSign.getPreset(message.variant);

                        if (preset != null) {
                            sign.setLineColor(preset.getLineColors());
                            sign.setLineHighlightColor(
                                    preset.getLineHighlightColors()
                            );
                        }

                        sign.markDirty();
                        world.markBlockRangeForRenderUpdate(
                                te.getPos(),
                                te.getPos()
                        );
                    }
                });
            }

            return null; // Pas de packet réponse
        }
    }
}
