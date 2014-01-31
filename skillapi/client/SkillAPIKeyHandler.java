package skillapi.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import skillapi.PlayerSkills;
import skillapi.SkillAPI;
import skillapi.packets.SkillPacket;
import skillapi.packets.TriggerSkillPacket;

import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Booleans;

public class SkillAPIKeyHandler {
	private static Minecraft game = Minecraft.getMinecraft();
    public KeyBinding[] keyBindings = new KeyBinding[0];
    private boolean[] repeatings = new boolean[0];
	private boolean[] active = new boolean[0];

	public SkillAPIKeyHandler() {
        FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	public void keyDown(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END){
            for(int idx=0; idx<keyBindings.length; idx++){
                if(keyBindings[idx].func_151470_d()){
                    if (!active[idx] || repeatings[idx]) {
                        fireKey(keyBindings[idx]);
                        active[idx] = true;
                    }
                }else{
                    active[idx] = false;
                }
            }
        }
	}

	private void fireKey(KeyBinding kb) {
		if (game.thePlayer != null) {
			if (game.currentScreen == null) {
				for (int i = 0; i < SkillAPIClientProxy.skillKeyBindings.length; i++) {
					if (SkillAPIClientProxy.skillKeyBindings[i] == kb && PlayerSkills.get(game.thePlayer).skillBar[i] != null) {
                        SkillPacket pkt = new TriggerSkillPacket(game.thePlayer.func_145782_y(), i, PlayerSkills.get(game.thePlayer).skillBar[i].getName());
						SkillAPI.channels.get(pkt.getChannel()).sendToServer(pkt.getPacket(Side.SERVER));
						return;
					}
				}
			}
			if (kb == SkillAPIClientProxy.skillGuiKeyBinding) {
				if (game.currentScreen == null) {
					game.func_147108_a(new GuiKnownSkills(PlayerSkills.get(game.thePlayer)));
				} else if (game.currentScreen instanceof GuiKnownSkills) {
					game.thePlayer.closeScreen();
				}
			}
		}
	}

	void addKeyBinding(KeyBinding binding, boolean repeats) {
		this.keyBindings = ObjectArrays.concat(this.keyBindings, binding);
		this.repeatings = Booleans.concat(this.repeatings, new boolean[] { repeats });
		this.active = new boolean[this.keyBindings.length];
	}

	void setKeyBinding(int i, boolean repeats) {
		this.repeatings[i + 1] = repeats;
	}
}
