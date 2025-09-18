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

    // Stockage temporaire du texte et du slider pendant le GUI
    private String[] guiLines;
    private int guiTextSpan;

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

        // --- Partie propriétés sous le titre ---
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
                super.mouseDragged(mc, mouseX, mouseY); // garder l'affichage du slider

                // Convertir la valeur flottante en entier 1..5
                int intValue = Math.round(this.getSliderValue() * 4) + 1;
                guiTextSpan = (intValue-1)/4;
                // Mettre à jour le TileEntity directement (pour voir le changement en temps réel dans le monde)

                // Repositionner le slider exactement sur ce cran
                this.setSliderValue((intValue - 1) / 4.0f, true);

                // Limiter le texte dans les textFields
                int maxChars = guiTextSpan * 10; // nombre max de caractères par bloc
                System.out.println("\u001B[32m[INFO] - Max chars: " + maxChars +"\u001B[0m");
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

        // --- Boutons d'alignement ---
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

        // --- Lignes de texte ---
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
            System.out.println("\u001B[32m[INFO] Align set to LEFT\u001B[0m");
        }
        else if (button == alignCenter) {
            sign.setAlign(AbstractTileEntitySign.Align.CENTER);
            System.out.println("\u001B[32m[INFO] Align set to CENTER\u001B[0m");
        }
        else if (button == alignRight) {
            sign.setAlign(AbstractTileEntitySign.Align.RIGHT);
            System.out.println("\u001B[32m[INFO] Align set to RIGHT\u001B[0m");
        } else {
            System.out.println("\u001B[33m[WARN] Unknown button ID: " + button.id + "\u001B[0m");
            return; // bouton non géré
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

                // nombre max de caractères = nombre de blocs * 10
                int maxChars = guiTextSpan * 10; // nombre max de caractères par bloc
                if (after.length() > maxChars) {
                    current.setText(before);
                } else {
                    guiLines[focusedTextField] = after;
                }

            }


            // Navigation TAB / flèches
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
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        fontRendererObj.drawString("Panel: " + sign.getDisplayName(), 10, 10, 0xFFFFFF);

        // Dessiner les champs texte
        for (GuiTextField tf : textFields) tf.drawTextBox();

        // --- Panel icônes à droite ---
        int panelX = (int) (this.width * 0.75) + 10;
        int panelY = 40;
        int iconSize = 20;
        int spacing = 5;
        int yOffset = 0;

        for (Map.Entry<String, SignIcons.IconData> entry : SignIcons.getAllIconTokens().entrySet()) {
            String token = entry.getKey();
            SignIcons.IconData iconData = entry.getValue();

            Minecraft.getMinecraft().getTextureManager().bindTexture(iconData.texture);
            drawModalRectWithCustomSizedTexture(panelX, panelY + yOffset, 0, 0, iconSize, iconSize, iconSize, iconSize);

            fontRendererObj.drawString(token, panelX + iconSize + 5, panelY + yOffset + (iconSize - fontRendererObj.FONT_HEIGHT) / 2, 0xFFFFFF);

            yOffset += iconSize + spacing;
        }

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
        // --- Mettre à jour le TileEntity localement ---
        for (int i = 0; i < guiLines.length; i++) {
            sign.setLine(i, guiLines[i]);
        }
        sign.setTextSpan(guiTextSpan);
        switch (getAlignFromButtons()) {
            case 0:
                sign.setAlign(AbstractTileEntitySign.Align.LEFT);
                break;
            case 1:
                sign.setAlign(AbstractTileEntitySign.Align.CENTER);
                break;
            case 2:
                sign.setAlign(AbstractTileEntitySign.Align.RIGHT);
                break;
        }


        System.out.println("--------------------------------------------------");
        System.out.println("Sign updated: ");
        for (String line : guiLines) {
            System.out.println(" - " + line);
        }
        System.out.println("Text span: " + guiTextSpan);
        System.out.println("Align: " + sign.getAlign());
        System.out.println("---------------------------------------------------");
        // --- Envoyer au serveur ---
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
        return 1; // par défaut
    }



    private void closeGui() {
        mc.displayGuiScreen(null);
        // Exemple quand tu quittes le GUI
    }

    // GuiResponder (non utilisés ici)
    @Override public void setEntryValue(int id, boolean value) {}
    @Override public void setEntryValue(int id, float value) {}
    @Override @ParametersAreNonnullByDefault public void setEntryValue(int id, String value) {}
}
