package genericskill;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public class SkillBindingSignet extends SkillGeneric {
	@Override
	public String getDescription() {
		return "Teleports you back to your\nroots.\n(requires a Heritage Amulet)";
	}

	@Override
	public int getManaCost(EntityPlayer player) {
		return 0;
	}

	@Override
	public float getChargeupTime(EntityPlayer player) {
		return 10;
	}

	@Override
	public float getCooldownTime(EntityPlayer player) {
		return 90;
	}

	@Override
	public boolean canPlayerUseSkill(EntityPlayer player) {
		return player.inventory.hasItem(GenericSkills.heritageAmulet);
	}

	@Override
	public boolean onSkillTrigger(EntityPlayer player) {
		if (!canPlayerUseSkill(player))
			return false;
		if (!player.worldObj.isRemote) {
			if (!player.worldObj.provider.canRespawnHere())
				player.travelToDimension(0);
			BlockPos pos = player.worldObj.getSpawnPoint();
			while (!player.worldObj.isAirBlock(pos))
				pos = pos.up(); //So you don't spawn in the floor.
			player.setPositionAndUpdate(pos.getX() + 0.5F, pos.getY() + 0.1F, pos.getZ() + 0.5F);
		}
		player.inventory.consumeInventoryItem(GenericSkills.heritageAmulet);
		player.worldObj.playSoundAtEntity(player, "mob.ghast.fireball", 1.0F, 1.0F);
		return true;
	}
}
