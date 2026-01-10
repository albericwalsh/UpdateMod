package fr.broawz.updatemod;

/*
 * Imports Gson
 * → Utilisés pour charger dynamiquement du contenu depuis un fichier JSON
 */
import com.google.gson.*;

/*
 * Imports internes au mod
 */
import fr.broawz.updatemod.blocks.ModBlocks;       // Classe représentant un bloc générique
import fr.broawz.updatemod.blocks.ModBlocksInit;   // Initialisation "classique" des blocs
import fr.broawz.updatemod.items.ItemDebugStick;   // Item de debug (probablement utilitaire)

/*
 * Imports Minecraft
 */
import net.minecraft.creativetab.CreativeTabs;

import java.io.InputStream;
import java.io.InputStreamReader;

/*
 * Registre central du contenu du mod
 * ----------------------------------
 * Rôle :
 *  - Initialiser les blocs / items
 *  - Centraliser les points d’entrée du contenu
 *  - Offrir une alternative DATA-DRIVEN via JSON
 */
public class ModContentRegistry {

    /*
     * Méthode d’enregistrement "classique"
     * ------------------------------------
     * → Appelée depuis UpdateMod.preInit()
     * → Contient le contenu codé en dur
     */
    public static void registerCustomBlocks() {
        // Initialisation des blocs définis en Java
        ModBlocksInit.init();

        // Création d’un item de debug
        // (le constructeur s’enregistre probablement tout seul)
        new ItemDebugStick();
    }

    /*
     * Méthode d’enregistrement depuis un fichier JSON
     * -----------------------------------------------
     * Objectif :
     *  - Définir des blocs sans recompiler le mod
     *  - Approche data-driven (scalable, maintenable)
     *
     * ⚠️ Actuellement NON appelée (commentée dans UpdateMod)
     */
    public static void registerFromJson() {

        // Chargement du fichier JSON depuis les resources
        InputStream stream = ModContentRegistry.class
                .getResourceAsStream("/assets/updatemod/data/blocks.json");

        // Sécurité : fichier absent
        if (stream == null) {
            System.err.println("❌ blocks.json introuvable !");
            return;
        }

        // Parsing du fichier JSON en tableau
        JsonArray array = new JsonParser()
                .parse(new InputStreamReader(stream))
                .getAsJsonArray();

        // Parcours de chaque définition de bloc
        for (JsonElement element : array) {

            JsonObject obj = element.getAsJsonObject();

            // Identifiant du bloc
            String id = obj.get("id").getAsString();

            // Dureté du bloc
            float hardness = obj.get("hardness").getAsFloat();

            // Onglet créatif (optionnel)
            String tabName = obj.has("tab")
                    ? obj.get("tab").getAsString()
                    : "building_blocks";

            // Conversion String → CreativeTabs
            CreativeTabs tab = getCreativeTabFromName(tabName);

            // Création dynamique du bloc
            ModBlocks block = new ModBlocks(id, id);

            // Application des propriétés
            block.setCreativeTab(tab);
            block.setHardness(hardness);
        }
    }

    /*
     * Méthode utilitaire
     * ------------------
     * Convertit un nom textuel (JSON)
     * en CreativeTabs Minecraft
     */
    private static CreativeTabs getCreativeTabFromName(String name) {
        switch (name) {
            case "decorations": return CreativeTabs.DECORATIONS;
            case "redstone": return CreativeTabs.REDSTONE;
            case "transportation": return CreativeTabs.TRANSPORTATION;
            case "misc": return CreativeTabs.MISC;
            case "food": return CreativeTabs.FOOD;
            case "tools": return CreativeTabs.TOOLS;
            case "combat": return CreativeTabs.COMBAT;
            case "brewing": return CreativeTabs.BREWING;
            case "materials": return CreativeTabs.MATERIALS;

            // Valeur par défaut
            case "building_blocks":
            default:
                return CreativeTabs.BUILDING_BLOCKS;
        }
    }
}
