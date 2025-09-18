package fr.broawz.updatemod.network;

import fr.broawz.updatemod.blocks.AbstractTileEntitySign;
import fr.broawz.updatemod.blocks.SignPreset;
import fr.broawz.updatemod.blocks.blocksign.BlockBasicSign;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateSign implements IMessage {
    private BlockPos pos;
    private String[] lines = new String[4];
    private int variant;
    private int textSpan;   // ajout
    private int align;      // ajout si nécessaire


    public PacketUpdateSign() {} // Obligatoire

    public PacketUpdateSign(BlockPos pos, String[] lines, int variant, int textSpan, int align) {
        this.pos = pos;
        this.lines = lines;
        this.variant = variant;
        this.textSpan = textSpan;
        this.align = align;
    }


    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(variant);
        buf.writeInt(textSpan);  // ajout
        buf.writeInt(align);     // ajout si besoin
        for (int i = 0; i < 4; i++) {
            byte[] data = lines[i].getBytes();
            buf.writeInt(data.length);
            buf.writeBytes(data);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        variant = buf.readInt();
        textSpan = buf.readInt();   // ajout
        align = buf.readInt();      // ajout si besoin
        for (int i = 0; i < 4; i++) {
            int len = buf.readInt();
            byte[] data = new byte[len];
            buf.readBytes(data);
            lines[i] = new String(data);
        }
    }


    public static class Handler implements IMessageHandler<PacketUpdateSign, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdateSign message, MessageContext ctx) {
            if (ctx.side.isServer()) {
                // côté serveur, récupérer le monde et le joueur
                World world = ctx.getServerHandler().playerEntity.worldObj;
                TileEntity te = world.getTileEntity(message.pos);

                if (te instanceof AbstractTileEntitySign) {
                    AbstractTileEntitySign sign = (AbstractTileEntitySign) te;

                    for (int i = 0; i < 4; i++) {
                        sign.setLine(i, message.lines[i]);
                    }

                    sign.setVariant(message.variant);

                    SignPreset preset = BlockBasicSign.getPreset(message.variant);
                    if (preset != null) {
                        sign.setLineColor(preset.getLineColors());
                        sign.setLineHighlightColor(preset.getLineHighlightColors());
                    }

                    sign.setTextSpan(message.textSpan);
                    switch (message.align) {
                        case 0: sign.setAlignNoUpdate(AbstractTileEntitySign.Align.LEFT); break;
                        case 2: sign.setAlignNoUpdate(AbstractTileEntitySign.Align.RIGHT); break;
                        default: sign.setAlignNoUpdate(AbstractTileEntitySign.Align.CENTER); break;
                    }

                    sign.markDirty();
                    world.markBlockRangeForRenderUpdate(te.getPos(), te.getPos());
                }
            } else {
                // côté client
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        World world = Minecraft.getMinecraft().theWorld;
                        if (world == null) return;

                        TileEntity te = world.getTileEntity(message.pos);
                        if (te instanceof AbstractTileEntitySign) {
                            AbstractTileEntitySign sign = (AbstractTileEntitySign) te;

                            for (int i = 0; i < 4; i++) {
                                sign.setLine(i, message.lines[i]);
                            }

                            sign.setVariant(message.variant);

                            SignPreset preset = BlockBasicSign.getPreset(message.variant);
                            if (preset != null) {
                                sign.setLineColor(preset.getLineColors());
                                sign.setLineHighlightColor(preset.getLineHighlightColors());
                            }

                            sign.markDirty();
                            world.markBlockRangeForRenderUpdate(te.getPos(), te.getPos());
                        }
                    }
                });
            }
            return null;
        }
    }
}
