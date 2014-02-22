package skillapi.packets;

import java.util.HashMap;
import java.util.Map;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;

public class SkillPacketHandler {
	public static final String[] CHANNELS = {"APISKILLINIT", "APISKILLGET", "APISKILLUPDATE", "APISKILLTRIGGER", "APISKILLACTIVATE", "APISKILLTICK", "APISKILLMANA"};

	@SubscribeEvent
	public void onServerPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
		SkillPacket skpacket = packets.get(event.packet.channel());
		if (skpacket != null) {
			skpacket.fromBytes(event.packet.payload());
			if(skpacket.run(((NetHandlerPlayServer) event.handler).playerEntity)){
                FMLProxyPacket proxy = skpacket.getPacket(Side.CLIENT);
                proxy.setDispatcher(event.packet.getDispatcher());
                event.reply = proxy;
            }
		}
	}

    @SubscribeEvent
    public void onClientPacket(FMLNetworkEvent.ClientCustomPacketEvent event){
        SkillPacket skpacket = packets.get(event.packet.channel());
        if (skpacket != null) {
            skpacket.fromBytes(event.packet.payload());
            skpacket.run(getPlayer());
        }
    }

    @SideOnly(Side.CLIENT)
    public static EntityPlayer getPlayer(){
        return FMLClientHandler.instance().getClient().thePlayer;
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
