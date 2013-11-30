package skillapi.packets;

import net.minecraft.entity.player.EntityPlayer;
import skillapi.PlayerSkills;
import skillapi.SkillRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class TriggerSkillPacket extends UpdateSkillPacket {
	public TriggerSkillPacket() {
	}

	public TriggerSkillPacket(int id, int i, String name) {
		super(id, i, name);
	}

	@Override
	String getChannel() {
		return SkillPacketHandler.CHANNEL3;
	}

	@Override
	void run(EntityPlayer player) {
		if (player.entityId == id && SkillRegistry.isSkillKnown(player, skill)) {
			if (PlayerSkills.get(player).skillBar[num] == SkillRegistry.get(skill)) {//Valid trigger packet
				PlayerSkills.get(player).skillBar[num].triggerSkill(player);
				if (!player.worldObj.isRemote) {//Send back checked info to client
					PacketDispatcher.sendPacketToPlayer(getPacket(), (Player) player);
				}
			}
		}
	}
}
