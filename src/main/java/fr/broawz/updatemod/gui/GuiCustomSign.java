package fr.broawz.updatemod.gui;

import fr.broawz.updatemod.UpdateMod;
import fr.broawz.updatemod.blocks.AbstractTileEntitySign;
import fr.broawz.updatemod.client.render.SignIcons;
import fr.broawz.updatemod.network.PacketUpdateSign;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import org.lwjgl.input.Keyboard;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Map;

/**
 * GUI personnalisée pour l’édition des panneaux (signs)
 * - Texte sur plusieurs lignes
 * - Alignement
 * - Largeur du texte (text span)
 * - Insertion d’icônes via une liste scrollable
 */
public class GuiCustomSign extends GuiScreen implements GuiPageButtonList.GuiResponder {

    /** TileEntity du panneau édité */
    private final AbstractTileEntitySign sign;

    /** Champs de texte (une par ligne du panneau) */
    private GuiTextField[] textFields;

    /** Boutons d’alignement */
    private GuiButton alignLeft, alignCenter, alignRight;

    /** Slider pour la largeur du texte (span) */
    private GuiSlider textSpanSlider;

    /** Index du champ de texte actuellement sélectionné */
    private int focusedTextField = 0;

    /** Texte temporaire édité dans le GUI */
    private String[] guiLines;

    /** Valeur temporaire du text span */
    private int guiTextSpan;

    /** Liste scrollable des icônes */
    private IconList iconList;

    /**
     * Constructeur
     * @param sign TileEntity du panneau
     */
    public GuiCustomSign(AbstractTileEntitySign sign) {
        this.sign = sign;
        this.guiLines = sign.getLines().clone(); // copie pour édition locale
        this.guiTextSpan = sign.getTextSpan();
    }

    /**
     * Initialisation de tous les composants graphiques
     */
    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        int linesCount = guiLines.length;
        int leftWidth = (int) (this.width * 0.75); // zone texte à gauche

        // Positionnement général
        int titleY = 10;
        int propY = titleY + 20;
        int spacing = 10;
        int leftMargin = 20;
        int currentX = leftMargin;
        int sliderWidth = Math.min(150, leftWidth - 40);

