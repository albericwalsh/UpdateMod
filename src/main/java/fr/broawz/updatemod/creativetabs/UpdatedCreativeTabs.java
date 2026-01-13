package fr.broawz.updatemod.creativetabs;

import fr.broawz.updatemod.blocks.ModBlocksInit;
import fr.broawz.updatemod.items.ItemTabIcon;
import fr.broawz.updatemod.items.ModItems;
import fr.broawz.updatemod.items.ModItemsInit;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * CreativeTab personnalisée du mod UpdateMod
 *
 * 👉 Cette classe définit :
 *  - l’onglet personnalisé dans l’inventaire créatif
 *  - l’icône affichée pour l’onglet
 *  - le nom affiché côté client
 *
 * Tous les items/blocs qui appellent :
 *   setCreativeTab(References.UPDATED_MOD)
 * apparaîtront ici.
 *
 * ⚠️ Classe CLIENT-ONLY (interface graphique)
 */
public class UpdatedCreativeTabs extends CreativeTabs {

    /**
     * Constructeur du CreativeTab
     *
     * @param label identifiant interne de l’onglet
     *              (utilisé par Minecraft, pas forcément visible)
     */
    public UpdatedCreativeTabs(String label) {
        super(label);
    }

    /**
     * Icône affichée pour l’onglet dans l’inventaire créatif
     *
     * ⚠️ Méthode appelée UNIQUEMENT côté client
     * ⚠️ Doit retourner un Item (pas un ItemStack)
     *
     * Astuce :
     *  - tu peux mettre un item du mod ici plus tard
     *  - ex : ModItems.DEBUG_STICK
     */
    @Override
    @MethodsReturnNonnullByDefault
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return ModItemsInit.TAB_ICON;
    }
}
