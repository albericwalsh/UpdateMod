package fr.broawz.updatemod.client.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.Minecraft;

import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SignIcons
 * ---------
 * Classe utilitaire pour gérer les icônes depuis JSON.
 */
public class SignIcons {

    public static class IconData {
        public final ResourceLocation texture;
        public final boolean ColorBend;
        public final String category;

        public IconData(ResourceLocation texture, boolean ColorBend, String category) {
            this.texture = texture;
            this.ColorBend = ColorBend;
            this.category = category;
        }
    }

    private static final Map<String, IconData> ICONS = new HashMap<>();

    /**
     * Charge les icônes depuis le fichier JSON.
     * À appeler côté client uniquement.
     */
    public static void loadFromJSON() {
        try (InputStreamReader reader = new InputStreamReader(
                Objects.requireNonNull(Minecraft.class.getResourceAsStream("/assets/updatemod/data/icons.json"))
        )) {
            // ⚠️ Version compatible avec Gson ancien
            JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
            JsonArray icons = root.getAsJsonArray("icons");

            for (JsonElement el : icons) {
                JsonObject icon = el.getAsJsonObject();
                String token = icon.get("token").getAsString();
                String path = icon.get("path").getAsString();
                boolean tintable = icon.get("tintable").getAsBoolean();
                String category = icon.has("category") ? icon.get("category").getAsString() : "default";

                ICONS.put(token, new IconData(new ResourceLocation("updatemod", path), tintable, category));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static List<String> parseLine(String line) {
        List<String> result = new ArrayList<>();
        if (line == null || line.isEmpty()) return result;

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
