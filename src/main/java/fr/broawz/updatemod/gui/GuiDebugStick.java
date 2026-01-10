package fr.broawz.updatemod.gui;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * GUI du Debug Stick
 *
 * Permet de :
 * - Afficher toutes les propriétés d’un bloc
 * - Modifier leurs valeurs de manière cyclique
 * - Appliquer ou annuler les changements
 *
 * ⚠️ Cette GUI est uniquement côté client
 */
@SideOnly(Side.CLIENT)
public class GuiDebugStick extends GuiScreen {

    /** Position du bloc ciblé */
    private final BlockPos pos;

    /** État original du bloc (avant modification) */
    private final IBlockState originalState;

    /** État modifié temporairement dans la GUI */
    private IBlockState editedState;

    /**
     * Constructeur
     *
     * @param pos   Position du bloc
     * @param state État actuel du bloc
     */
    public GuiDebugStick(BlockPos pos, IBlockState state) {
        this.pos = pos;
        this.originalState = state;
        this.editedState = state; // on commence avec l’état original
    }

    /**
     * Initialisation de la GUI
     * Création dynamique d’un bouton par propriété du bloc
     */
    @Override
    public void initGui() {
        this.buttonList.clear();

        int y = this.height / 4; // position verticale de départ
        int i = 0;

        /**
         * Pour chaque propriété du bloc :
         * - on crée un bouton
         * - le texte affiche "nomPropriété: valeur"
         */
        for (IProperty<?> prop : originalState.getProperties().keySet()) {
            String label = prop.getName() + ": " + originalState.getValue(prop).toString();

            GuiButton button = new GuiButton(
                    i,                          // ID = index de la propriété
                    this.width / 2 - 100,       // centré horizontalement
                    y,
                    200,
                    20,
                    label
            );

            this.buttonList.add(button);
            y += 25; // espacement vertical
            i++;
        }

        /**
         * Boutons de validation / annulation
         */
        GuiButton doneButton = new GuiButton(
                1000,
                this.width / 2 - 100,
                y + 10,
                98,
                20,
                "Valider"
        );

        GuiButton cancelButton = new GuiButton(
                1001,
                this.width / 2 + 2,
                y + 10,
                98,
                20,
                "Annuler"
        );

        this.buttonList.add(doneButton);
        this.buttonList.add(cancelButton);
    }

    /**
     * Gestion des clics sur les boutons
     */
    @Override
    protected void actionPerformed(GuiButton button) {

        // ===== Bouton "Valider" =====
        if (button.id == 1000) {

            /**
             * Ici, on ne modifie pas directement le bloc :
             * on envoie une commande côté serveur
             */
            Minecraft.getMinecraft().thePlayer.sendChatMessage(
                    "/debugstick apply "
                            + pos.getX() + " "
                            + pos.getY() + " "
                            + pos.getZ()
            );

            this.mc.displayGuiScreen(null);
        }

        // ===== Bouton "Annuler" =====
        else if (button.id == 1001) {
            this.mc.displayGuiScreen(null);
        }

        // ===== Boutons des propriétés =====
        else {

            /**
             * Récupération de la propriété associée au bouton
             * L’ID du bouton correspond à l’index dans la liste des propriétés
             */
            IProperty<?> prop = (IProperty<?>) originalState
                    .getProperties()
                    .keySet()
                    .toArray()[button.id];

            // Valeur actuelle
            Comparable current = editedState.getValue(prop);

            /**
             * Liste de toutes les valeurs possibles pour cette propriété
             * Exemple : facing = north / south / east / west
             */
            Collection<?> values = prop.getAllowedValues();
            List<?> list = new ArrayList<>(values);

            // Passage à la valeur suivante (cycle)
            int index = list.indexOf(current);
            index = (index + 1) % list.size();
            Comparable newVal = (Comparable) list.get(index);

            /**
             * Application de la nouvelle valeur à l’état temporaire
             */
            IProperty property = (IProperty) prop;
            editedState = editedState.withProperty(property, newVal);

            // Mise à jour du texte du bouton
            button.displayString = prop.getName() + ": " + newVal.toString();
        }
    }

    /**
     * Empêche la pause du jeu lorsque la GUI est ouverte
     */
    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    /**
     * Rendu de la GUI
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        drawCenteredString(
                this.fontRendererObj,
                "Modifier état du bloc",
                this.width / 2,
                15,
                0xFFFFFF
        );

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
