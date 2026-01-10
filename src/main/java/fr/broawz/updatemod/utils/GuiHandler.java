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

/**
 * Gestionnaire des GUIs pour le mod
 *
 * Ce handler est utilisé par Forge pour ouvrir :
 * - les GUIs côté serveur (conteneurs)
 * - les GUIs côté client (interfaces graphiques)
 *
 * Ici, les panneaux personnalisés n'ont **pas de conteneur serveur**, seulement une GUI côté client.
 */
public class GuiHandler implements IGuiHandler {

    /**
     * Récupération de l'élément GUI côté serveur
     *
     * @param ID Identifiant de la GUI
     * @param player Joueur qui ouvre la GUI
     * @param world Monde
     * @param x Position X
     * @param y Position Y
     * @param z Position Z
     * @return L'objet serveur (conteneur), ou null si pas nécessaire
     */
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

        if (ID == 1) { // GUI du panneau
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

            if (te instanceof AbstractTileEntitySign) {
                // Pas de conteneur serveur pour les panneaux
                return null;
            } else {
                // Alerte si le TileEntity n’est pas reconnu
                System.out.println("\u001B[31m[UpdateMod][GuiHandler] Server TE NON reconnu !\u001B[0m");
            }
        }
        return null;
    }

    /**
     * Récupération de l'élément GUI côté client
     *
     * @param ID Identifiant de la GUI
     * @param player Joueur qui ouvre la GUI
     * @param world Monde
     * @param x Position X
     * @param y Position Y
     * @param z Position Z
     * @return L'objet GUI côté client, ou null si pas applicable
     */
    @Override
    @SideOnly(Side.CLIENT)
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {

        if (ID == 1) { // GUI du panneau
            TileEntity te = world.getTileEntity(new BlockPos(x, y, z));

            if (te instanceof AbstractTileEntitySign) {
                // Retourne la GUI client pour le panneau
                return new GuiCustomSign((AbstractTileEntitySign) te);
            } else {
                // Alerte si le TileEntity n’est pas reconnu côté client
                System.out.println("\u001B[31m[UpdateMod][GuiHandler] Client TE NON reconnu !\u001B[0m");
            }
        }
        return null;
    }
}
