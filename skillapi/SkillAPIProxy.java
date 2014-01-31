package skillapi;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class SkillAPIProxy {
	public void loadSkillKeyBindings() {
	}

	public void updateKeyBindingTypes(EntityPlayer player) {
	}

	public void register() {
		FMLCommonHandler.instance().bus().register(new SkillTickHandler());
		PlayerEventHandler playerEvent = new PlayerEventHandler();
		MinecraftForge.EVENT_BUS.register(playerEvent);
        FMLCommonHandler.instance().bus().register(playerEvent);
	}
}
