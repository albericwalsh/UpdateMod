package fr.broawz.updatemod.items;

/*
 * Imports Minecraft
 */
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

/*
 * ItemBlock personnalisé pour les panneaux
 * ----------------------------------------
 * Rôle :
 *  - Gérer les variantes via la metadata
 *  - Fournir un nom différent par variante
 *  - Afficher toutes les variantes dans l’inventaire créatif
 */
public class ItemBlockSign extends ItemBlock {

    /*
     * Liste des variantes
     * -------------------
     * L’index = metadata
     * La valeur = suffixe du nom
     */
    private final String[] variants;

    /*
     * Constructeur
     * ------------
     * Associe le bloc et ses variantes
     */
    public ItemBlockSign(Block block, String[] variants) {
        super(block);
        this.variants = variants;

        // Indique que l’item utilise la metadata
        setHasSubtypes(true);

        // Désactive le système de durabilité
        setMaxDamage(0);
    }

    /*
     * Nom non localisé dynamique
     * --------------------------
     * Exemples :
     *  - tile.basic_sign.basic
     *  - tile.basic_sign.red
     */
    @Override
    public String getUnlocalizedName(ItemStack stack) {

        int meta = stack.getMetadata();

        // Sécurité : fallback sur 0 si meta invalide
        if (meta < 0 || meta >= variants.length) {
            meta = 0;
        }

        // Ajout du suffixe de variante
        return super.getUnlocalizedName() + "." + variants[meta];
    }

    /*
     * Ajout de TOUTES les variantes
     * dans l’onglet créatif
     */
    @Override
    public void getSubItems(
            Item itemIn,
            CreativeTabs tab,
            List<ItemStack> items
    ) {
        for (int i = 0; i < variants.length; i++) {
            items.add(new ItemStack(itemIn, 1, i));
        }
    }
}
