package fr.broawz.updatemod.utils;

import fr.broawz.updatemod.creativetabs.UpdatedCreativeTabs;
import net.minecraft.creativetab.CreativeTabs;

public class References {
    public static final String MODID = "updatemod";
    public static final String NAME = "Update Mod";
    public static final String VERSION = "1.0.0";

    public static final String ACCEPTABLE_REMOTE_VERSION = "*";
    public static final String CLIENT_PROXY = "fr.broawz.updatemod.proxy.ClientProxy";
    public static final String SERVER_PROXY = "fr.broawz.updatemod.proxy.ServerProxy";

    public static final CreativeTabs UPDATED_MOD = new UpdatedCreativeTabs("UpdateMod");
}
