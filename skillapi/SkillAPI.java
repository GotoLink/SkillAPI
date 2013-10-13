package skillapi;

import skillapi.packets.SkillPacketHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "skillapi", name = "Skill API", version = "0.1")
@NetworkMod(clientSideRequired = true, packetHandler = SkillPacketHandler.class, channels = { SkillPacketHandler.CHANNEL0, SkillPacketHandler.CHANNEL1, SkillPacketHandler.CHANNEL2,
	SkillPacketHandler.CHANNEL3, SkillPacketHandler.CHANNEL4, SkillPacketHandler.CHANNEL5 })
public final class SkillAPI {
	@SidedProxy(modId = "skillapi", clientSide = "skillapi.client.SkillAPIClientProxy", serverSide = "skillapi.SkillAPIProxy")
	public static SkillAPIProxy proxy;

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
