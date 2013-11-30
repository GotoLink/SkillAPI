package skillapi.packets;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import skillapi.PlayerSkills;
import skillapi.SkillAPI;
import skillapi.SkillRegistry;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class UpdateSkillPacket extends LearnSkillPacket {
	protected int num;

	public UpdateSkillPacket() {
	}

	public UpdateSkillPacket(int id, int num, String skill) {
		super(id, skill);
		this.num = num;
	}

	@Override
	String getChannel() {
		return SkillPacketHandler.CHANNEL2;
	}

	@Override
	void write(DataOutput out) throws IOException {
		out.writeInt(num);
		super.write(out);
	}

	@Override
	void read(DataInput in) throws IOException {
		num = in.readInt();
		super.read(in);
	}

	@Override
	void run(EntityPlayer player) {
		if (player.entityId == id && (SkillRegistry.isSkillKnown(player, skill) || skill == null)) {//Valid update packet
			if (num >= 0) {
				PlayerSkills.get(player).skillBar[num] = SkillRegistry.get(skill);
			} else {
				PlayerSkills.get(player).chargingSkill = SkillRegistry.get(skill);
			}
			if (!player.worldObj.isRemote) {//Send back checked info to client
				PacketDispatcher.sendPacketToPlayer(getPacket(), (Player) player);
			}
			SkillAPI.proxy.updateKeyBindingTypes(player);
		}
	}
}
