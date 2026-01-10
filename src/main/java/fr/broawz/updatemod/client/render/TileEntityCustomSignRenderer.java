package fr.broawz.updatemod.client.render;

import fr.broawz.updatemod.blocks.AbstractTileEntitySign;
import fr.broawz.updatemod.client.font.CustomFontRenderer;
import fr.broawz.updatemod.utils.tools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static net.minecraft.client.gui.Gui.drawModalRectWithCustomSizedTexture;

/**
 * TESR pour panneaux personnalisés, compatible OptiFine.
 */
public class TileEntityCustomSignRenderer extends TileEntitySpecialRenderer<AbstractTileEntitySign> {

    /**
     * Calcul largeur ligne + position curseur selon alignement
     */
    private int[] computeLineMetrics(String line, FontRenderer fontRenderer, int iconSize, AbstractTileEntitySign.Align align) {
        List<String> tokens = SignIcons.parseLine(line);

        int lineWidth = 0;
        for (String token : tokens) {
            if (SignIcons.isIcon(token)) {
                lineWidth += iconSize + 2;
            } else {
                for (char c : token.toCharArray()) {
                    if (c == ' ' || c == '\u00A0' || c == '\u2007' || c == '\u2060') {
                        lineWidth += tools.FIXED_SPACE_WIDTH;
                    } else {
                        lineWidth += fontRenderer.getCharWidth(c);
                    }
                }
            }
        }

        int cursorX;
        switch (align) {
            case CENTER:
                cursorX = -lineWidth / 2;
                break;
            case RIGHT:
                cursorX = -lineWidth; // RIGHT → texte “pousse” vers la gauche
                break;
            default:
                cursorX = 0; // LEFT → texte commence exactement sur le bloc
        }

        return new int[]{lineWidth, cursorX};
    }

    @Override
    public void renderTileEntityAt(AbstractTileEntitySign te,
                                   double x, double y, double z,
                                   float partialTicks, int destroyStage) {

        GlStateManager.pushMatrix();

        // Placement au centre du bloc
        GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5F);

        EnumFacing textFacing = te.getFacing().getOpposite();
        float frontOffset = 0.497F; // légèrement devant la face du bloc

        // Rotation + translation selon orientation
        if (textFacing == EnumFacing.NORTH) {
            GlStateManager.translate(-0.5F, 0, -frontOffset);
        } else if (textFacing == EnumFacing.SOUTH) {
            GlStateManager.translate(-0.5F, 0, frontOffset);
            GlStateManager.rotate(180, 0, 1, 0);
        } else if (textFacing == EnumFacing.WEST) {
            GlStateManager.translate(-frontOffset, 0, -0.5F);
            GlStateManager.rotate(90, 0, 1, 0);
        } else if (textFacing == EnumFacing.EAST) {
            GlStateManager.translate(frontOffset, 0, -0.5F);
            GlStateManager.rotate(-90, 0, 1, 0);
        }

        /* ========================================================= */
        /* === OFFSET LOGIQUE DE DÉPART SELON ALIGN + TEXTSPAN ==== */
        /* ========================================================= */

        float logicalOffsetX = 0F;

        if (te.getAlign() == AbstractTileEntitySign.Align.RIGHT) {
            // RIGHT commence à 1 bloc à droite
            logicalOffsetX = 1F;
        }
        else if (te.getAlign() == AbstractTileEntitySign.Align.CENTER) {
            // CENTER : span impair → décalage de 0.5
            if ((te.getTextSpan() & 1) == 1) {
                logicalOffsetX = 0.5F;
            }
        }

// Application de l’offset dans le repère local
        GlStateManager.translate(logicalOffsetX, 0, 0);


        // Mise à l'échelle du texte
        GlStateManager.scale(te.getTextScale(), -te.getTextScale(), te.getTextScale());

        /* ========================================================= */
        /* === PASSE 1 : SURLIGNAGE ============================== */
        /* ========================================================= */
        for (int i = 0; i < 4; i++) {
            FontRenderer fontRenderer = CustomFontRenderer.getFont(te.getFontName()[i], te.getFontSize()[i]);
            String line = te.getLines()[i];
            int highlight = te.getLineHighlightColor()[i];
            int highlightHeight = te.getHighlightHeight()[i];
            int iconSize = fontRenderer.FONT_HEIGHT;

            if (highlight == 0) continue;

            int[] metrics = computeLineMetrics(line, fontRenderer, iconSize, te.getAlign());
            int lineWidth = metrics[0];
            int cursorX = metrics[1];

            GlStateManager.pushMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.disableCull();

            // Mettre le highlight légèrement “derrière” le texte
            GlStateManager.translate(0, 0, -0.001F);

            float r = ((highlight >> 16) & 0xFF) / 255F;
            float g = ((highlight >> 8) & 0xFF) / 255F;
            float b = (highlight & 0xFF) / 255F;

            int top = i * 10 - 20 - highlightHeight / 2 + iconSize / 2;
            int bottom = top + highlightHeight;

            Tessellator tess = Tessellator.getInstance();
            VertexBuffer buffer = tess.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(cursorX, bottom, 0).color(r, g, b, 1).endVertex();
            buffer.pos(cursorX + lineWidth, bottom, 0).color(r, g, b, 1).endVertex();
            buffer.pos(cursorX + lineWidth, top, 0).color(r, g, b, 1).endVertex();
            buffer.pos(cursorX, top, 0).color(r, g, b, 1).endVertex();
            tess.draw();

            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }

        /* ========================================================= */
        /* === PASSE 2 : TEXTE & ICÔNES ========================== */
        /* ========================================================= */
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.color(1f, 1f, 1f, 1f);

        for (int i = 0; i < 4; i++) {
            FontRenderer fontRenderer = CustomFontRenderer.getFont(te.getFontName()[i], te.getFontSize()[i]);
            String line = te.getLines()[i];
            int color = te.getTextColor()[i];
            int iconSize = fontRenderer.FONT_HEIGHT;

            int[] metrics = computeLineMetrics(line, fontRenderer, iconSize, te.getAlign());
            int cursorX = metrics[1];

            List<String> tokens = SignIcons.parseLine(line);
            int textOffsetY = i * 10 - 20; // texte au-dessus du highlight

            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO
            );

            for (String token : tokens) {
                if (SignIcons.isIcon(token)) {
                    SignIcons.IconData data = SignIcons.getIconData(token);
                    if (data != null) {
                        Minecraft.getMinecraft().getTextureManager().bindTexture(data.texture);
                        if (data.ColorBend) {
                            float r = ((color >> 16) & 0xFF) / 255F;
                            float g = ((color >> 8) & 0xFF) / 255F;
                            float b = (color & 0xFF) / 255F;
                            GlStateManager.color(r, g, b, 1);
                        } else {
                            GlStateManager.color(1, 1, 1, 1);
                        }

                        drawModalRectWithCustomSizedTexture(cursorX, textOffsetY, 0, 0, iconSize, iconSize, iconSize, iconSize);
                        cursorX += iconSize + 2;
                    }
                } else {
                    for (char c : token.toCharArray()) {
                        int charWidth;
                        if (c == ' ' || c == '\u00A0' || c == '\u2007' || c == '\u2060') {
                            charWidth = tools.FIXED_SPACE_WIDTH;
                        } else {
                            charWidth = fontRenderer.getCharWidth(c);
                            fontRenderer.drawString(String.valueOf(c), cursorX, textOffsetY, color);
                        }
                        cursorX += charWidth;
                    }
                }
            }

            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
        }

        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.popMatrix();
    }
}
