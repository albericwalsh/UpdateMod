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
 * Renderer TESR pour les panneaux personnalisés.
 * Compatible OptiFine + Shaders.
 *
 * Gère :
 *  - Alignement du texte (LEFT, CENTER, RIGHT)
 *  - Couleurs par ligne
 *  - Surlignage avec correction des normales pour Sud/Ouest
 *  - Icônes intégrées dans le texte
 *  - Orientation du panneau (NORTH, SOUTH, EAST, WEST)
 */
public class TileEntityCustomSignRenderer extends TileEntitySpecialRenderer<AbstractTileEntitySign> {

    /**
     * Calcule la largeur totale d'une ligne et la position initiale du curseur selon l'alignement
     *
     * @param line Ligne de texte à analyser
     * @param fontRenderer FontRenderer pour calculer les largeurs
     * @param iconSize Taille des icônes
     * @param align Alignement demandé
     * @return [0] = largeur totale, [1] = position X de départ
     */
    private int[] computeLineMetrics(String line, FontRenderer fontRenderer, int iconSize, AbstractTileEntitySign.Align align) {
        List<String> tokens = SignIcons.parseLine(line);

        // Calcul de la largeur totale
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

        // Position de départ selon l'alignement
        int cursorX;
        switch (align) {
            case CENTER:
                cursorX = -lineWidth / 2;
                break;
            case RIGHT:
                cursorX = -lineWidth;
                break;
            default: // LEFT
                cursorX = 0;
                break;
        }

        return new int[]{lineWidth, cursorX};
    }

    @Override
    public void renderTileEntityAt(AbstractTileEntitySign te,
                                   double x, double y, double z,
                                   float partialTicks, int destroyStage) {

        GlStateManager.pushMatrix();

        /* ========================================================= */
        /* === CALCUL DE L'ALIGNEMENT HORIZONTAL DU TEXTE ========= */
        /* ========================================================= */

        float offsetX;

        switch (te.getAlign()) {
            case LEFT:
                offsetX = -0.5F;
                break;
            case RIGHT:
                offsetX = (te.getTextSpan() - 1) + 0.5F;
                break;
            default: // CENTER
                offsetX = (te.getTextSpan() - 1) / 2F;
                break;
        }

        /* ========================================================= */
        /* === POSITIONNEMENT DANS LE MONDE ======================= */
        /* ========================================================= */

        // Placement au centre du bloc
        GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5F);

        // Le texte regarde dans le sens opposé au panneau
        EnumFacing textFacing = te.getFacing().getOpposite();
        float offsetY = 0.497F; // Légèrement devant la face du bloc

        // Ajustement position + rotation selon la face
        switch (textFacing) {
            case NORTH:
                GlStateManager.translate(offsetX, 0, -offsetY);
                break;
            case SOUTH:
                GlStateManager.translate(-offsetX, 0, offsetY);
                GlStateManager.rotate(180, 0, 1, 0);
                break;
            case WEST:
                GlStateManager.translate(-offsetY, 0, offsetX);
                GlStateManager.rotate(90, 0, 1, 0);
                break;
            case EAST:
                GlStateManager.translate(offsetY, 0, -offsetX);
                GlStateManager.rotate(-90, 0, 1, 0);
                break;
        }

        // Mise à l'échelle du texte (pixels → monde)
        GlStateManager.scale(te.getTextScale(), -te.getTextScale(), te.getTextScale());

        /* ========================================================= */
        /* === PASSE 1 : DESSIN DES SURLIGNAGES =================== */
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

            // États GL pour OptiFine : ordre important
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();

            // Désactiver depth write mais garder depth test
            GlStateManager.depthMask(false);
            GlStateManager.enableDepth();

            // Pas de blend pour un rendu opaque
            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();

            // Désactiver face culling
            GlStateManager.disableCull();

            // Forcer couleur blanche avant rendu
            GlStateManager.color(1f, 1f, 1f, 1f);

            // Petit décalage Z pour mettre derrière le texte
            GlStateManager.translate(0, 0, -0.001F);

            // Extraction des composants RGB
            float r = ((highlight >> 16) & 0xFF) / 255F;
            float g = ((highlight >> 8) & 0xFF) / 255F;
            float b = (highlight & 0xFF) / 255F;

            int top = i * 10 - 20 - highlightHeight / 2 + iconSize / 2;
            int bottom = top + highlightHeight;

            // Dessin du rectangle de surlignage
            Tessellator tess = Tessellator.getInstance();
            VertexBuffer buffer = tess.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(cursorX, bottom, 0).color(r, g, b, 1).endVertex();
            buffer.pos(cursorX + lineWidth, bottom, 0).color(r, g, b, 1).endVertex();
            buffer.pos(cursorX + lineWidth, top, 0).color(r, g, b, 1).endVertex();
            buffer.pos(cursorX, top, 0).color(r, g, b, 1).endVertex();
            tess.draw();

            // Restaurer les états dans l'ordre inverse
            GlStateManager.enableCull();
            GlStateManager.depthMask(true);
            GlStateManager.enableFog();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();

            GlStateManager.popMatrix();
        }

        /* ========================================================= */
        /* === PASSE 2 : DESSIN DU TEXTE ET DES ICÔNES ============ */
        /* ========================================================= */

        // Réinitialiser complètement les états pour le texte
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

            // Position Y du texte
            int textOffsetY = i * 10 - 20;

            // États pour le texte
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
                    // Dessin d'une icône
                    SignIcons.IconData data = SignIcons.getIconData(token);
                    if (data != null) {
                        Minecraft.getMinecraft().getTextureManager().bindTexture(data.texture);

                        if (data.ColorBend) {
                            // Icône teintée avec la couleur du texte
                            float r = ((color >> 16) & 0xFF) / 255F;
                            float g = ((color >> 8) & 0xFF) / 255F;
                            float b = (color & 0xFF) / 255F;
                            GlStateManager.color(r, g, b, 1);
                        } else {
                            // Icône avec ses couleurs originales
                            GlStateManager.color(1, 1, 1, 1);
                        }

                        drawModalRectWithCustomSizedTexture(
                                cursorX, textOffsetY,
                                0, 0,
                                iconSize, iconSize,
                                iconSize, iconSize
                        );

                        cursorX += iconSize + 2;
                    }
                } else {
                    // Dessin de texte caractère par caractère
                    for (char c : token.toCharArray()) {
                        int charWidth;

                        if (c == ' ' || c == '\u00A0' || c == '\u2007' || c == '\u2060') {
                            // Espaces : utiliser largeur fixe
                            charWidth = tools.FIXED_SPACE_WIDTH;
                        } else {
                            // Caractère visible : dessiner et utiliser sa largeur réelle
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

        // Nettoyage final
        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.popMatrix();
    }
}