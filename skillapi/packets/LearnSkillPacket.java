package skillapi.packets;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import skillapi.PlayerSkills;
import skillapi.SkillRegistry;

public class LearnSkillPacket extends SkillPacket {
	protected int id;
	protected String skill;

	public LearnSkillPacket() {
	}

	public LearnSkillPacket(int id, String skill) {
		this.id = id;
		this.skill = skill;
	}

	@Override
	String getChannel() {
		return SkillPacketHandler.CHANNEL1;
	}

	@Override
	void write(DataOutput out) throws IOException {
		out.writeInt(id);
		if (skill != null)
			out.writeUTF(skill);
	}

	@Override
	void read(DataInput in) throws IOException {
		id = in.readInt();
		try {
			skill = in.readUTF();
		} catch (EOFException e) {
		}
	}

	@Override
	void run(EntityPlayer player) {
		if (player.entityId == id && (SkillRegistry.isSkillRegistered(skill) || skill == null)) {
			PlayerSkills.get(player).skillJustLearnt = SkillRegistry.get(skill);
			if (skill != null && !SkillRegistry.isSkillKnown(player, skill)) {
				PlayerSkills.get(player).knownSkills.add(skill);
			}
		}
	}
}
