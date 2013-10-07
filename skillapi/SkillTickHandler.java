package skillapi;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.player.EntityPlayer;
import skillapi.packets.ActiveSkillPacket;
import skillapi.packets.TickDataSkillPacket;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class SkillTickHandler implements IScheduledTickHandler{

	public static Map<String,Map<String,int[]>> timers = new ConcurrentHashMap();//username->skill->charge, cooldown, duration timers
	public final static int mult = 10;
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		List<String> actives = PlayerSkills.get((EntityPlayer)tickData[0]).activeSkills;
		for (String skill : actives){
			SkillRegistry.get(skill).onTickWhileActive((EntityPlayer) tickData[0]);
		}
		if(timers.containsKey(((EntityPlayer)tickData[0]).username)){
			Map<String,int[]> map = timers.get(((EntityPlayer)tickData[0]).username);
			for(String skillName:map.keySet()){
				int[] data = map.get(skillName);
				for(int i=0;i<3;i++){
					if(data[i]>0){
						data[i]++;
					}
				}
				Skill skill = SkillRegistry.get(skillName);
				if(data[0]==0 && PlayerSkills.get((EntityPlayer)tickData[0]).chargingSkill != null && PlayerSkills.get((EntityPlayer)tickData[0]).chargingSkill.getName().equals(skillName)){
					PlayerSkills.get((EntityPlayer)tickData[0]).chargingSkill = null;
				}
				if(data[0]>skill.getChargeupTime((EntityPlayer)tickData[0])*mult){
					data[0]=0;//stop charge timer
					if(skill.onSkillTrigger((EntityPlayer)tickData[0])){
						PlayerSkills.get((EntityPlayer)tickData[0]).spendMana(skill.getManaCost((EntityPlayer)tickData[0]));
						if(skill.getDuration((EntityPlayer)tickData[0])>0){
							if(!actives.contains(skillName)){
								actives.add(skillName);
								PacketDispatcher.sendPacketToPlayer(new ActiveSkillPacket(((EntityPlayer)tickData[0]).entityId,skillName,true).getPacket(), (Player) tickData[0]);
							}
							data[2]=1;//start skill timer
						}
						data[1]=1;//start cooldown timer
					} else{
						((EntityPlayer)tickData[0]).worldObj.playSoundAtEntity((EntityPlayer)tickData[0], "note.bass", 1.0F, 0F);
					}
				}
				if(data[1]>skill.getCooldownTime((EntityPlayer)tickData[0])*mult){
					data[1]=0;//stop cooldown timer
				}
				if(data[2]>skill.getDuration((EntityPlayer)tickData[0])*mult ||((EntityPlayer)tickData[0]).isDead){
					data[2]=0;//stop skill timer
					actives.remove(skillName);
					PacketDispatcher.sendPacketToPlayer(new ActiveSkillPacket(((EntityPlayer)tickData[0]).entityId,skillName,false).getPacket(), (Player) tickData[0]);
					skill.onSkillEnd((EntityPlayer)tickData[0]);
				}
				skill.charge = data[0];
				skill.cooldownFrame = data[1];
				skill.timeLeft = skill.getDuration((EntityPlayer)tickData[0])<=0?0:(skill.getDuration((EntityPlayer)tickData[0])- data[2]/mult);
				PacketDispatcher.sendPacketToPlayer(new TickDataSkillPacket(((EntityPlayer)tickData[0]).entityId,skill).getPacket(), (Player) tickData[0]);
				map.put(skillName, data);
			}
		}
	}

	@Override
	public EnumSet<TickType> ticks() {
		return EnumSet.of(TickType.PLAYER);
	}

	@Override
	public String getLabel() {
		return "SKillAPI.Player.Tick";
	}

	@Override
	public int nextTickSpacing() {
		return 1;
	}
	
	public static Map<String,int[]> get(EntityPlayer player){
		if(!timers.containsKey(player.username))
			timers.put(player.username, new HashMap());
		return timers.get(player.username);
	}

	public static int[] getData(EntityPlayer player, Skill skill) {
		Map<String,int[]> playerSkillMap = get(player);
		if(!playerSkillMap.containsKey(skill.getName())){
			playerSkillMap.put(skill.getName(),new int[]{0,0,0});
		}
		return playerSkillMap.get(skill.getName());
	}
}
