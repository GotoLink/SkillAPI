package skillapi.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

public abstract class SkillPacket {
	public final Packet getPacket() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        try {
			write(out);
		} catch (IOException e) {
			e.printStackTrace();
		}
        return new Packet250CustomPayload(getChannel(), bos.toByteArray());
	}

	abstract String getChannel();
	abstract void write(DataOutput out) throws IOException;
	abstract void read(DataInput in) throws IOException;
	abstract void run(EntityPlayer player);
}
