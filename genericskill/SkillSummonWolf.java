package genericskill;

import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

public class SkillSummonWolf extends SkillGeneric {
	@Override
	public boolean canPlayerUseSkill(EntityPlayer player) {
		return player.inventory.hasItem(Item.bone.itemID);
	}

	@Override
	public float getChargeupTime(EntityPlayer player) {
		return 5;
	}

	@Override
	public float getCooldownTime(EntityPlayer player) {
		return 60;
	}

	@Override
	public String getDescription() {
		return "Injects your life force\ninto a bone thereby summoning\na trusty canine companion.\n(requires a bone)";
	}

	@Override
	public int getManaCost(EntityPlayer player) {
		return 10;
	}

	@Override
	public boolean onSkillTrigger(EntityPlayer player) {
		if (!canPlayerUseSkill(player))
			return false;
		player.inventory.consumeInventoryItem(Item.bone.itemID);
		player.worldObj.playSoundAtEntity(player, "mob.ghast.fireball", 1.0F, 1.0F);
		EntityWolf wolf = new EntityWolf(player.worldObj);
		wolf.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		wolf.spawnExplosionParticle();
		if (!player.worldObj.isRemote) {
			wolf.setTamed(true);
			wolf.setPathToEntity(null);
			wolf.setAttackTarget(null);
			wolf.setSitting(false);
			wolf.setHealth(20);
			wolf.setOwner(player.getCommandSenderName());
			player.worldObj.setEntityState(wolf, (byte) 7);
			player.worldObj.spawnEntityInWorld(wolf);
		}
		return true;
	}
}
