package fr.broawz.updatemod.gui;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiDebugStick extends GuiScreen {

    private final BlockPos pos;
    private final IBlockState originalState;
    private IBlockState editedState;

    public GuiDebugStick(BlockPos pos, IBlockState state) {
        this.pos = pos;
        this.originalState = state;
        this.editedState = state;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        int y = this.height / 4;

        int i = 0;
        List<GuiButton> stateButtons = new ArrayList<>();
        for (IProperty<?> prop : originalState.getProperties().keySet()) {
            String label = prop.getName() + ": " + originalState.getValue(prop).toString();
            GuiButton button = new GuiButton(i, this.width / 2 - 100, y, 200, 20, label);
            stateButtons.add(button);
            this.buttonList.add(button);
            y += 25;
            i++;
        }

        GuiButton doneButton = new GuiButton(1000, this.width / 2 - 100, y + 10, 98, 20, "Valider");
        GuiButton cancelButton = new GuiButton(1001, this.width / 2 + 2, y + 10, 98, 20, "Annuler");
        this.buttonList.add(doneButton);
        this.buttonList.add(cancelButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1000) { // Valider
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/debugstick apply " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
            this.mc.displayGuiScreen(null);
        } else if (button.id == 1001) { // Annuler
            this.mc.displayGuiScreen(null);
        } else {
            // Modifier propriété cyclique
            IProperty<?> prop = (IProperty<?>) originalState.getProperties().keySet().toArray()[button.id];
            Comparable current = editedState.getValue(prop);
            Collection<?> values = prop.getAllowedValues();
            List<?> list = new ArrayList<>(values);
            int index = list.indexOf(current);
            index = (index + 1) % list.size();
            Comparable newVal = (Comparable) list.get(index);

            IProperty property = (IProperty) prop;
            editedState = editedState.withProperty(property, newVal);

            button.displayString = prop.getName() + ": " + newVal.toString();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        drawCenteredString(this.fontRendererObj, "Modifier état du bloc", this.width / 2, 15, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

