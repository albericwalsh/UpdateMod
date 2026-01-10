package fr.broawz.updatemod.blocks;

/**
 * SignPreset
 * ----------
 * Classe qui définit un “preset” visuel pour un panneau.
 * Chaque preset contient :
 *  - Les couleurs des lignes de texte
 *  - Les couleurs de surlignage (highlight) pour chaque ligne
 *
 * Utilisé par :
 *  - BlockBasicSign
 *  - AbstractTileEntitySign
 *  - PacketUpdateSign
 *
 * Remarques :
 *  - Immuable : pas de modification après création
 *  - Toujours 4 lignes
 */
public class SignPreset {

    /** Valeur indiquant “pas de highlight” */
    public static final int NO_HIGHLIGHT = 0x00000000;

    /** Couleurs du texte pour chaque ligne (4 éléments) */
    private final int[] lineColors;

    /** Couleurs de surlignage pour chaque ligne (4 éléments) */
    private final int[] lineHighlightColors;

    /**
     * Constructeur principal
     * ----------------------
     * Crée un preset avec des couleurs personnalisées
     *
     * @param lineColors couleurs du texte (taille = 4)
     * @param lineHighlightColors couleurs de surlignage (taille = 4)
     * @throws IllegalArgumentException si les tableaux n'ont pas exactement 4 éléments
     */
    public SignPreset(int[] lineColors, int[] lineHighlightColors) {
        checkLength(lineColors);
        checkLength(lineHighlightColors);

        // Clone pour éviter que des modifications externes corrompent le preset
        this.lineColors = lineColors.clone();
        this.lineHighlightColors = lineHighlightColors.clone();
    }

    /**
     * Retourne les couleurs de texte
     * -------------------------------
     * @return copie du tableau de 4 couleurs
     */
    public int[] getLineColors() {
        return lineColors.clone();
    }

    /**
     * Retourne les couleurs de surlignage
     * -----------------------------------
     * @return copie du tableau de 4 couleurs
     */
    public int[] getLineHighlightColors() {
        return lineHighlightColors.clone();
    }

    /**
     * Vérifie que le tableau contient exactement 4 éléments
     * -----------------------------------------------------
     * @param arr tableau à vérifier
     * @throws IllegalArgumentException si taille != 4
     */
    private static void checkLength(int[] arr) {
        if (arr == null || arr.length != 4) {
            throw new IllegalArgumentException(
                    "SignPreset arrays must contain exactly 4 elements"
            );
        }
    }

    /**
     * Factory pour créer un preset simple
     * ----------------------------------
     * - Même couleur pour toutes les lignes
     * - Pas de highlight
     *
     * @param color couleur du texte
     * @return nouveau SignPreset
     */
    public static SignPreset newPreset(int color) {
        return new SignPreset(
                new int[]{color, color, color, color},
                new int[]{NO_HIGHLIGHT, NO_HIGHLIGHT, NO_HIGHLIGHT, NO_HIGHLIGHT}
        );
    }

    /**
     * Factory pour créer un preset avec des couleurs personnalisées
     * ----------------------------------------------------------------
     * - Texte et highlight peuvent être différents pour chaque ligne
     *
     * @param textColors couleurs du texte
     * @param highlightColors couleurs de surlignage
     * @return nouveau SignPreset
     */
    public static SignPreset newPreset(int[] textColors, int[] highlightColors) {
        return new SignPreset(textColors, highlightColors);
    }

}
