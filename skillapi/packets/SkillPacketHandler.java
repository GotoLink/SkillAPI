package skillapi.packets;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import skillapi.SkillAPI;

import java.util.HashMap;
import java.util.Map;

public class SkillPacketHandler {
	public static final String[] CHANNELS = {"APISKILLINIT", "APISKILLGET", "APISKILLUPDATE", "APISKILLTRIGGER", "APISKILLACTIVATE", "APISKILLTICK", "APISKILLMANA"};

	@SubscribeEvent
	public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
		final SkillPacket skpacket = packets.get(event.packet.channel());
		if (skpacket != null) {
			skpacket.fromBytes(event.packet.payload());
			final EntityPlayerMP player = ((NetHandlerPlayServer) event.handler).playerEntity;
			addTask(event.handler, new Runnable() {
				@Override
				public void run() {
					if(skpacket.run(player)){
						SkillAPI.channels.get(skpacket.getChannel()).sendTo(skpacket.getPacket(Side.CLIENT), player);
					}
				}
			});
		}
	}

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
		final SkillPacket skpacket = packets.get(event.packet.channel());
		if (skpacket != null) {
			skpacket.fromBytes(event.packet.payload());
			addTask(event.handler, new Runnable() {
				@Override
				public void run() {
					skpacket.run(SkillAPI.proxy.getPlayer());
				}
			});
		}
	}

	private void addTask(INetHandler netHandler, Runnable runnable){
		FMLCommonHandler.instance().getWorldThread(netHandler).addScheduledTask(runnable);
	}
	
	public static Map<String, SkillPacket> packets = new HashMap<String, SkillPacket>();
	static{
		packets.put(CHANNELS[0], new InitSkillPacket());
		packets.put(CHANNELS[1], new LearnSkillPacket());
		packets.put(CHANNELS[2], new UpdateSkillPacket());
		packets.put(CHANNELS[3], new TriggerSkillPacket());
		packets.put(CHANNELS[4], new ActiveSkillPacket());
		packets.put(CHANNELS[5], new TickDataSkillPacket());
		packets.put(CHANNELS[6], new ManaSpentPacket());
	}
}
