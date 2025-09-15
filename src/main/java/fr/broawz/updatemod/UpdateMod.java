package fr.broawz.updatemod;

import fr.broawz.updatemod.blocks.ModBlocks;
import fr.broawz.updatemod.blocks.ModBlocksInit;
import fr.broawz.updatemod.network.PacketUpdateSign;
import fr.broawz.updatemod.proxy.CommonProxy;
import fr.broawz.updatemod.utils.GuiHandler;
import fr.broawz.updatemod.utils.References;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = References.MODID, name = References.NAME, version = References.VERSION, acceptableRemoteVersions = References.ACCEPTABLE_REMOTE_VERSION)
public class UpdateMod {

    @SidedProxy(
            clientSide = References.CLIENT_PROXY,
            serverSide = References.SERVER_PROXY
    )
    public static CommonProxy proxy;

    @Mod.Instance(References.MODID)
    public static UpdateMod instance;
    public static SimpleNetworkWrapper NETWORK;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("updatemod");
        NETWORK.registerMessage(PacketUpdateSign.Handler.class, PacketUpdateSign.class, 0, Side.SERVER);
        ModContentRegistry.registerCustomBlocks();

//        ModContentRegistry.registerFromJson();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }
}