        /**
         * Slider de largeur du texte (text span)
         * Détermine le nombre max de caractères par ligne
         */
        textSpanSlider = new GuiSlider(this, 0, currentX, propY, "",
                1, 5, guiTextSpan,
                new GuiSlider.FormatHelper() {
                    @Override
                    @MethodsReturnNonnullByDefault
                    @ParametersAreNonnullByDefault
                    public String getText(int id, String name, float value) {
                        return Math.round(value) + " blocks";
                    }
                }) {

            /**
             * Lors du déplacement du slider :
             * - recalcul du text span
             * - limitation du nombre de caractères
             */
            @Override
            @ParametersAreNonnullByDefault
            protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
                super.mouseDragged(mc, mouseX, mouseY);

                int intValue = Math.round(this.getSliderValue() * 4) + 1;
                guiTextSpan = (intValue - 1) / 4;
                this.setSliderValue((intValue - 1) / 4.0f, true);

                int maxChars = guiTextSpan * 10;

                // Mise à jour de tous les champs de texte
                for (int i = 0; i < textFields.length; i++) {
                    textFields[i].setMaxStringLength(maxChars);
                    String text = textFields[i].getText();
                    if (text.length() > maxChars) {
                        text = text.substring(0, maxChars);
                        textFields[i].setText(text);
                    }
                    guiLines[i] = textFields[i].getText();
                }
            }
        };

        this.buttonList.add(textSpanSlider);
        currentX += sliderWidth + spacing;

        /**
         * Boutons d’alignement
         */
        int btnWidth = 30;
        alignLeft = new GuiButton(1, currentX, propY, btnWidth, 20, "|=");
        currentX += btnWidth + spacing;
        alignCenter = new GuiButton(2, currentX, propY, btnWidth, 20, "=|=");
        currentX += btnWidth + spacing;
        alignRight = new GuiButton(3, currentX, propY, btnWidth, 20, "=|");

        this.buttonList.add(alignLeft);
        this.buttonList.add(alignCenter);
        this.buttonList.add(alignRight);

        updateAlignButtons();

        /**
         * Champs de texte (une ligne par champ)
         */
        int startY = propY + 40;
        textFields = new GuiTextField[linesCount];

        for (int i = 0; i < linesCount; i++) {
            textFields[i] = new GuiTextField(
                    i,
                    this.fontRendererObj,
                    20,
                    startY + i * 25,
                    leftWidth - 40,
                    20
            );
            textFields[i].setText(guiLines[i]);
        }

        // Focus initial sur la première ligne
        if (textFields.length > 0) {
            textFields[0].setFocused(true);
            focusedTextField = 0;
        }

        /**
         * Création de la liste scrollable des icônes (à droite)
         */
        int listWidth = 80;
        int listLeft = this.width - listWidth - 20;
        int listRight = this.width;

        iconList = new IconList(
                this.mc,
                listRight - listLeft,
                this.height,
                40,
                this.height - 40,
                24
        );
        iconList.left = listLeft;
        iconList.right = listRight;
    }

    /**
     * Active/désactive les boutons d’alignement
     * selon l’alignement actuel du panneau
     */
    private void updateAlignButtons() {
        alignLeft.enabled = sign.getAlign() != AbstractTileEntitySign.Align.LEFT;
        alignCenter.enabled = sign.getAlign() != AbstractTileEntitySign.Align.CENTER;
        alignRight.enabled = sign.getAlign() != AbstractTileEntitySign.Align.RIGHT;
    }

    /**
     * Gestion des clics sur les boutons
     */
    @Override
    @ParametersAreNonnullByDefault
    protected void actionPerformed(GuiButton button) {
        if (button == alignLeft) {
            sign.setAlign(AbstractTileEntitySign.Align.LEFT);
        } else if (button == alignCenter) {
            sign.setAlign(AbstractTileEntitySign.Align.CENTER);
        } else if (button == alignRight) {
            sign.setAlign(AbstractTileEntitySign.Align.RIGHT);
        }
        updateAlignButtons();
    }

    /**
     * Gestion du clavier :
     * - saisie texte
     * - navigation entre lignes
     */
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (textFields.length > 0) {
            GuiTextField current = textFields[focusedTextField];
            String before = current.getText();

            if (current.textboxKeyTyped(typedChar, keyCode)) {
                String after = current.getText();
                int maxChars = guiTextSpan * 10;

                if (after.length() > maxChars) {
                    current.setText(before);
                } else {
                    guiLines[focusedTextField] = after;
                }
            }

            // Navigation clavier
            if (keyCode == Keyboard.KEY_TAB) {
                current.setFocused(false);
                focusedTextField =
                        (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
                                ? (focusedTextField - 1 + textFields.length) % textFields.length
                                : (focusedTextField + 1) % textFields.length;
                textFields[focusedTextField].setFocused(true);

            } else if (keyCode == Keyboard.KEY_DOWN) {
                current.setFocused(false);
                focusedTextField = (focusedTextField + 1) % textFields.length;
                textFields[focusedTextField].setFocused(true);

            } else if (keyCode == Keyboard.KEY_UP) {
                current.setFocused(false);
                focusedTextField = (focusedTextField - 1 + textFields.length) % textFields.length;
                textFields[focusedTextField].setFocused(true);
            }
        }

        // Fermer le GUI
        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_RETURN) closeGui();
    }

    /**
     * Gestion des clics souris
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (int i = 0; i < textFields.length; i++) {
            textFields[i].mouseClicked(mouseX, mouseY, mouseButton);
            if (textFields[i].isFocused()) focusedTextField = i;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Gestion du scroll souris (liste d’icônes)
     */
    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        iconList.handleMouseInput();
    }

    /**
     * Rendu du GUI
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        fontRendererObj.drawString("Panel: " + sign.getDisplayName(), 10, 10, 0xFFFFFF);

        for (GuiTextField tf : textFields) tf.drawTextBox();

        iconList.drawScreen(mouseX, mouseY, partialTicks);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        for (GuiTextField tf : textFields) tf.updateCursorCounter();
    }

    /**
     * Fermeture du GUI → application des changements
     */
    @Override
    public void onGuiClosed() {
        applyChanges();
        Keyboard.enableRepeatEvents(false);
    }

    /**
     * Envoie les modifications au serveur
     */
    private void applyChanges() {
        for (int i = 0; i < guiLines.length; i++) {
            sign.setLine(i, guiLines[i]);
        }
        sign.setTextSpan(guiTextSpan);

        PacketUpdateSign packet = new PacketUpdateSign(
                sign.getPos(),
                guiLines,
                sign.getVariant(),
                guiTextSpan,
                getAlignFromButtons()
        );
        UpdateMod.NETWORK.sendToServer(packet);
    }

    /**
     * Détermine l’alignement à partir des boutons
     */
    private int getAlignFromButtons() {
        if (alignRight.enabled && alignCenter.enabled) return 0;
        if (alignRight.enabled && alignLeft.enabled) return 1;
        if (alignLeft.enabled && alignCenter.enabled) return 2;
        return 1;
    }

    /** Ferme le GUI */
    private void closeGui() {
        mc.displayGuiScreen(null);
    }

    @Override public void setEntryValue(int id, boolean value) {}
    @Override public void setEntryValue(int id, float value) {}
    @Override @ParametersAreNonnullByDefault public void setEntryValue(int id, String value) {}

    // =========================
    // Classe interne : IconList
    // =========================

    /**
     * Liste scrollable des icônes disponibles
     * Cliquer sur une icône l’insère dans la ligne active
     */
    class IconList extends GuiSlot {

        private final Minecraft mc;
        private final java.util.List<Map.Entry<String, SignIcons.IconData>> entries;

        public IconList(Minecraft mc, int screenWidth, int screenHeight, int top, int bottom, int slotHeight) {
            super(mc, screenWidth, screenHeight, top, bottom, slotHeight);
            this.mc = mc;
            this.entries = new java.util.ArrayList<>(SignIcons.getAllIconTokens().entrySet());
            this.setShowSelectionBox(false);

            int listWidth = 80;
            this.left = screenWidth - listWidth - 20;
            this.right = screenWidth - 20;
        }

        @Override
        protected int getSize() {
            return entries.size();
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {
            if (index >= 0 && index < entries.size() && textFields.length > 0) {
                String token = entries.get(index).getKey();
                textFields[focusedTextField].writeText(token);
            }
        }

        @Override protected boolean isSelected(int index) { return false; }

        @Override
        protected int getScrollBarX() {
            return this.right - 6;
        }

        @Override
        protected void drawBackground() {
            GuiCustomSign.this.drawGradientRect(
                    this.left, this.top, this.right, this.bottom,
                    0xC0101010, 0xD0101010
            );
        }

        @Override
        protected void drawSlot(int entryID, int insideLeft, int yPos, int insideSlotHeight, int mouseXIn, int mouseYIn) {
            if (entryID < 0 || entryID >= entries.size()) return;

            Map.Entry<String, SignIcons.IconData> entry = entries.get(entryID);

            mc.getTextureManager().bindTexture(entry.getValue().texture);

            int iconX = this.left + 5;
            int textX = iconX + 25;

            drawModalRectWithCustomSizedTexture(iconX, yPos, 0, 0, 20, 20, 20, 20);
            GuiCustomSign.this.fontRendererObj.drawString(entry.getKey(), textX, yPos + 6, 0xFFFFFF);
        }
    }
}
