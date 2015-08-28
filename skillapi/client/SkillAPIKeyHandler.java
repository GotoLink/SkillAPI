package skillapi.client;

import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Booleans;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import skillapi.PlayerSkills;
import skillapi.Skill;
import skillapi.SkillAPI;
import skillapi.packets.SkillPacket;
import skillapi.packets.TriggerSkillPacket;

public class SkillAPIKeyHandler {
    public static final SkillAPIKeyHandler INSTANCE = new SkillAPIKeyHandler();
    public KeyBinding[] keyBindings = new KeyBinding[0];
    private boolean[] repeatings = new boolean[0];
	private boolean[] active = new boolean[0];

	private SkillAPIKeyHandler() {
        FMLCommonHandler.instance().bus().register(this);
	}

	@SubscribeEvent
	public void keyDown(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.END){
            for(int idx=0; idx<keyBindings.length; idx++){
                if(keyBindings[idx].isKeyDown()){
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
		if (Minecraft.getMinecraft().thePlayer != null) {
			if (Minecraft.getMinecraft().currentScreen == null) {
				if (kb == SkillAPIClientProxy.skillGuiKeyBinding) {
					Minecraft.getMinecraft().displayGuiScreen(new GuiKnownSkills());
					return;
				}
				for (int i = 0; i < SkillAPIClientProxy.skillKeyBindings.length; i++) {
					if (SkillAPIClientProxy.skillKeyBindings[i] == kb) {
						Skill skill = PlayerSkills.get(Minecraft.getMinecraft().thePlayer).skillBar[i];
						if (skill != null) {
							SkillPacket pkt = new TriggerSkillPacket(Minecraft.getMinecraft().thePlayer.getEntityId(), i, skill.getName());
							SkillAPI.channels.get(pkt.getChannel()).sendToServer(pkt.getPacket(Side.SERVER));
						}
						break;
					}
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
