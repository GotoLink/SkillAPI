package skillapi.packets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import skillapi.PlayerSkills;
import skillapi.SkillRegistry;

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
				if (player instanceof EntityPlayerMP) {//Send back checked info to client
					((EntityPlayerMP) player).playerNetServerHandler.sendPacketToPlayer(getPacket());
				}
			}
		}
	}
}
