package skillapi.packets;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import skillapi.PlayerSkills;
import skillapi.Skill;
import skillapi.SkillAPI;
import skillapi.SkillRegistry;

public class InitSkillPacket extends SkillPacket {
	private int id;
	private int mana;
	private List<String> known = new ArrayList<String>();
	private List<String> active = new LinkedList<String>();
	private Skill[] bar = new Skill[5];

	public InitSkillPacket() {
	}

	public InitSkillPacket(PlayerSkills skills) {
		this.id = skills.getPlayer().entityId;
		this.mana = skills.getMana();
		this.known = skills.knownSkills;
		this.active = skills.activeSkills;
		this.bar = skills.skillBar;
	}

	@Override
	String getChannel() {
		return SkillPacketHandler.CHANNEL0;
	}

	@Override
	void write(DataOutput out) throws IOException {
		out.writeInt(id);
		out.writeInt(mana);
		out.writeInt(known.size());
		if (known.size() > 0) {
			for (String skill : known) {
				out.writeUTF(skill);
			}
		}
		out.writeInt(active.size());
		if (active.size() > 0) {
			for (String skill : active) {
				out.writeUTF(skill);
			}
		}
		for (int i = 0; i < bar.length; i++) {
			if (bar[i] != null) {
				out.writeInt(i);
				out.writeUTF(bar[i].getName());
			}
		}
	}

	@Override
	void read(DataInput in) throws IOException {
		id = in.readInt();
		mana = in.readInt();
		int size = in.readInt();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				known.add(in.readUTF());
			}
		}
		size = in.readInt();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				active.add(in.readUTF());
			}
		}
		while (true) {
			try {
				size = in.readInt();
				bar[size] = SkillRegistry.get(in.readUTF());
			} catch (EOFException e) {
				break;
			}
		}
	}

	@Override
	void run(EntityPlayer player) {
		if (player.entityId == id) {
			PlayerSkills skills = PlayerSkills.get(player);
			skills.setMana(mana);
			skills.knownSkills.clear();
			for (String sk : known) {
				skills.knownSkills.add(sk);
			}
			skills.activeSkills.clear();
			for (String sk : active) {
				skills.activeSkills.add(sk);
			}
			for (int i = 0; i < bar.length; i++) {
				skills.skillBar[i] = bar[i];
			}
			SkillAPI.proxy.updateKeyBindingTypes(player);
		}
	}
}
