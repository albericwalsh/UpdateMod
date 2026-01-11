package fr.broawz.updatemod.items;

import fr.broawz.updatemod.utils.References;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Item de debug / outil développeur
 * --------------------------------
 * Rôle :
 *  - Permet de récupérer des infos sur un bloc visé
 *  - Ouvre une GUI côté client
 *  - Sert d'outil interne (non gameplay)
 *
 * ⚠️ CET ITEM EST CLIENT-ONLY
 */
@SideOnly(Side.CLIENT)
public class ItemDebugStick extends ModItems {

    /**
     * Constructeur
     * Initialise l'item comme un outil unique (non empilable)
     */
    public ItemDebugStick() {
        super(
                "debug_stick",     // unlocalizedName
                "debug_stick",     // registryName
                true               // auto-register
        );

        setCreativeTab(References.UPDATED_MOD);
        setMaxStackSize(1);
    }

    /**
     * Appelé lors du clic droit avec l'item
     * ⚠️ Cette méthode ne s'exécute que côté CLIENT grâce à @SideOnly
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(
            World worldIn,
            EntityPlayer playerIn,
            EnumHand handIn
    ) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        // ✅ Plus besoin de vérifier isRemote, on est forcément côté client
        if (playerIn != null) {

            // RayTrace : récupère le bloc regardé (5 blocs max)
            RayTraceResult rayTrace = playerIn.rayTrace(5.0D, 1.0F);

            if (rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos pos = rayTrace.getBlockPos();
                IBlockState state = worldIn.getBlockState(pos);

                // ✅ Import dynamique pour éviter le chargement côté serveur
                openDebugGui(pos, state);

                // Message de confirmation dans le chat
                playerIn.addChatMessage(
                        new TextComponentString("DebugStick activated")
                );

                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    /**
     * Ouvre la GUI de debug
     * Méthode séparée pour éviter l'import direct de GuiDebugStick
     */
    @SideOnly(Side.CLIENT)
    private void openDebugGui(BlockPos pos, IBlockState state) {
        // Import uniquement au moment de l'appel
        net.minecraft.client.Minecraft.getMinecraft().displayGuiScreen(
                new fr.broawz.updatemod.gui.GuiDebugStick(pos, state)
        );
    }
}