package fr.broawz.updatemod.gui;

import fr.broawz.updatemod.UpdateMod;
import fr.broawz.updatemod.blocks.AbstractTileEntitySign;
import fr.broawz.updatemod.network.PacketUpdateSign;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiCustomSign extends GuiScreen {

    private final AbstractTileEntitySign te;
    private GuiTextField[] textFields = new GuiTextField[4];

    public GuiCustomSign(AbstractTileEntitySign te) {
        this.te = te;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        int startY = this.height / 2 - 50;

        for (int i = 0; i < 4; i++) {
            // Constructeur correct pour 1.10.2 : id, fontRenderer, x, y, width, height
            textFields[i] = new GuiTextField(i, this.fontRendererObj, 0, 0, 200, 20);
            textFields[i].setText(te.getLines()[i]);

            // Positionnement centré
            int x = (this.width - 200) / 2; // largeur du champ = 200
            int y = startY + i * 25;
            textFields[i].xPosition = x;
            textFields[i].yPosition = y;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (GuiTextField tf : textFields) {
            tf.textboxKeyTyped(typedChar, keyCode);
        }

        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_RETURN) {
            closeGui();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (GuiTextField tf : textFields) {
            tf.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        for (GuiTextField tf : textFields) {
            tf.drawTextBox();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() {
        for (GuiTextField tf : textFields) {
            tf.updateCursorCounter();
        }
    }

    private void closeGui() {
        for (int i = 0; i < 4; i++) {
            te.setLine(i, textFields[i].getText());
        }
        Keyboard.enableRepeatEvents(false);
        this.mc.displayGuiScreen(null);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();

        String[] lines = new String[4];
        for (int i = 0; i < 4; i++) {
            lines[i] = textFields[i].getText();
        }

        UpdateMod.NETWORK.sendToServer(new PacketUpdateSign(te.getPos(), lines, te.getVariant()));
        UpdateMod.NETWORK.sendToServer(new PacketUpdateSign(te.getPos(), lines, te.getVariant()));
    }
}
