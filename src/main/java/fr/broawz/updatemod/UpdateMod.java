package fr.broawz.updatemod;

/*
 * Imports des classes internes au mod
 */
import fr.broawz.updatemod.network.PacketUpdateSign;   // Packet réseau personnalisé
import fr.broawz.updatemod.proxy.CommonProxy;          // Proxy commun client / serveur
import fr.broawz.updatemod.utils.GuiHandler;            // Gestionnaire des GUI
import fr.broawz.updatemod.utils.References;            // Constantes du mod (MODID, VERSION, etc.)

/*
 * Imports Forge / Minecraft
 */
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/*
 * Classe principale du mod
 * → Point d’entrée Forge
 * → Chargée au lancement du jeu
 */
@Mod(
        modid = References.MODID,                         // Identifiant unique du mod
        name = References.NAME,                           // Nom affiché
        version = References.VERSION,                     // Version du mod
        acceptableRemoteVersions = References.ACCEPTABLE_REMOTE_VERSION
)
public class UpdateMod {

    /*
     * Proxy Forge
     * → Permet de séparer le code CLIENT et SERVEUR
     * → Évite les crashes côté serveur (classes client-only)
     */
    @SidedProxy(
            clientSide = References.CLIENT_PROXY,         // Proxy client
            serverSide = References.SERVER_PROXY           // Proxy serveur
    )
    public static CommonProxy proxy;

    /*
     * Instance unique du mod
     * → Utilisée par Forge
     * → Sert notamment pour le GuiHandler
     */
    @Mod.Instance(References.MODID)
    public static UpdateMod instance;

    /*
     * Canal réseau du mod
     * → Sert à envoyer des packets client <-> serveur
     */
    public static SimpleNetworkWrapper NETWORK;

    /*
     * PHASE PRE-INIT
     * ----------------
     * → Création des canaux réseau
     * → Enregistrement des packets
     * → Enregistrement des blocs / items
     * → Setup des GUI
     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        // Création du canal réseau du mod
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(References.MODID);

        // Enregistrement du packet PacketUpdateSign
        // 0 = ID du packet
        // Side.SERVER = le packet est reçu côté serveur
        NETWORK.registerMessage(
                PacketUpdateSign.Handler.class,
                PacketUpdateSign.class,
                0,
                Side.SERVER
        );

        // Enregistrement des blocs personnalisés du mod
        ModContentRegistry.registerCustomBlocks();

        // Chargement alternatif depuis JSON (désactivé pour le moment)
        // ModContentRegistry.registerFromJson();

        // Enregistrement du gestionnaire de GUI
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

        // Appel du preInit spécifique au proxy (client ou serveur)
        proxy.preInit();
    }

    /*
     * PHASE INIT
     * ----------
     * → Recettes
     * → Renders
     * → Handlers supplémentaires
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    /*
     * PHASE POST-INIT
     * ---------------
     * → Interactions avec d'autres mods
     * → Ajustements finaux
     * (vide pour l’instant)
     */
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }
}
