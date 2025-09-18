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

public class GuiCustomSign extends GuiScreen implements GuiPageButtonList.GuiResponder {

    private final AbstractTileEntitySign sign;
    private GuiTextField[] textFields;

    private GuiButton alignLeft, alignCenter, alignRight;
    private GuiSlider textSpanSlider;
    private int focusedTextField = 0;

    private String[] guiLines;
    private int guiTextSpan;

    // Nouvelle liste scrollable
    private IconList iconList;

    public GuiCustomSign(AbstractTileEntitySign sign) {
        this.sign = sign;
        this.guiLines = sign.getLines().clone();
        this.guiTextSpan = sign.getTextSpan();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        int linesCount = guiLines.length;
        int leftWidth = (int) (this.width * 0.75);

        int titleY = 10;
        int propY = titleY + 20;
        int spacing = 10;
        int leftMargin = 20;
        int currentX = leftMargin;
        int sliderWidth = Math.min(150, leftWidth - 40);

        textSpanSlider = new GuiSlider(this, 0, currentX, propY, "", 1, 5, guiTextSpan, new GuiSlider.FormatHelper() {
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

        int startY = propY + 40;
        textFields = new GuiTextField[linesCount];
        for (int i = 0; i < linesCount; i++) {
            textFields[i] = new GuiTextField(i, this.fontRendererObj, 20, startY + i * 25, leftWidth - 40, 20);
            textFields[i].setText(guiLines[i]);
        }

        if (textFields.length > 0) {
            textFields[0].setFocused(true);
            focusedTextField = 0;
        }

        // --- Création de la liste scrollable des icônes ---
        int listWidth = 80; // largeur de la liste
        int listLeft = this.width - listWidth -20; // à droite avec une marge de 20px
        int listRight = this.width; // bord droit de l'écran

        iconList = new IconList(this.mc, listRight - listLeft, this.height, 40, this.height - 40, 24);
        iconList.left = listLeft;
        iconList.right = listRight;
    }

    private void updateAlignButtons() {
        alignLeft.enabled = sign.getAlign() != AbstractTileEntitySign.Align.LEFT;
        alignCenter.enabled = sign.getAlign() != AbstractTileEntitySign.Align.CENTER;
        alignRight.enabled = sign.getAlign() != AbstractTileEntitySign.Align.RIGHT;
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
        updateAlignButtons();
    }

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

            if (keyCode == Keyboard.KEY_TAB) {
                current.setFocused(false);
                if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                    focusedTextField = (focusedTextField - 1 + textFields.length) % textFields.length;
                } else {
                    focusedTextField = (focusedTextField + 1) % textFields.length;
                }
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
        for (int i = 0; i < textFields.length; i++) {
            textFields[i].mouseClicked(mouseX, mouseY, mouseButton);
            if (textFields[i].isFocused()) focusedTextField = i;
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
        fontRendererObj.drawString("Panel: " + sign.getDisplayName(), 10, 10, 0xFFFFFF);

        for (GuiTextField tf : textFields) tf.drawTextBox();

        // Liste scrollable des icônes
        iconList.drawScreen(mouseX, mouseY, partialTicks);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        for (GuiTextField tf : textFields) tf.updateCursorCounter();
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
        switch (getAlignFromButtons()) {
            case 0: sign.setAlign(AbstractTileEntitySign.Align.LEFT); break;
            case 1: sign.setAlign(AbstractTileEntitySign.Align.CENTER); break;
            case 2: sign.setAlign(AbstractTileEntitySign.Align.RIGHT); break;
        }

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
        else if (alignLeft.enabled && alignCenter.enabled) return 2;
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
        private final java.util.List<Map.Entry<String, SignIcons.IconData>> entries;

        public IconList(Minecraft mc, int screenWidth, int screenHeight, int top, int bottom, int slotHeight) {
            // Ici, on met juste screenWidth/Height pour initialiser GuiSlot
            super(mc, screenWidth, screenHeight, top, bottom, slotHeight);
            this.mc = mc;
            this.entries = new java.util.ArrayList<>(SignIcons.getAllIconTokens().entrySet());
            this.setShowSelectionBox(false);

            // ⚡ Nouvelle largeur réduite et collée à droite
            int listWidth = 100; // largeur souhaitée (moins d’un quart)
            this.left = screenWidth - listWidth - 20; // 20px de marge à droite
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

        @Override
        protected boolean isSelected(int index) {
            return false;
        }

        @Override
        protected int getScrollBarX() {
            return this.right - 6; // scrollbar collée à droite
        }

        @Override
        protected void drawBackground() {
            GuiCustomSign.this.drawGradientRect(this.left, this.top, this.right, this.bottom,
                    0xC0101010, 0xD0101010); // fond limité à la zone
        }

        @Override
        protected void drawSlot(int entryID, int insideLeft, int yPos, int insideSlotHeight, int mouseXIn, int mouseYIn) {
            if (entryID < 0 || entryID >= entries.size()) return;

            Map.Entry<String, SignIcons.IconData> entry = entries.get(entryID);
            String token = entry.getKey();
            SignIcons.IconData iconData = entry.getValue();

            mc.getTextureManager().bindTexture(iconData.texture);

            int padding = 5; // marge intérieure
            int iconX = this.left + padding; // icône dans la zone
            int textX = iconX + 25;          // texte à droite de l’icône

            drawModalRectWithCustomSizedTexture(iconX, yPos, 0, 0, 20, 20, 20, 20);
            GuiCustomSign.this.fontRendererObj.drawString(token, textX, yPos + 6, 0xFFFFFF);
        }
    }
}
