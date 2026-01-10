package fr.broawz.updatemod.blocks.tileentity;

/*
 * Import de la TileEntity abstraite
 * → Contient toute la logique commune aux panneaux
 */
import fr.broawz.updatemod.blocks.AbstractTileEntitySign;

/*
 * TileEntity concrète du panneau basique
 * -------------------------------------
 * Rôle :
 *  - Fournir une implémentation minimale
 *  - Définir l’état initial du panneau
 *  - Ne PAS contenir de logique métier complexe
 */
public class TileEntityBasicSign extends AbstractTileEntitySign {

    /*
     * Constructeur
     * ------------
     * Définit l’état par défaut du panneau
     * au moment où il est placé dans le monde
     */
    public TileEntityBasicSign() {
        super();

        // Initialisation des 4 lignes de texte
        // → vide par défaut
        this.lines = new String[]{"", "", "", ""};

        // Couleur du texte par défaut (blanc)
        setLineColor(new int[]{
                0xFFFFFF,
                0xFFFFFF,
                0xFFFFFF,
                0xFFFFFF
        });
    }
}
