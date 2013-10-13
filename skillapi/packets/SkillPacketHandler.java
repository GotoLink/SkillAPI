package skillapi.packets;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

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

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		SkillPacket skpacket = null;
		if (packet.channel.equals(CHANNEL0)) {
			skpacket = new InitSkillPacket();
		} else if (packet.channel.equals(CHANNEL1)) {
			skpacket = new LearnSkillPacket();
		} else if (packet.channel.equals(CHANNEL2)) {
			skpacket = new UpdateSkillPacket();
		} else if (packet.channel.equals(CHANNEL3)) {
			skpacket = new TriggerSkillPacket();
		} else if (packet.channel.equals(CHANNEL4)) {
			skpacket = new ActiveSkillPacket();
		} else if (packet.channel.equals(CHANNEL5)) {
			skpacket = new TickDataSkillPacket();
		}
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
}
