package fr.broawz.updatemod.client.render;

import fr.broawz.updatemod.blocks.AbstractTileEntitySign;
import fr.broawz.updatemod.client.font.CustomFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.List;

import static fr.broawz.updatemod.utils.tools.color;
import static net.minecraft.client.gui.Gui.drawModalRectWithCustomSizedTexture;
import static net.minecraft.client.gui.Gui.drawRect;

public class TileEntityCustomSignRenderer extends TileEntitySpecialRenderer<AbstractTileEntitySign> {

    @Override
    public void renderTileEntityAt(AbstractTileEntitySign te, double x, double y, double z, float partialTicks, int destroyStage) {
        GlStateManager.pushMatrix();

        // Centrer sur le bloc
        GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5F);
        EnumFacing textFacing = te.getFacing().getOpposite();

        switch (textFacing) {
            case NORTH: GlStateManager.translate(0, 0, -0.498); break;
            case SOUTH: GlStateManager.translate(0, 0, 0.498); GlStateManager.rotate(180, 0, 1, 0); break;
            case WEST:  GlStateManager.translate(-0.498, 0, 0); GlStateManager.rotate(90, 0, 1, 0); break;
            case EAST:  GlStateManager.translate(0.498, 0, 0); GlStateManager.rotate(-90, 0, 1, 0); break;
        }

        GlStateManager.scale(te.getTextScale(), -te.getTextScale(), te.getTextScale());

        for (int i = 0; i < 4; i++) {
            FontRenderer fontRenderer = CustomFontRenderer.getFont(te.getFontName()[i], te.getFontSize()[i]);
            String line = te.getLines()[i];
            int color = te.getTextColor()[i];
            int highlight = te.getLineHighlightColor()[i];
            int highlightHeight = te.getHighlightHeight()[i];
            int height = fontRenderer.FONT_HEIGHT;

            // --- surlignage ---
            if (highlight != 0) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0, 0, -0.001F);
                int width = fontRenderer.getStringWidth(line);
                int top = (i * 10 - 20 - highlightHeight / 2 + height / 2) + 1;
                int bottom = top + highlightHeight;
                drawRect(-width / 2 - 1, top, width / 2 + 1, bottom, color(highlight));
                GlStateManager.popMatrix();
            }

            // --- découper ligne en tokens texte + icônes ---
            List<String> tokens = SignIcons.parseLine(line);

            // largeur totale pour centrer
            int totalWidth = 0;
            for (String token : tokens) {
                if (SignIcons.isIcon(token)) totalWidth += height + 2;
                else totalWidth += fontRenderer.getStringWidth(token);
            }

            int cursorX = -totalWidth / 2;

            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();

            // ---- première passe : icônes ----
            for (String token : tokens) {
                if (SignIcons.isIcon(token)) {
                    ResourceLocation iconTex = SignIcons.getIcon(token);
                    if (iconTex != null) {
                        SignIcons.IconData data = SignIcons.getIconData(token);
                        if (data != null) {
                            Minecraft.getMinecraft().getTextureManager().bindTexture(data.texture);

                            // applique la couleur seulement si pas trop foncé et si pas noColorBend
                            if (data.ColorBend) {
                                float r = ((color >> 16) & 0xFF) / 255f;
                                float g = ((color >> 8) & 0xFF) / 255f;
                                float b = (color & 0xFF) / 255f;
                                GlStateManager.color(r, g, b, 1.0F);
                            } else {
                                GlStateManager.color(1f, 1f, 1f, 1f); // couleur blanche par défaut
                            }

                            int iconSize = height;
                            drawModalRectWithCustomSizedTexture(cursorX, i * 10 - 20, 0, 0, iconSize, iconSize, iconSize, iconSize);
                        }
                    }
                }
                if (SignIcons.isIcon(token)) cursorX += height + 2;
                else cursorX += fontRenderer.getStringWidth(token);
            }

            // ---- deuxième passe : texte ----
            cursorX = -totalWidth / 2;
            for (String token : tokens) {
                if (!SignIcons.isIcon(token)) {
                    fontRenderer.drawString(token, cursorX, i * 10 - 20, color);
                }
                cursorX += SignIcons.isIcon(token) ? height + 2 : fontRenderer.getStringWidth(token);
            }

            GlStateManager.disableBlend();
            GlStateManager.color(1f, 1f, 1f, 1f); // reset couleur
        }

        GlStateManager.popMatrix();
    }
}
