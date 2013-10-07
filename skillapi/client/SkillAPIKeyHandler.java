package skillapi.client;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import skillapi.PlayerSkills;
import skillapi.packets.TriggerSkillPacket;

import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Booleans;

import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;

public class SkillAPIKeyHandler extends KeyHandler{

	private static Minecraft game = Minecraft.getMinecraft();
    private List<KeyBinding> helper;
    private boolean[] active = new boolean[0];
    private boolean[] armed = new boolean[0];
    
	public SkillAPIKeyHandler() {
		super(new KeyBinding[0], new boolean[0]);
	}

	@Override
	public String getLabel() {
		return "SkillAPIKeyHandler";
	}

	@Override
	public void keyDown(EnumSet<TickType> type, KeyBinding kb, boolean end, boolean isRepeat) {
		if (!end)
        {
            return;
        }
        int idx = helper.indexOf(kb);
        if (type.contains(TickType.CLIENT))
        {
            armed[idx] = true;
        }
        if (armed[idx] && (!active[idx] || repeatings[idx]))
        {
            fireKey(kb);
            active[idx] = true;
            armed[idx] = false;
        }
	}

	private void fireKey(KeyBinding kb) {
		if(game.thePlayer!=null){
			for (int i=0;i<SkillAPIClientProxy.skillKeyBindings.length;i++){
				if(SkillAPIClientProxy.skillKeyBindings[i]==kb && PlayerSkills.get(game.thePlayer).skillBar[i]!=null){
					PacketDispatcher.sendPacketToServer(new TriggerSkillPacket(game.thePlayer.entityId,i,PlayerSkills.get(game.thePlayer).skillBar[i].getName()).getPacket());
					return;
				}
			}
			if(kb == SkillAPIClientProxy.skillGuiKeyBinding){
				if(game.currentScreen == null){
					game.displayGuiScreen(new GuiKnownSkills(PlayerSkills.get(game.thePlayer)));
				}
				else if(game.currentScreen instanceof GuiKnownSkills){
					game.thePlayer.closeScreen();
				}
			}
		}
	}

	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean end) {
		if (!end)
        {
            return;
        }
        int idx = helper.indexOf(kb);
        active[idx] = false;
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.CLIENT);
	}
	
	void addKeyBinding(KeyBinding binding, boolean repeats)
    {
        this.keyBindings = ObjectArrays.concat(this.keyBindings, binding);
        this.repeatings = Booleans.concat(this.repeatings, new boolean[] { repeats });
        this.keyDown = new boolean[this.keyBindings.length];
        this.active = new boolean[this.keyBindings.length];
        this.armed = new boolean[this.keyBindings.length];
        this.helper = Arrays.asList(this.keyBindings);
    }

	void setKeyBinding(int i, boolean repeats) {
		this.repeatings[i+1] = repeats;
	}
}
