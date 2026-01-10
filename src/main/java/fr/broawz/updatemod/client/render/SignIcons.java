package fr.broawz.updatemod.client.render;

import net.minecraft.util.ResourceLocation;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SignIcons
 * ---------
 * Classe utilitaire responsable de :
 *  - Définir les icônes utilisables dans les panneaux
 *  - Associer un token texte ([PL], [^], etc.) à une texture
 *  - Découper une ligne de texte en segments texte / icônes
 *
 * Utilisée principalement par le renderer des panneaux.
 */
public class SignIcons {

    /**
     * IconData
     * --------
     * Structure de données décrivant une icône.
     */
    public static class IconData {

        /** Texture de l’icône */
        public final ResourceLocation texture;

        /**
         * ColorBend :
         *  - true  → l’icône conserve ses couleurs originales
         *  - false → l’icône peut être teintée avec la couleur de la ligne
         */
        public final boolean ColorBend;

        public IconData(ResourceLocation texture, boolean ColorBend) {
            this.texture = texture;
            this.ColorBend = ColorBend;
        }
    }

    /**
     * Dictionnaire des icônes disponibles
     * -----------------------------------
     * Clé   : token texte (ex: "[PL]")
     * Valeur : données de rendu de l’icône
     */
    private static final Map<String, IconData> ICONS = new HashMap<>();

    /**
     * Initialisation statique des icônes
     * ----------------------------------
     * Ajouter ici toutes les icônes reconnues par les panneaux.
     */
    static {
        ICONS.put("[v]",  new IconData(new ResourceLocation("updatemod", "textures/icons/arrow_down.png"),  true));
        ICONS.put("[>]",  new IconData(new ResourceLocation("updatemod", "textures/icons/arrow_right.png"), true));
        ICONS.put("[<]",  new IconData(new ResourceLocation("updatemod", "textures/icons/arrow_left.png"),  true));
        ICONS.put("[^]",  new IconData(new ResourceLocation("updatemod", "textures/icons/arrow_up.png"),    true));

        ICONS.put("[HW]", new IconData(new ResourceLocation("updatemod", "textures/icons/hightway.png"),     false));
        ICONS.put("[!PL]",new IconData(new ResourceLocation("updatemod", "textures/icons/no_truck.png"),    false));
        ICONS.put("[PL]", new IconData(new ResourceLocation("updatemod", "textures/icons/truck.png"),       false));
        ICONS.put("[AP]", new IconData(new ResourceLocation("updatemod", "textures/icons/airport.png"),     false));

        ICONS.put("[EX]", new IconData(new ResourceLocation("updatemod", "textures/icons/exit.png"),         true));

        // ➕ Ajouter d'autres icônes ici
    }

    /**
     * Vérifie si un token correspond à une icône valide
     */
    public static boolean isIcon(String token) {
        return ICONS.containsKey(token);
    }

    /**
     * Retourne les données complètes d’une icône
     */
    public static IconData getIconData(String token) {
        return ICONS.get(token);
    }

    /**
     * Retourne uniquement la texture associée à un token
     */
    public static ResourceLocation getIcon(String token) {
        IconData data = ICONS.get(token);
        return data != null ? data.texture : null;
    }

    /**
     * Retourne la map complète des icônes
     * (utile pour debug ou extensions futures)
     */
    public static Map<String, IconData> getAllIconTokens() {
        return ICONS;
    }

    /**
     * Découpe une ligne de texte en segments :
     *  - texte normal
     *  - tokens d’icônes ([PL], [^], etc.)
     *
     * Exemple :
     * "Route [HW] Nord [^]"
     * → ["Route ", "[HW]", " Nord ", "[^]"]
     */
    public static List<String> parseLine(String line) {
        List<String> result = new ArrayList<>();
        if (line == null || line.isEmpty()) return result;

        /**
         * Regex :
         *  - Capture tout ce qui est entre crochets []
         *  - Autorise lettres, chiffres et symboles directionnels
         */
        Matcher matcher = Pattern
                .compile("(\\[[!A-Za-z0-9<>^]+\\])")
                .matcher(line);

        int lastEnd = 0;

        while (matcher.find()) {

            // Texte avant l’icône
            if (matcher.start() > lastEnd) {
                String before = line.substring(lastEnd, matcher.start());
                if (!before.isEmpty()) result.add(before);
            }

            // Token icône
            result.add(matcher.group());
            lastEnd = matcher.end();
        }

        // Texte après la dernière icône
        if (lastEnd < line.length()) {
            String after = line.substring(lastEnd);
            if (!after.isEmpty()) result.add(after);
        }

        return result;
    }
}
