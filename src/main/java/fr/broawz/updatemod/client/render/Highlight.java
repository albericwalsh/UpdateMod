package fr.broawz.updatemod.client.render;

import fr.broawz.updatemod.blocks.AbstractBlockSign;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class Highlight {
    /**
     * HighlightData
     * --------
     * Structure de données décrivant une icône.
     */
    public static class HighlightData {

        /** Texture de l’icône */
        public final ResourceLocation texture;

        /**
         * ColorBend :
         *  - true  → l’icône conserve ses couleurs originales
         *  - false → l’icône peut être teintée avec la couleur de la ligne
         */

        public HighlightData(ResourceLocation texture) {
            this.texture = texture;
        }

        private static final Map<AbstractBlockSign.SignVariant, HighlightData> COLOR = new HashMap<>();

        static {
            COLOR.put(AbstractBlockSign.SignVariant.BLACK,  new HighlightData(new ResourceLocation("updatemod", "textures/highlight/black.png")));
            COLOR.put(AbstractBlockSign.SignVariant.BLUE,  new HighlightData(new ResourceLocation("updatemod", "textures/highlight/blue.png")));
            COLOR.put(AbstractBlockSign.SignVariant.GREEN,  new HighlightData(new ResourceLocation("updatemod", "textures/highlight/green.png")));
            COLOR.put(AbstractBlockSign.SignVariant.PURPLE,  new HighlightData(new ResourceLocation("updatemod", "textures/highlight/purple.png")));
            COLOR.put(AbstractBlockSign.SignVariant.RED,  new HighlightData(new ResourceLocation("updatemod", "textures/highlight/red.png")));
            COLOR.put(AbstractBlockSign.SignVariant.YELLOW,  new HighlightData(new ResourceLocation("updatemod", "textures/highlight/yellow.png")));
        }

        /**
         * Vérifie si un token correspond à une icône valide
         */
        public static boolean isColor(AbstractBlockSign.SignVariant token) {
            return COLOR.containsKey(token);
        }

        /**
         * Retourne les données complètes d’une icône
         */
        public static HighlightData getColorData(AbstractBlockSign.SignVariant token) {
            if (token == null) return null;
            return COLOR.get(token);
        }


        /**
         * Retourne uniquement la texture associée à un token
         */
        public static ResourceLocation getColor(AbstractBlockSign.SignVariant token) {
            HighlightData data = COLOR.get(token);
            return data != null ? data.texture : null;
        }

        /**
         * Retourne la map complète des icônes
         * (utile pour debug ou extensions futures)
         */
        public static Map<AbstractBlockSign.SignVariant, HighlightData> getAllColorTokens() {
            return COLOR;
        }

        /**
         * Indique si ce variant possède un highlight
         */
        public static boolean hasHighlight(AbstractBlockSign.SignVariant variant) {
            return variant != null && COLOR.containsKey(variant);
        }

        /**
         * Retourne les données de highlight ou null si absent
         */
        public static HighlightData getHighlightData(AbstractBlockSign.SignVariant variant) {
            if (variant == null) return null;
            return COLOR.get(variant);
        }

    }

}
