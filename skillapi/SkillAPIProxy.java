package skillapi;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class SkillAPIProxy {
	public void loadSkillKeyBindings() {
	}

	public void updateKeyBindingTypes(EntityPlayer player) {
	}

	public void register() {
		TickRegistry.registerScheduledTickHandler(new SkillTickHandler(), Side.SERVER);
		PlayerEventHandler playerEvent = new PlayerEventHandler();
		MinecraftForge.EVENT_BUS.register(playerEvent);
		GameRegistry.registerPlayerTracker(playerEvent);
	}
}
