package skillapi.packets;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class SkillPacketHandler implements IPacketHandler {
	public static final String CHANNEL0 = "APISKILLINIT";
	public static final String CHANNEL1 = "APISKILLGET";
	public static final String CHANNEL2 = "APISKILLUPDATE";
	public static final String CHANNEL3 = "APISKILLTRIGGER";
	public static final String CHANNEL4 = "APISKILLACTIVATE";
	public static final String CHANNEL5 = "APISKILLTICK";
	public static final String CHANNEL6 = "APISKILLMANA";

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		SkillPacket skpacket = packets.get(packet.channel);
		if (skpacket != null) {
			try {
				skpacket.read(new DataInputStream(new ByteArrayInputStream(packet.data)));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			skpacket.run((EntityPlayer) player);
		}
	}
	
	public static Map<String, SkillPacket> packets = new HashMap<String, SkillPacket>();
	static{
		packets.put(CHANNEL0, new InitSkillPacket());
		packets.put(CHANNEL1, new LearnSkillPacket());
		packets.put(CHANNEL2, new UpdateSkillPacket());
		packets.put(CHANNEL3, new TriggerSkillPacket());
		packets.put(CHANNEL4, new ActiveSkillPacket());
		packets.put(CHANNEL5, new TickDataSkillPacket());
		packets.put(CHANNEL6, new ManaSpentPacket());
	}
}
