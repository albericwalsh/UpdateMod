package fr.broawz.updatemod.client.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

public class CustomFontRenderer {

    private static FontRenderer fontRenderer;

    public static FontRenderer getFont(String fontName, int size) {
        if (fontRenderer == null) {
            try {
                java.awt.Font awtFont = new java.awt.Font(fontName, java.awt.Font.PLAIN, size);
                fontRenderer = new FontRenderer(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);
                fontRenderer.setUnicodeFlag(true); // Unicode si nécessaire
            } catch (Exception e) {
                e.printStackTrace();
                fontRenderer = Minecraft.getMinecraft().fontRendererObj;
            }
        }
        return fontRenderer;
    }
}
