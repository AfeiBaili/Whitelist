package cn.afeibaili.whitelist;

import cn.afeibaili.whitelist.listener.Listener;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = Whitelist.MODID,
    version = Tags.VERSION,
    name = "Whitelist",
    acceptedMinecraftVersions = "[1.7.10]",
    acceptableRemoteVersions = "*",
    acceptableSaveVersions = "*"
)
public class Whitelist {

    public static final String MODID = "whitelist";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new Listener());
    }
}
