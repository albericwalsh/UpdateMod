package fr.broawz.updatemod.client.render;

import fr.broawz.updatemod.blocks.AbstractTileEntitySign;
import fr.broawz.updatemod.client.font.CustomFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;

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

        // Offset selon alignement
        float offsetX;
        switch (te.getAlign()) {
            case LEFT:
                offsetX = -0.5F;
                break;
            case RIGHT:
                offsetX = te.getTextSpan() - 0.5F;
                break;
            default:
                offsetX = te.getTextSpan() / 2F - 0.5F;
                break;
        }

        // Translation selon face
        switch (textFacing) {
            case NORTH:
                GlStateManager.translate(offsetX, 0, -0.498);
                break;
            case SOUTH:
                GlStateManager.translate(-offsetX, 0, 0.498);
                GlStateManager.rotate(180, 0, 1, 0);
                break;
            case WEST:
                GlStateManager.translate(-0.498, 0, offsetX);
                GlStateManager.rotate(90, 0, 1, 0);
                break;
            case EAST:
                GlStateManager.translate(0.498, 0, -offsetX);
                GlStateManager.rotate(-90, 0, 1, 0);
                break;
        }

        GlStateManager.scale(te.getTextScale(), -te.getTextScale(), te.getTextScale());

        for (int i = 0; i < 4; i++) {
            FontRenderer fontRenderer = CustomFontRenderer.getFont(te.getFontName()[i], te.getFontSize()[i]);
            String line = te.getLines()[i];
            int color = te.getTextColor()[i];
            int highlight = te.getLineHighlightColor()[i];
            int highlightHeight = te.getHighlightHeight()[i];
            int height = fontRenderer.FONT_HEIGHT;

            // Découper ligne en tokens texte + icônes
            List<String> tokens = SignIcons.parseLine(line);

            // Calculer largeur totale de la ligne (texte + icônes)
            int lineWidth = 0;
            for (String token : tokens) {
                if (SignIcons.isIcon(token)) {
                    lineWidth += height + 2; // largeur icône + spacing
                } else {
                    lineWidth += fontRenderer.getStringWidth(token);
                }
            }

            // Calculer curseur initial selon alignement
            int cursorXStart;
            switch (te.getAlign()) {
                case LEFT:
                    cursorXStart = 0;
                    break;
                case CENTER:
                    cursorXStart = -lineWidth / 2;
                    break;
                case RIGHT:
                    cursorXStart = -lineWidth;
                    break;
                default:
                    cursorXStart = 0;
                    break;
            }

            // --- Surlignage ---
            if (highlight != 0) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0, 0, -0.001F); // léger décalage Z
                int top = (i * 10 - 20 - highlightHeight / 2 + height / 2) + 1;
                int bottom = top + highlightHeight;
                drawRect(cursorXStart, top, cursorXStart + lineWidth, bottom, color(highlight));
                GlStateManager.popMatrix();
            }

            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();

            // Dessiner texte + icônes
            int cursorX = cursorXStart;
            for (String token : tokens) {
                if (SignIcons.isIcon(token)) {
                    SignIcons.IconData data = SignIcons.getIconData(token);
                    if (data != null) {
                        Minecraft.getMinecraft().getTextureManager().bindTexture(data.texture);
                        if (data.ColorBend) {
                            float r = ((color >> 16) & 0xFF) / 255f;
                            float g = ((color >> 8) & 0xFF) / 255f;
                            float b = (color & 0xFF) / 255f;
                            GlStateManager.color(r, g, b, 1.0F);
                        } else {
                            GlStateManager.color(1f, 1f, 1f, 1f);
                        }
                        drawModalRectWithCustomSizedTexture(cursorX, i * 10 - 20, 0, 0, height, height, height, height);
                    }
                    cursorX += height + 2;
                } else {
                    fontRenderer.drawString(token, cursorX, i * 10 - 20, color);
                    cursorX += fontRenderer.getStringWidth(token);
                }
            }

            GlStateManager.disableBlend();
            GlStateManager.color(1f, 1f, 1f, 1f);
        }

        GlStateManager.popMatrix();
    }
}
