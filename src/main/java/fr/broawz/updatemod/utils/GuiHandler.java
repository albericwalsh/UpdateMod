package fr.broawz.updatemod.utils;

import fr.broawz.updatemod.blocks.AbstractTileEntitySign;
import fr.broawz.updatemod.gui.GuiCustomSign;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

        if (ID == 1) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

            if (te instanceof AbstractTileEntitySign) {
                return null; // pas de conteneur pour une pancarte
            } else {
                System.out.println("\u001B[31m[UpdateMod][GuiHandler] Server TE NON reconnu !\u001B[0m");
            }
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

        if (ID == 1) {
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

            if (te instanceof AbstractTileEntitySign) {
                return new GuiCustomSign((AbstractTileEntitySign) te);
            } else {
                System.out.println("\u001B[31m[UpdateMod][GuiHandler] Client TE NON reconnu !\u001B[0m");
            }
        }
        return null;
    }


}
