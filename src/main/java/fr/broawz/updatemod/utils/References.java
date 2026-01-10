package fr.broawz.updatemod.utils;

import fr.broawz.updatemod.creativetabs.UpdatedCreativeTabs;
import net.minecraft.creativetab.CreativeTabs;

/**
 * Classe de constantes globales pour le mod
 *
 * Contient :
 * - Informations sur le mod (ID, nom, version)
 * - Références aux proxies client/serveur
 * - Onglet créatif personnalisé
 */
public class References {

    // --- Informations de base du mod ---
    public static final String MODID = "updatemod";     // Identifiant unique du mod
    public static final String NAME = "Update Mod";     // Nom affiché dans l’interface
    public static final String VERSION = "1.0.0";       // Version du mod

    // --- Version réseau ---
    public static final String ACCEPTABLE_REMOTE_VERSION = "*"; // Acceptation de toutes les versions côté réseau

    // --- Proxies ---
    public static final String CLIENT_PROXY = "fr.broawz.updatemod.proxy.ClientProxy"; // Proxy côté client
    public static final String SERVER_PROXY = "fr.broawz.updatemod.proxy.ServerProxy"; // Proxy côté serveur

    // --- Onglet créatif personnalisé ---
    // Utilisé pour regrouper tous les items et blocs du mod dans l’interface créative
    public static final CreativeTabs UPDATED_MOD = new UpdatedCreativeTabs("UpdateMod");
}
