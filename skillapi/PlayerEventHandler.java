package skillapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import skillapi.packets.InitSkillPacket;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PlayerEventHandler implements IPlayerTracker {
	public Map<String, List<String>> knownSkillsBackup = new HashMap<String, List<String>>();
	public Map<String, Skill[]> skillBarBackup = new HashMap<String, Skill[]>();

	@ForgeSubscribe
	public void onConstructing(EntityConstructing event) {
		if (event.entity instanceof EntityPlayer) {
			if (((EntityPlayer) event.entity).getExtendedProperties("SKILLAPI") == null) {
				((EntityPlayer) event.entity).registerExtendedProperties("SKILLAPI", new PlayerSkills((EntityPlayer) event.entity, 20));
			}
		}
	}

	@ForgeSubscribe
	public void onSpawn(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityPlayerMP) {
			PacketDispatcher.sendPacketToPlayer(new InitSkillPacket(PlayerSkills.get((EntityPlayer) event.entity)).getPacket(), (Player) event.entity);
		}
	}

	@ForgeSubscribe
	public void onLivingUpdate(LivingUpdateEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			PlayerSkills sk = PlayerSkills.get((EntityPlayer) event.entityLiving);
			sk.setPrevMana(sk.getMana());
			if (sk.getMana() < 20 && (event.entityLiving.ticksExisted % 20) * 12 == 0)
				sk.restoreMana(1);
		}
	}

	@ForgeSubscribe
	public void onDeath(LivingDeathEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			PlayerSkills skills = PlayerSkills.get((EntityPlayer) event.entityLiving);
			skills.resetSkills();
			if (event.entityLiving instanceof EntityPlayerMP) {
				knownSkillsBackup.put(((EntityPlayer) event.entityLiving).username, skills.knownSkills);
				skillBarBackup.put(((EntityPlayer) event.entityLiving).username, skills.skillBar);
			}
		}
	}

	@ForgeSubscribe
	public void afterSleepInBed(PlayerSleepInBedEvent event) {
		if (!event.entityPlayer.worldObj.isRemote && !event.entityPlayer.isPlayerSleeping()) {
			if (!event.entityPlayer.worldObj.isDaytime() && event.entityPlayer.isEntityAlive() && event.entityPlayer.worldObj.provider.isSurfaceWorld()) {
				PlayerSkills.get(event.entityPlayer).resetSkills();
			}
		}
	}

	@Override
	public void onPlayerLogin(EntityPlayer player) {
		PacketDispatcher.sendPacketToPlayer(new InitSkillPacket(PlayerSkills.get(player)).getPacket(), (Player) player);
	}

	@Override
	public void onPlayerLogout(EntityPlayer player) {
	}

	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {
		PacketDispatcher.sendPacketToPlayer(new InitSkillPacket(PlayerSkills.get(player)).getPacket(), (Player) player);
	}

	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		PlayerSkills.get(player).setMana(20);
		if (knownSkillsBackup.containsKey(player.username)) {
			PlayerSkills.get(player).knownSkills = knownSkillsBackup.remove(player.username);
			PlayerSkills.get(player).skillBar = skillBarBackup.remove(player.username);
		}
		PacketDispatcher.sendPacketToPlayer(new InitSkillPacket(PlayerSkills.get(player)).getPacket(), (Player) player);
	}
}
