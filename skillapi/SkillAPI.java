package skillapi;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import skillapi.packets.SkillPacketHandler;

import java.util.HashMap;
import java.util.Map;

@Mod(modid = "skillapi", name = "Skill API", version = "$version")
public final class SkillAPI {
	@SidedProxy(modId = "skillapi", clientSide = "skillapi.client.SkillAPIClientProxy", serverSide = "skillapi.SkillAPIProxy")
	public static SkillAPIProxy proxy;
    public static Map<String, FMLEventChannel> channels;

    @EventHandler
    public void pre(FMLPreInitializationEvent event){
        channels = new HashMap<String, FMLEventChannel>();
        Object handler = new SkillPacketHandler();
        FMLEventChannel channel;
        for(int i=0; i<SkillPacketHandler.CHANNELS.length; i++){
            channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(SkillPacketHandler.CHANNELS[i]);
            channel.register(handler);
            channels.put(SkillPacketHandler.CHANNELS[i], channel);
        }
        if(event.getSourceFile().getName().endsWith(".jar") && event.getSide().isClient()){
            try {
                Class.forName("mods.mud.ModUpdateDetector").getDeclaredMethod("registerMod", ModContainer.class, String.class, String.class).invoke(null,
                        FMLCommonHandler.instance().findContainerFor(this),
                        "https://raw.github.com/GotoLink/SkillAPI/master/API_update.xml",
                        "https://raw.github.com/GotoLink/SkillAPI/master/API_changelog.md"
                );
            } catch (Throwable e) {
            }
        }
    }

	@EventHandler
	public void load(FMLInitializationEvent event) {
		proxy.loadSkillKeyBindings();
		proxy.register();
	}

	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new SkillCommand());
	}
}
