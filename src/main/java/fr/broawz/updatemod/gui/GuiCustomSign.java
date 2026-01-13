package fr.broawz.updatemod.gui;

import fr.broawz.updatemod.UpdateMod;
import fr.broawz.updatemod.blocks.AbstractTileEntitySign;
import fr.broawz.updatemod.client.render.SignIcons;
import fr.broawz.updatemod.network.PacketUpdateSign;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GUI personnalisée pour l'édition des panneaux
 * Version 1.1 : Ajout de catégories, recherche et tooltips
 */
public class GuiCustomSign extends GuiScreen implements GuiPageButtonList.GuiResponder {

    private final AbstractTileEntitySign sign;
    private GuiTextField[] textFields;
    private GuiButton alignLeft, alignCenter, alignRight;
    private int focusedTextField = 0;
    private final String[] guiLines;
    private int guiTextSpan;
    private IconList iconList;

    // ✅ NOUVEAU : Recherche et catégories
    private GuiTextField searchField;
    private String currentCategory = "all";
    private GuiButton categoryAllBtn, categoryArrowBtn, categoryRoadBtn, categoryInfraBtn, categoryAireBtn;
    private int activeLine = 0;


    public GuiCustomSign(AbstractTileEntitySign sign) {
        this.sign = sign;
        this.guiLines = sign.getLines().clone();
        this.guiTextSpan = sign.getTextSpan();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        int linesCount = guiLines.length;
        int leftWidth = (int) (this.width * 0.70); // Réduction pour faire place aux catégories

        int titleY = 10;
        int propY = titleY + 20;
        int spacing = 10;
        int leftMargin = 20;
        int currentX = leftMargin;
        int sliderWidth = Math.min(150, leftWidth - 40);

        // Slider de largeur du texte
        GuiSlider textSpanSlider = new GuiSlider(this, 0, currentX, propY, "",
                1, 5, guiTextSpan,
                new GuiSlider.FormatHelper() {
                    @Override
                    @MethodsReturnNonnullByDefault
                    @ParametersAreNonnullByDefault
                    public String getText(int id, String name, float value) {
                        return Math.round(value) + " blocks";
                    }
                }) {

            @Override
            @ParametersAreNonnullByDefault
            protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
                super.mouseDragged(mc, mouseX, mouseY);

                int intValue = Math.round(this.getSliderValue() * 4) + 1;
                guiTextSpan = (intValue - 1) / 4;
                this.setSliderValue((intValue - 1) / 4.0f, true);

                int maxChars = guiTextSpan * 10;

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

        // Boutons d'alignement
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

        // Champs de texte
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

        if (textFields.length > 0) {
            textFields[0].setFocused(true);
            focusedTextField = 0;
        }

        // ✅ NOUVEAU : Champ de recherche (en haut à droite)
        int searchY = 10;

        searchField = new GuiTextField(
                100,
                this.fontRendererObj,
                this.width - 150,
                searchY,
                130,
                18
        );

        searchField.setMaxStringLength(50);
        searchField.setText("");

        // ✅ NOUVEAU : Boutons de catégories
        int catBtnWidth  = 32;
        int catBtnHeight = 20;
        int catSpacing   = 4;
        int columns      = 4;

        int catStartX = this.width - (catBtnWidth * columns + catSpacing * (columns - 1)) - 10;
        int catStartY = searchY + 25;


        GuiButton[] categoryButtons = new GuiButton[] {
                categoryAllBtn   = new GuiButton(10, 0, 0, catBtnWidth, catBtnHeight, "All"),
                categoryArrowBtn = new GuiButton(11, 0, 0, catBtnWidth, catBtnHeight, ""),
                categoryRoadBtn  = new GuiButton(12, 0, 0, catBtnWidth, catBtnHeight, ""),
                categoryInfraBtn = new GuiButton(13, 0, 0, catBtnWidth, catBtnHeight, ""),
                categoryAireBtn  = new GuiButton(14, 0, 0, catBtnWidth, catBtnHeight, "")
        };

        for (int i = 0; i < categoryButtons.length; i++) {
            int col = i % columns;
            int row = i / columns;

            categoryButtons[i].xPosition = catStartX + col * (catBtnWidth + catSpacing);
            categoryButtons[i].yPosition = catStartY + row * (catBtnHeight + catSpacing);

            this.buttonList.add(categoryButtons[i]);
        }

        updateCategoryButtons();

        // Liste scrollable des icônes
        int listWidth = 160;
        int listRight = this.width;
        int listLeftFinal = listRight - listWidth;
        int rows = (int) Math.ceil(categoryButtons.length / (float) columns);
        int mosaicHeight = rows * (catBtnHeight + catSpacing);

        int listTop = catStartY + mosaicHeight + 10;

        iconList = new IconList(
                this.mc,
                listWidth,
                this.height,
                listTop,
                this.height - 40,
                24
        );
        iconList.left = listLeftFinal;
        iconList.right = listRight;

    }

    private void updateAlignButtons() {
        alignLeft.enabled = sign.getAlign() != AbstractTileEntitySign.Align.LEFT;
        alignCenter.enabled = sign.getAlign() != AbstractTileEntitySign.Align.CENTER;
        alignRight.enabled = sign.getAlign() != AbstractTileEntitySign.Align.RIGHT;
    }

    // ✅ NOUVEAU : Mise à jour des boutons de catégories
    private void updateCategoryButtons() {
        categoryAllBtn.enabled = !currentCategory.equals("all");
        categoryArrowBtn.enabled = !currentCategory.equals("arrow");
        categoryRoadBtn.enabled = !currentCategory.equals("road");
        categoryInfraBtn.enabled = !currentCategory.equals("infrastructure");
        categoryAireBtn.enabled = !currentCategory.equals("aire");
    }

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
        // ✅ NOUVEAU : Gestion des catégories
        else if (button == categoryAllBtn) {
            currentCategory = "all";
            iconList.updateFilter();
        } else if (button == categoryArrowBtn) {
            currentCategory = "arrow";
            iconList.updateFilter();
        } else if (button == categoryRoadBtn) {
            currentCategory = "road";
            iconList.updateFilter();
        } else if (button == categoryInfraBtn) {
            currentCategory = "infrastructure";
            iconList.updateFilter();
        } else if (button == categoryAireBtn) {
            currentCategory = "aire";
            iconList.updateFilter();
        }

        updateAlignButtons();
        updateCategoryButtons();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        // ✅ Gestion de la recherche
        if (searchField.isFocused()) {
            searchField.textboxKeyTyped(typedChar, keyCode);
            iconList.updateFilter();
            activeLine = focusedTextField;
            return;
        }

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

        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_RETURN) closeGui();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // ✅ Clic sur le champ de recherche
        searchField.mouseClicked(mouseX, mouseY, mouseButton);

