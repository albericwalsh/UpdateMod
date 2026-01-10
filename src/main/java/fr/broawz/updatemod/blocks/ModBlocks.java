package fr.broawz.updatemod.blocks;

/*
 * Imports internes
 */
import fr.broawz.updatemod.utils.References;

/*
 * Imports Minecraft
 */
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

/*
 * Imports Forge
 */
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/*
 * Classe de base pour TOUS les blocs du mod
 * ----------------------------------------
 * Philosophie :
 *  - Chaque bloc s’auto-enregistre
 *  - Un seul constructeur = bloc fonctionnel
 *  - Centralisation des renders via une liste statique
 */
public class ModBlocks extends Block {

    /*
     * Nom interne (utilisé pour la traduction)
     * Exemple : "my_block"
     */
    private final String unlocalizedName;

    /*
     * Nom enregistré dans le registre Forge
     * Exemple : "my_block"
     */
    private final String registeredName;

    /*
     * Liste statique de TOUS les blocs créés via cette classe
     * → Utilisée pour enregistrer les renders côté client
     */
    public static final List<Block> BLOCKS = new ArrayList<>();

    /*
     * Constructeur principal
     * ----------------------
     * Crée un bloc prêt à l’emploi :
     *  - Définit le matériau
     *  - Initialise les noms
     *  - Enregistre le bloc
     *  - Enregistre l’ItemBlock associé
     */
    public ModBlocks(String unlocalizedName, String registeredName) {
        super(Material.ROCK); // matériau par défaut
        this.unlocalizedName = unlocalizedName;
        this.registeredName = registeredName;

        initBlock();          // setup des noms
        registerBlock(this);  // enregistrement Forge
        registerItemBlock();  // enregistrement ItemBlock

        // Le rendu est volontairement déporté côté client
        // registerRender();
    }

    /*
     * Initialisation des propriétés de base du bloc
     */
    private void initBlock() {
        // Nom unique Forge : modid:block_name
        this.setRegistryName(
                new ResourceLocation(References.MODID, this.registeredName)
        );

        // Nom utilisé pour les fichiers de langue
        this.setUnlocalizedName(this.unlocalizedName);
    }

    /*
     * Enregistrement du bloc dans Forge
     * + ajout dans la liste statique BLOCKS
     */
    private void registerBlock(Block block) {
        GameRegistry.register(block);
        BLOCKS.add(block); // mémorise le bloc pour les renders
    }

    /*
     * Création et enregistrement de l’ItemBlock associé
     * → permet d’avoir le bloc dans l’inventaire
     */
    private void registerItemBlock() {
        ItemBlock itemBlock = new ItemBlock(this);
        itemBlock.setRegistryName(getRegistryName());
        GameRegistry.register(itemBlock);
    }

    /*
     * Ancienne version : rendu par bloc
     * ---------------------------------
     * Fonctionnelle mais moins scalable
     */
//    @SideOnly(Side.CLIENT)
//    private void registerRender() {
//        Item item = Item.getItemFromBlock(this);
//        assert item != null;
//        ModelLoader.setCustomModelResourceLocation(
//                item,
//                0,
//                new ModelResourceLocation(getRegistryName(), "inventory")
//        );
//    }

    /*
     * Enregistrement GLOBAL des renders
     * ---------------------------------
     * Appelé UNE SEULE FOIS côté client
     * (généralement dans ClientProxy)
     */
    @SideOnly(Side.CLIENT)
    public static void registerRenders() {

        // Parcourt tous les blocs créés
        for (Block block : BLOCKS) {

            Item item = Item.getItemFromBlock(block);
            assert item != null;

            // Association Item ↔ modèle JSON
            ModelLoader.setCustomModelResourceLocation(
                    item,
                    0,
                    new ModelResourceLocation(
                            block.getRegistryName(),
                            "inventory"
                    )
            );
        }
    }
}
