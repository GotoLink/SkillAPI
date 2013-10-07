package skillapi.packets;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import skillapi.PlayerSkills;
import skillapi.SkillRegistry;

public class ActiveSkillPacket extends LearnSkillPacket{

	private boolean active;

	public ActiveSkillPacket(){}
	public ActiveSkillPacket(int id, String skill, boolean active){
		super(id,skill);
		this.active = active;
	}
	@Override
	String getChannel() {
		return SkillPacketHandler.CHANNEL4;
	}

	@Override
	void write(DataOutput out) throws IOException {
		super.write(out);
		out.writeBoolean(active);
	}

	@Override
	void read(DataInput in) throws IOException {
		super.read(in);
		active = in.readBoolean();
	}

	@Override
	void run(EntityPlayer player) {
		if(player.entityId == id && SkillRegistry.isSkillRegistered(skill)){
			if(active){
				PlayerSkills.get(player).activeSkills.add(skill);
			}else{
				PlayerSkills.get(player).activeSkills.remove(skill);
			}
		}
	}

}
