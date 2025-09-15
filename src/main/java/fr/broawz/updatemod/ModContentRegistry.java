package fr.broawz.updatemod;

import com.google.gson.*;
import fr.broawz.updatemod.blocks.ModBlocks;
import fr.broawz.updatemod.blocks.ModBlocksInit;
import fr.broawz.updatemod.items.ItemDebugStick;
import net.minecraft.creativetab.CreativeTabs;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ModContentRegistry {

    public static void registerCustomBlocks() {
        // Register custom blocks here

        ModBlocksInit.init();

        new ItemDebugStick();
    }

    public static void registerFromJson() {
        InputStream stream = ModContentRegistry.class.getResourceAsStream("/assets/updatemod/data/blocks.json");
        if (stream == null) {
            System.err.println("❌ blocks.json introuvable !");
            return;
        }

        JsonArray array = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonArray();
        for (JsonElement element : array) {
            JsonObject obj = element.getAsJsonObject();

            String id = obj.get("id").getAsString();
            float hardness = obj.get("hardness").getAsFloat();
            String tabName = obj.has("tab") ? obj.get("tab").getAsString() : "building_blocks";

            CreativeTabs tab = getCreativeTabFromName(tabName);

            ModBlocks block = new ModBlocks(id, id);
            block.setCreativeTab(tab);
            block.setHardness(hardness);
        }
    }

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
            case "building_blocks":
            default: return CreativeTabs.BUILDING_BLOCKS;
        }
    }
}
