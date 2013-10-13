package skillapi.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;

import skillapi.PlayerSkills;
import skillapi.Skill;
import skillapi.SkillAPIProxy;
import cpw.mods.fml.client.registry.KeyBindingRegistry;

public class SkillAPIClientProxy extends SkillAPIProxy {
	public static KeyBinding skillGuiKeyBinding, skillKeyBindings[] = new KeyBinding[5];
	public static char keys[] = { 'K', 'Z', 'X', 'C', 'V', 'B' };
	public static SkillAPIKeyHandler handler = new SkillAPIKeyHandler();

	@Override
	public void loadSkillKeyBindings() {
		skillGuiKeyBinding = new KeyBinding("key.skillGui", Keyboard.getKeyIndex(Character.toString(keys[0])));
		handler.addKeyBinding(skillGuiKeyBinding, false);
		for (int i = 1; i < keys.length; i++) {
			skillKeyBindings[i - 1] = new KeyBinding("key.skill" + i, Keyboard.getKeyIndex(Character.toString(keys[i])));
		}
		for (int i = 0; i < skillKeyBindings.length; i++) {
			handler.addKeyBinding(skillKeyBindings[i], false);
		}
		KeyBindingRegistry.registerKeyBinding(handler);
		updateKeyBindingTypes(Minecraft.getMinecraft().thePlayer);
	}

	@Override
	public void updateKeyBindingTypes(EntityPlayer player) {
		if (player == null)
			return;
		Skill skill;
		for (int i = 0; i < PlayerSkills.get(player).skillBar.length; i++) {
			skill = PlayerSkills.get(player).skillBar[i];
			if (skill == null || skill.getChargeupTime(player) > 0 || skill.getCooldownTime(player) > 0 || skill.getDuration(player) > 0)
				handler.setKeyBinding(i, false);
			else
				handler.setKeyBinding(i, true);
		}
	}

	@Override
	public void register() {
		super.register();
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
	}
}
