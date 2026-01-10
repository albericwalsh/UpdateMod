package fr.broawz.updatemod.client.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * CustomFontRenderer
 * ------------------
 * Classe utilitaire côté client permettant de récupérer un FontRenderer
 * personnalisé à partir d’un nom de police et d’une taille.
 *
 * Objectif :
 *  - Centraliser la gestion des polices
 *  - Éviter de recréer des FontRenderer partout dans le code
 *
 * ⚠️ Limitation actuelle :
 *  - Un seul FontRenderer est stocké (singleton)
 *  - Le paramètre size n’est pas encore réellement utilisé
 */
public class CustomFontRenderer {

    /**
     * Instance unique du FontRenderer
     * --------------------------------
     * Pour l’instant, un seul renderer est conservé en mémoire.
     * Si tu veux plusieurs polices / tailles :
     * → utiliser une Map<String, FontRenderer>
     */
    private static FontRenderer fontRenderer;

    /**
     * Retourne un FontRenderer pour la police demandée
     *
     * @param fontName nom de la police (ex: "Arial")
     * @param size taille souhaitée (actuellement non appliquée)
     * @return FontRenderer utilisable pour le rendu du texte
     */
    public static FontRenderer getFont(String fontName, int size) {

        // Création lazy : on instancie seulement au premier appel
        if (fontRenderer == null) {
            try {
                // Tentative de création d’une police AWT
                // ⚠️ Minecraft ne l’utilise pas directement ici,
                // mais ça prépare le terrain pour un renderer custom futur
                java.awt.Font awtFont =
                        new java.awt.Font(fontName, java.awt.Font.PLAIN, size);

                // Création du FontRenderer Minecraft
                fontRenderer = new FontRenderer(
                        Minecraft.getMinecraft().gameSettings,
                        new ResourceLocation("textures/font/ascii.png"),
                        Minecraft.getMinecraft().renderEngine,
                        false
                );

                // Active l’Unicode (utile pour accents, symboles, etc.)
                fontRenderer.setUnicodeFlag(true);

            } catch (Exception e) {
                // En cas d’erreur :
                // → fallback sur le renderer par défaut de Minecraft
                e.printStackTrace();
                fontRenderer = Minecraft.getMinecraft().fontRendererObj;
            }
        }

        return fontRenderer;
    }
}
