package skillapi.packets;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import skillapi.PlayerSkills;

public class ManaSpentPacket extends SkillPacket{
	protected int id;
	protected int cost;
	
	public ManaSpentPacket(int entityId, int mana) {
		this.id = entityId;
		this.cost = mana;
	}

	public ManaSpentPacket() {
	}

	@Override
	String getChannel() {
		return SkillPacketHandler.CHANNEL6;
	}

	@Override
	void write(DataOutput out) throws IOException {
		out.writeInt(id);
		out.writeInt(cost);
	}

	@Override
	void read(DataInput in) throws IOException {
		id=in.readInt();
		cost=in.readInt();
	}

	@Override
	void run(EntityPlayer player) {
		if(player.entityId == id){
			PlayerSkills.get(player).spendMana(cost);
		}
	}
}
