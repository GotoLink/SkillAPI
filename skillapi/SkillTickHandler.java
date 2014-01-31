package skillapi;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import skillapi.packets.ActiveSkillPacket;
import skillapi.packets.ManaSpentPacket;
import skillapi.packets.SkillPacket;
import skillapi.packets.TickDataSkillPacket;

public class SkillTickHandler {
	public static Map<UUID, Map<String, int[]>> timers = new ConcurrentHashMap<UUID, Map<String, int[]>>();//username->skill->charge, cooldown, duration timers
	public final static int mult = 10;

	@SubscribeEvent
	public void tickEnd(TickEvent.PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.END && event.side.isServer()){
            List<String> actives = PlayerSkills.get(event.player).activeSkills;
            for (String skill : actives) {
                SkillRegistry.get(skill).onTickWhileActive(event.player);
            }
            if (timers.containsKey(event.player.getUniqueID())) {
                Map<String, int[]> map = timers.get(event.player.getUniqueID());
                for (String skillName : map.keySet()) {
                    int[] data = map.get(skillName);
                    for (int i = 0; i < 3; i++) {
                        if (data[i] > 0) {
                            data[i]++;
                        }
                    }
                    Skill skill = SkillRegistry.get(skillName);
                    if (data[0] == 0 && PlayerSkills.get(event.player).chargingSkill != null && PlayerSkills.get(event.player).chargingSkill.getName().equals(skillName)) {
                        PlayerSkills.get(event.player).chargingSkill = null;
                    }
                    SkillPacket pkt;
                    if (data[0] > skill.getChargeupTime(event.player) * mult) {
                        data[0] = 0;//stop charge timer
                        if (skill.onSkillTrigger(event.player)) {
                            PlayerSkills.get(event.player).spendMana(skill.getManaCost(event.player));
                            pkt = new ManaSpentPacket(event.player.func_145782_y(), skill.getManaCost(event.player));
                            SkillAPI.channels.get(pkt.getChannel()).sendTo(pkt.getPacket(Side.CLIENT), (EntityPlayerMP) event.player);
                            if (skill.getDuration(event.player) > 0) {
                                if (!actives.contains(skillName)) {
                                    actives.add(skillName);
                                    pkt = new ActiveSkillPacket(event.player.func_145782_y(), skillName, true);
                                    SkillAPI.channels.get(pkt.getChannel()).sendTo(pkt.getPacket(Side.CLIENT), (EntityPlayerMP) event.player);
                                }
                                data[2] = 1;//start skill timer
                            }
                            data[1] = 1;//start cooldown timer
                        } else {
                            event.player.worldObj.playSoundAtEntity(event.player, "note.bass", 1.0F, 0F);
                        }
                    }
                    if (data[1] > skill.getCooldownTime(event.player) * mult) {
                        data[1] = 0;//stop cooldown timer
                    }
                    if (data[2] > skill.getDuration(event.player) * mult || event.player.isDead) {
                        data[2] = 0;//stop skill timer
                        actives.remove(skillName);
                        pkt = new ActiveSkillPacket(event.player.func_145782_y(), skillName, false);
                        SkillAPI.channels.get(pkt.getChannel()).sendTo(pkt.getPacket(Side.CLIENT), (EntityPlayerMP) event.player);
                        skill.onSkillEnd(event.player);
                    }
                    skill.charge = data[0];
                    skill.cooldownFrame = data[1];
                    skill.timeLeft = skill.getDuration(event.player) <= 0 ? 0 : (skill.getDuration(event.player) - data[2] / mult);
                    pkt = new TickDataSkillPacket(event.player.func_145782_y(), skill);
                    SkillAPI.channels.get(pkt.getChannel()).sendTo(pkt.getPacket(Side.CLIENT), (EntityPlayerMP) event.player);
                    map.put(skillName, data);
                }
            }
        }
	}

	public static Map<String, int[]> get(EntityPlayer player) {
		if (!timers.containsKey(player.getUniqueID()))
			timers.put(player.getUniqueID(), new HashMap<String, int[]>());
		return timers.get(player.getUniqueID());
	}

	public static int[] getData(EntityPlayer player, Skill skill) {
		Map<String, int[]> playerSkillMap = get(player);
		if (!playerSkillMap.containsKey(skill.getName())) {
			playerSkillMap.put(skill.getName(), new int[] { 0, 0, 0 });
		}
		return playerSkillMap.get(skill.getName());
	}
}
