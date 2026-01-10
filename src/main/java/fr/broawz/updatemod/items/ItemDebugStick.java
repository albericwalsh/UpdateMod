package fr.broawz.updatemod.items;

/*
 * Imports internes au mod
 */
import fr.broawz.updatemod.gui.GuiDebugStick;      // GUI de debug affichée au clic
import fr.broawz.updatemod.utils.References;       // CreativeTab et constantes

/*
 * Imports Minecraft
 */
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.Objects;

/*
 * Item de debug / outil développeur
 * --------------------------------
 * Rôle :
 *  - Permet de récupérer des infos sur un bloc visé
 *  - Ouvre une GUI côté client
 *  - Sert d’outil interne (non gameplay)
 */
public class ItemDebugStick extends ModItems {

    /*
     * Constructeur
     * ------------
     * Initialise l’item comme un outil unique (non empilable)
     */
    public ItemDebugStick() {

        // Appel au constructeur de ModItems
        super(
                "debug_stick",     // unlocalizedName
                "debug_stick",     // registryName
                true               // auto-register
        );

        // Onglet créatif custom du mod
        setCreativeTab(References.UPDATED_MOD);

        // Un seul par slot (outil)
        setMaxStackSize(1);
    }

    /*
     * Appelé lors du clic droit avec l’item
     * ------------------------------------
     * Important : cette méthode est exécutée
     * à la fois côté CLIENT et SERVEUR
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(
            World worldIn,
            EntityPlayer playerIn,
            EnumHand handIn
    ) {

        // Récupération de l’ItemStack tenu
        ItemStack stack = Objects.requireNonNull(
                playerIn.getHeldItem(handIn)
        );

        /*
         * CÔTÉ SERVEUR
         * ------------
         * On ne fait RIEN ici
         * → évite les crashes (GUI = client-only)
         */
        if (!worldIn.isRemote) {
            return new ActionResult<>(
                    EnumActionResult.PASS,
                    stack
            );
        }

        /*
         * CÔTÉ CLIENT
         * -----------
         * RayTrace + ouverture de GUI
         */
        if (playerIn != null) {

            // RayTrace : récupère le bloc regardé (5 blocs max)
            BlockPos pos = Objects.requireNonNull(
                    playerIn.rayTrace(5.0D, 1.0F)
            ).getBlockPos();

            // État du bloc ciblé
            IBlockState state = worldIn.getBlockState(pos);

            // Affichage de la GUI de debug
            Minecraft.getMinecraft().displayGuiScreen(
                    new GuiDebugStick(pos, state)
            );

            // Message de confirmation dans le chat
            playerIn.addChatMessage(
                    new TextComponentString("DebugStick activated")
            );
        }

        return new ActionResult<>(
                EnumActionResult.SUCCESS,
                stack
        );
    }
}
