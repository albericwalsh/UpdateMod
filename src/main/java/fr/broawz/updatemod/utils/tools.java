package fr.broawz.updatemod.utils;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.VertexBuffer;

/**
 * Classe utilitaire pour des fonctions graphiques et texte
 *
 * Contient :
 * - Largeur fixe pour certains espaces
 * - Conversion couleur hex → ARGB
 * - Correction de double-spaces
 * - Dessin de rectangles colorés (quad)
 */
public class tools {

    // --- Constante ---
    // Largeur fixe pour le rendu de certains espaces (équivalent vanilla Minecraft)
    public static final int FIXED_SPACE_WIDTH = 4;

    /**
     * Convertit un entier RGB hexadécimal en couleur ARGB complète (avec alpha = 255)
     * @param hexRGB couleur RGB (ex: 0xFF00FF)
     * @return couleur ARGB (0xAARRGGBB)
     */
    public static int color(int hexRGB) {
        return 0xFF000000 | (hexRGB & 0xFFFFFF);
    }

    /**
     * Remplace les doubles espaces par un token spécial "<SPACE>"
     * Utile pour conserver la mise en page ou découper les chaînes
     * @param input chaîne d’entrée
     * @return chaîne modifiée
     */
    public static String fixSpaces(String input) {
        return input.replace("  ", "<SPACE>");
    }

    /**
     * Dessine un rectangle coloré (quad) directement avec les coordonnées et couleurs
     * @param x1 coordonnée gauche
     * @param y1 coordonnée haut
     * @param x2 coordonnée droite
     * @param y2 coordonnée bas
     * @param r composante rouge (0.0f → 1.0f)
     * @param g composante verte (0.0f → 1.0f)
     * @param b composante bleue (0.0f → 1.0f)
     */
    public static void drawColoredQuadRaw(int x1, int y1, int x2, int y2, float r, float g, float b) {
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer buffer = tess.getBuffer();

        // Démarre le dessin d’un quad avec couleur
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        // Définit les 4 sommets du rectangle
        buffer.pos(x1, y2, 0).color(r, g, b, 1.0F).endVertex(); // coin bas-gauche
        buffer.pos(x2, y2, 0).color(r, g, b, 1.0F).endVertex(); // coin bas-droit
        buffer.pos(x2, y1, 0).color(r, g, b, 1.0F).endVertex(); // coin haut-droit
        buffer.pos(x1, y1, 0).color(r, g, b, 1.0F).endVertex(); // coin haut-gauche

        // Applique le dessin
        tess.draw();
    }
}
