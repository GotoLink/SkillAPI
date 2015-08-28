package skillapi.packets;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;

public abstract class SkillPacket implements IMessage {
	public final FMLProxyPacket getPacket(Side side) {
        ByteBuf buf = Unpooled.buffer();
        toBytes(buf);
        FMLProxyPacket p = new FMLProxyPacket(new PacketBuffer(buf), getChannel());
        p.setTarget(side);
		return p;
	}

	public abstract String getChannel();

	abstract boolean run(EntityPlayer player);
}
