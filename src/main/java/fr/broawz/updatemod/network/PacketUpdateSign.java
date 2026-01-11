package fr.broawz.updatemod.network;

import fr.broawz.updatemod.blocks.AbstractTileEntitySign;
import fr.broawz.updatemod.blocks.SignPreset;
import fr.broawz.updatemod.blocks.blocksign.BlockBasicSign;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Packet réseau : mise à jour d'un panneau
 * ---------------------------------------
 * Sens : CLIENT → SERVEUR
 * (édition via GUI)
 */
public class PacketUpdateSign implements IMessage {

    private BlockPos pos;
    private String[] lines = new String[4];
    private int variant;
    private int textSpan;
    private int align;

    /**
     * Constructeur vide OBLIGATOIRE pour Forge
     */
    public PacketUpdateSign() {}

    /**
     * Constructeur utilisé côté client pour envoyer les données
     */
    public PacketUpdateSign(BlockPos pos, String[] lines, int variant, int textSpan, int align) {
        this.pos = pos;
        this.lines = lines;
        this.variant = variant;
        this.textSpan = textSpan;
        this.align = align;
    }

    /**
     * Sérialisation → ByteBuf
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(variant);
        buf.writeInt(textSpan);
        buf.writeInt(align);

        for (int i = 0; i < 4; i++) {
            String s = lines[i] == null ? "" : lines[i];
            ByteBufUtils.writeUTF8String(buf, s);
        }
    }

    /**
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

    /**
     * Handler du packet - CÔTÉ SERVEUR UNIQUEMENT
     * ✅ Pas d'import Minecraft ici !
     */
    public static class Handler implements IMessageHandler<PacketUpdateSign, IMessage> {

        @Override
        public IMessage onMessage(PacketUpdateSign message, MessageContext ctx) {

            // ✅ Récupérer le joueur serveur (pas de Minecraft.getMinecraft() !)
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            // ✅ Exécuter sur le thread serveur
            player.getServerWorld().addScheduledTask(() -> {

                World world = player.worldObj;
                TileEntity te = world.getTileEntity(message.pos);

                if (te instanceof AbstractTileEntitySign) {
                    AbstractTileEntitySign sign = (AbstractTileEntitySign) te;

                    // Mise à jour du texte
                    for (int i = 0; i < 4; i++) {
                        sign.setLine(i, message.lines[i]);
                    }

                    // Variant
                    sign.setVariant(message.variant);

                    // Application du preset
                    SignPreset preset = BlockBasicSign.getPreset(message.variant);
                    if (preset != null) {
                        sign.setLineColor(preset.getLineColors());
                        sign.setLineHighlightColor(preset.getLineHighlightColors());
                    }

                    // Options de rendu
                    sign.setTextSpan(message.textSpan);

                    switch (message.align) {
                        case 0:
                            sign.setAlign(AbstractTileEntitySign.Align.LEFT);
                            break;
                        case 2:
                            sign.setAlign(AbstractTileEntitySign.Align.RIGHT);
                            break;
                        default:
                            sign.setAlign(AbstractTileEntitySign.Align.CENTER);
                            break;
                    }

                    // Marque la TileEntity comme modifiée
                    sign.markDirty();

                    // ✅ Synchronise automatiquement vers tous les clients
                    world.notifyBlockUpdate(
                            message.pos,
                            world.getBlockState(message.pos),
                            world.getBlockState(message.pos),
                            3
                    );
                }
            });

            return null;
        }
    }
}