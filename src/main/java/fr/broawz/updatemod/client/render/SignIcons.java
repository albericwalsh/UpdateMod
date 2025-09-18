package fr.broawz.updatemod.client.render;

import net.minecraft.util.ResourceLocation;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignIcons {

    // Classe interne pour stocker info icône
    public static class IconData {
        public final ResourceLocation texture;
        public final boolean ColorBend; // si true, on n'applique pas la couleur de ligne

        public IconData(ResourceLocation texture, boolean ColorBend) {
            this.texture = texture;
            this.ColorBend = ColorBend;
        }
    }

    private static final Map<String, IconData> ICONS = new HashMap<>();

    static {
        ICONS.put("[v]", new IconData(new ResourceLocation("updatemod", "textures/icons/arrow_down.png"), true));
        ICONS.put("[>]", new IconData(new ResourceLocation("updatemod", "textures/icons/arrow_right.png"), true));
        ICONS.put("[<]", new IconData(new ResourceLocation("updatemod", "textures/icons/arrow_left.png"), true));
        ICONS.put("[^]", new IconData(new ResourceLocation("updatemod", "textures/icons/arrow_up.png"), true));
        ICONS.put("[HW]", new IconData(new ResourceLocation("updatemod", "textures/icons/hightway.png"), false));
        ICONS.put("[!PL]", new IconData(new ResourceLocation("updatemod", "textures/icons/no_truck.png"), false));
        ICONS.put("[PL]", new IconData(new ResourceLocation("updatemod", "textures/icons/truck.png"), false));
        ICONS.put("[AP]", new IconData(new ResourceLocation("updatemod", "textures/icons/airport.png"), false));
        ICONS.put("[EX]", new IconData(new ResourceLocation("updatemod", "textures/icons/exit.png"), false));
// Ajouter d'autres icônes ici
    }

    public static boolean isIcon(String token) {
        return ICONS.containsKey(token);
    }

    public static IconData getIconData(String token) {
        return ICONS.get(token);
    }

    public static ResourceLocation getIcon(String token) {
        IconData data = ICONS.get(token);
        return data != null ? data.texture : null;
    }

    public static Map<String, IconData> getAllIconTokens() {
        return ICONS;
    }

    /**
     * Découpe une ligne en texte + icônes (tokens)
     */
    public static List<String> parseLine(String line) {
        List<String> result = new ArrayList<>();
        if (line == null || line.isEmpty()) return result;

        // Regex qui capture [v], [!PL], [ABC], etc.
        Matcher matcher = Pattern.compile("(\\[[!A-Za-z0-9<>^]+\\])").matcher(line);

        int lastEnd = 0;
        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                String before = line.substring(lastEnd, matcher.start());
                if (!before.isEmpty()) result.add(before);
            }
            result.add(matcher.group());
            lastEnd = matcher.end();
        }
        if (lastEnd < line.length()) {
            String after = line.substring(lastEnd);
            if (!after.isEmpty()) result.add(after);
        }
        return result;
    }
}
