package fr.broawz.updatemod.items;

import fr.broawz.updatemod.gui.GuiDebugStick;
import fr.broawz.updatemod.utils.References;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.Objects;

public class ItemDebugStick extends ModItems {

    public ItemDebugStick() {
        super("debug_stick", "debug_stick", true);
        setCreativeTab(References.UPDATED_MOD); // Mettre dans ton CreativeTab
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = Objects.requireNonNull(playerIn.getHeldItem(handIn));

        if (!worldIn.isRemote) {
            // côté serveur : juste PASS
            return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
        }

        // côté client : raytrace + GUI
        if (playerIn != null) {
            BlockPos pos = Objects.requireNonNull(playerIn.rayTrace(5.0D, 1.0F)).getBlockPos();
            IBlockState state = worldIn.getBlockState(pos);

            Minecraft.getMinecraft().displayGuiScreen(new GuiDebugStick(pos, state));
            playerIn.addChatMessage(new TextComponentString("DebugStick activated"));
        }

        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }

}
