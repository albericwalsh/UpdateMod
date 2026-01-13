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
 * Version 1.1 : Ajout des noms et catégories pour chaque icône
 */
public class SignIcons {

    /**
     * Données d'une icône
     */
    public static class IconData {
        public final ResourceLocation texture;
        public final boolean ColorBend;
        public final String category;
        public final String name;
        public final boolean categoryIcon;


        public IconData(ResourceLocation texture, boolean ColorBend, String category, String name, boolean categoryIcon) {
            this.texture = texture;
            this.ColorBend = ColorBend;
            this.category = category;
            this.name = name;
            this.categoryIcon = categoryIcon;
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
            JsonObject root = new JsonParser().parse(reader).getAsJsonObject();
            JsonArray icons = root.getAsJsonArray("icons");

            for (JsonElement el : icons) {
                JsonObject icon = el.getAsJsonObject();

                String token = icon.get("token").getAsString();
                String name = icon.get("name").getAsString();  // ✅ NOUVEAU
                String path = icon.get("path").getAsString();
                boolean tintable = icon.get("tintable").getAsBoolean();
                String category = icon.has("category") ? icon.get("category").getAsString() : "default";

                boolean isCategoryIcon = icon.has("CategoryIcon") && icon.get("CategoryIcon").getAsBoolean();

                ICONS.put(token, new IconData(
                        new ResourceLocation("updatemod", path),
                        tintable,
                        category,
                        name,
                        isCategoryIcon
                ));
            }

            System.out.println("[SignIcons] Loaded " + ICONS.size() + " icons from JSON");

        } catch (Exception e) {
            System.err.println("[SignIcons] Failed to load icons.json!");
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

    public static IconData getCategoryIcon(String category) {
        for (IconData data : ICONS.values()) {
            if (data.category.equals(category) && data.categoryIcon) {
                return data;
            }
        }
        return null;
    }


    /**
     * Découpe une ligne de texte en segments texte + icônes
     */
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