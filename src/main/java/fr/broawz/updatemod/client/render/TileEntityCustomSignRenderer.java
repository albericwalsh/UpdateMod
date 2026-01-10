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
 * Gère :
 *  - alignement du texte
 *  - couleurs par ligne
 *  - surlignage
 *  - icônes intégrées dans le texte
 *  - orientation du panneau (facing)
 */
public class TileEntityCustomSignRenderer extends TileEntitySpecialRenderer<AbstractTileEntitySign> {

    /**
     * Méthode appelée automatiquement par Minecraft pour rendre le TileEntity
     */
    @Override
    public void renderTileEntityAt(AbstractTileEntitySign te,
                                   double x, double y, double z,
                                   float partialTicks, int destroyStage) {

        // Sauvegarde de l’état OpenGL
        GlStateManager.pushMatrix();

        /* ========================================================= */
        /* === CALCUL DE L’ALIGNEMENT HORIZONTAL DU TEXTE ========= */
        /* ========================================================= */

        float offsetX; // Décalage horizontal en unités blocs

        switch (te.getAlign()) {
            case LEFT:
                // Texte aligné à gauche par rapport au centre du panneau
                offsetX = -0.5F;
                break;
            case RIGHT:
                // Texte aligné à droite en fonction de la largeur totale (textSpan)
                offsetX = te.getTextSpan() - 0.5F;
                break;
            default:
                // Texte centré
                offsetX = te.getTextSpan() / 2F - 0.5F;
                break;
        }

        /* ========================================================= */
        /* === POSITIONNEMENT DANS LE MONDE ======================= */
        /* ========================================================= */

        // Placement au centre du bloc
        GlStateManager.translate(x + 0.5F, y + 0.5F, z + 0.5F);

        // Le texte regarde toujours dans le sens opposé au panneau
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

        // Mise à l’échelle du texte (pixels → monde)
        GlStateManager.scale(te.getTextScale(), -te.getTextScale(), te.getTextScale());

        /* ========================================================= */
        /* === PASSE 1 : DESSIN DES SURLIGNAGES =================== */
        /* ========================================================= */

        // On dessine d’abord TOUS les fonds, avant le texte
        for (int i = 0; i < 4; i++) {

            FontRenderer fontRenderer =
                    CustomFontRenderer.getFont(te.getFontName()[i], te.getFontSize()[i]);

            String line = te.getLines()[i];
            int highlight = te.getLineHighlightColor()[i];
            int highlightHeight = te.getHighlightHeight()[i];
            int iconSize = fontRenderer.FONT_HEIGHT;

            // Pas de surlignage → on ignore la ligne
            if (highlight == 0) continue;

            // Découpage texte + icônes
            List<String> tokens = SignIcons.parseLine(line);

            /* --- Calcul de la largeur totale de la ligne --- */
            int lineWidth = 0;
            for (String token : tokens) {
                if (SignIcons.isIcon(token)) {
                    lineWidth += iconSize + 2;
                } else {
                    for (char c : token.toCharArray()) {
                        lineWidth += fontRenderer.getCharWidth(c == '\u00A0' ? ' ' : c);
                    }
                }
            }

            /* --- Position de départ selon l’alignement --- */
            int cursorXStart;
            switch (te.getAlign()) {
                case CENTER: cursorXStart = -lineWidth / 2; break;
                case RIGHT:  cursorXStart = -lineWidth;     break;
                default:     cursorXStart = 0;              break;
            }

            // Désactivation des textures pour dessiner un quad couleur
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableBlend();

            // Conversion couleur int → float
            float r = ((highlight >> 16) & 0xFF) / 255F;
            float g = ((highlight >> 8) & 0xFF) / 255F;
            float b = (highlight & 0xFF) / 255F;

            int top = i * 10 - 20 - highlightHeight / 2 + iconSize / 2;
            int bottom = top + highlightHeight;

            // Dessin du rectangle de surlignage
            Tessellator tess = Tessellator.getInstance();
            VertexBuffer buffer = tess.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            buffer.pos(cursorXStart, bottom, 0).color(r, g, b, 1).endVertex();
            buffer.pos(cursorXStart + lineWidth, bottom, 0).color(r, g, b, 1).endVertex();
            buffer.pos(cursorXStart + lineWidth, top, 0).color(r, g, b, 1).endVertex();
            buffer.pos(cursorXStart, top, 0).color(r, g, b, 1).endVertex();
            tess.draw();
        }

        /* ========================================================= */
        /* === PASSE 2 : DESSIN DU TEXTE ET DES ICÔNES ============ */
        /* ========================================================= */

        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();

        for (int i = 0; i < 4; i++) {

            FontRenderer fontRenderer =
                    CustomFontRenderer.getFont(te.getFontName()[i], te.getFontSize()[i]);

            String line = te.getLines()[i];
            int color = te.getTextColor()[i];
            int iconSize = fontRenderer.FONT_HEIGHT;

            List<String> tokens = SignIcons.parseLine(line);

            int cursorX = 0;

            // Dessin caractère par caractère / icône par icône
            for (String token : tokens) {

                if (SignIcons.isIcon(token)) {
                    SignIcons.IconData data = SignIcons.getIconData(token);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(data.texture);

                    // Icône colorée ou non
                    if (data.ColorBend) {
                        float r = ((color >> 16) & 0xFF) / 255F;
                        float g = ((color >> 8) & 0xFF) / 255F;
                        float b = (color & 0xFF) / 255F;
                        GlStateManager.color(r, g, b, 1);
                    } else {
                        GlStateManager.color(1, 1, 1, 1);
                    }

                    drawModalRectWithCustomSizedTexture(
                            cursorX, i * 10 - 20,
                            0, 0,
                            iconSize, iconSize,
                            iconSize, iconSize
                    );

                    cursorX += iconSize + 2;
                } else {
                    for (char c : token.toCharArray()) {
                        fontRenderer.drawString(
                                String.valueOf(c),
                                cursorX,
                                i * 10 - 20,
                                color
                        );
                        cursorX += tools.FIXED_SPACE_WIDTH;
                    }
                }
            }
        }

        // Restauration de l’état OpenGL
        GlStateManager.popMatrix();
    }
}