        for (int i = 0; i < textFields.length; i++) {
            textFields[i].mouseClicked(mouseX, mouseY, mouseButton);
            if (textFields[i].isFocused()) {
                focusedTextField = i;
                activeLine = i; // ✅ IMPORTANT
                searchField.setFocused(false);
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        iconList.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        for (GuiTextField tf : textFields) tf.drawTextBox();

        iconList.drawScreen(mouseX, mouseY, partialTicks);


        super.drawScreen(mouseX, mouseY, partialTicks);

// ✅ ICI
        searchField.drawTextBox();

// Icônes des boutons catégories
        iconList.drawCategoryButtonIcon(categoryArrowBtn, "arrow");
        iconList.drawCategoryButtonIcon(categoryRoadBtn, "road");
        iconList.drawCategoryButtonIcon(categoryInfraBtn, "infrastructure");
        iconList.drawCategoryButtonIcon(categoryAireBtn, "aire");

// Tooltip
        iconList.drawTooltip(mouseX, mouseY);

    }

    @Override
    public void updateScreen() {
        for (GuiTextField tf : textFields) tf.updateCursorCounter();
        searchField.updateCursorCounter();
    }


    @Override
    public void onGuiClosed() {
        applyChanges();
        Keyboard.enableRepeatEvents(false);
    }

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

    private int getAlignFromButtons() {
        if (alignRight.enabled && alignCenter.enabled) return 0;
        if (alignRight.enabled && alignLeft.enabled) return 1;
        if (alignLeft.enabled && alignCenter.enabled) return 2;
        return 1;
    }

    private void closeGui() {
        mc.displayGuiScreen(null);
    }

    @Override public void setEntryValue(int id, boolean value) {}
    @Override public void setEntryValue(int id, float value) {}
    @Override @ParametersAreNonnullByDefault public void setEntryValue(int id, String value) {}

    // =========================
    // Classe interne : IconList
    // =========================

    class IconList extends GuiSlot {

        private final Minecraft mc;
        private List<Map.Entry<String, SignIcons.IconData>> allEntries;
        private List<Map.Entry<String, SignIcons.IconData>> filteredEntries;
        private int hoveredIndex = -1;

        public IconList(Minecraft mc, int screenWidth, int screenHeight, int top, int bottom, int slotHeight) {
            super(mc, screenWidth, screenHeight, top, bottom, slotHeight);
            this.mc = mc;
            this.allEntries = new ArrayList<>(SignIcons.getAllIconTokens().entrySet());
            this.filteredEntries = new ArrayList<>(allEntries);
            this.setShowSelectionBox(false);
        }

        // ✅ NOUVEAU : Mise à jour du filtre
        public void updateFilter() {
            String search = searchField.getText().toLowerCase();

            filteredEntries = allEntries.stream()
                    .filter(entry -> {
                        SignIcons.IconData data = entry.getValue();

                        // Filtre par catégorie
                        if (!currentCategory.equals("all") && !data.category.equals(currentCategory)) {
                            return false;
                        }

                        // Filtre par recherche
                        if (!search.isEmpty()) {
                            String token = entry.getKey().toLowerCase();
                            String name = data.name.toLowerCase();
                            return token.contains(search) || name.contains(search);
                        }

                        return true;
                    })
                    .collect(Collectors.toList());
        }

        @Override
        protected int getSize() {
            return filteredEntries.size();
        }

        @Override
        protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {}


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
            if (entryID < 0 || entryID >= filteredEntries.size()) return;

            Map.Entry<String, SignIcons.IconData> entry = filteredEntries.get(entryID);

            mc.getTextureManager().bindTexture(entry.getValue().texture);

            int iconX = this.left + 4;
            int textX = iconX + 22;

            // ✅ Highlight si survol
            if (mouseXIn >= this.left && mouseXIn <= this.right &&
                    mouseYIn >= yPos && mouseYIn < yPos + insideSlotHeight) {
                hoveredIndex = entryID;
                drawRect(this.left, yPos, this.right, yPos + insideSlotHeight, 0x80FFFFFF);
            }

            drawModalRectWithCustomSizedTexture(iconX, yPos, 0, 0, 20, 20, 20, 20);
            GuiCustomSign.this.fontRendererObj.drawString(entry.getKey(), textX, yPos + 6, 0xFFFFFF);
        }

        // ✅ NOUVEAU : Affichage du tooltip
        public void drawTooltip(int mouseX, int mouseY) {
            if (hoveredIndex >= 0 && hoveredIndex < filteredEntries.size()) {
                if (mouseX >= this.left && mouseX <= this.right &&
                        mouseY >= this.top && mouseY <= this.bottom) {

                    SignIcons.IconData data = filteredEntries.get(hoveredIndex).getValue();

                    List<String> tooltip = new ArrayList<>();
                    tooltip.add("§f" + data.name);
                    tooltip.add("§7Category: " + data.category);
                    if (data.ColorBend) {
                        tooltip.add("§eTintable");
                    }
                    GuiCustomSign.this.drawHoveringText(tooltip, mouseX, mouseY);
                }
            }
            hoveredIndex = -1;
        }

        private void drawCategoryButtonIcon(GuiButton button, String category) {
            SignIcons.IconData icon = SignIcons.getCategoryIcon(category);
            if (icon == null) return;

            GL11.glColor4f(1f, 1f, 1f, 1f);
            mc.getTextureManager().bindTexture(icon.texture);

            int size = 14;
            int x = button.xPosition + (button.width - size) / 2;
            int y = button.yPosition + (button.height - size) / 2;

            drawModalRectWithCustomSizedTexture(x, y, 0, 0, size, size, size, size);
        }
    }
}