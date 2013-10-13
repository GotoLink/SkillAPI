package skillapi.packets;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import skillapi.PlayerSkills;
import skillapi.Skill;
import skillapi.SkillRegistry;

public class TickDataSkillPacket extends UpdateSkillPacket {
	private float time;
	private int charge;

	public TickDataSkillPacket() {
	}

	public TickDataSkillPacket(int id, Skill skill) {
		super(id, skill.cooldownFrame, skill.getName());
		this.charge = skill.charge;
		this.time = skill.timeLeft;
	}

	@Override
	String getChannel() {
		return SkillPacketHandler.CHANNEL5;
	}

	@Override
	void write(DataOutput out) throws IOException {
		super.write(out);
		out.writeFloat(time);
		out.writeInt(charge);
	}

	@Override
	void read(DataInput in) throws IOException {
		super.read(in);
		time = in.readFloat();
		charge = in.readInt();
	}

	@Override
	void run(EntityPlayer player) {
		if (player.entityId == id && SkillRegistry.isSkillKnown(player, skill)) {//Valid update packet
			Skill skil = SkillRegistry.get(skill);
			skil.charge = charge;
			if (charge == 0 && PlayerSkills.get(player).chargingSkill != null && PlayerSkills.get(player).chargingSkill.getName().equals(skill)) {
				PlayerSkills.get(player).chargingSkill = null;
			}
			skil.timeLeft = time;
			skil.cooldownFrame = num;
		}
	}
}
