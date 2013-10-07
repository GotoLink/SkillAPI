package genericskill;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SkillBarrage extends SkillGeneric{

	Random rand = new Random();

	@Override
	public String getDescription() {
		return "Fires a shower of up to 5\narrows all at once. Its strength\nand spread is affected by your\nlevel.\n(requires a bow and at least 2\narrows)";
	}

	@Override
	public int getManaCost(EntityPlayer player) {
		return 10;
	}

	@Override
	public float getChargeupTime(EntityPlayer player) {
		return 2F;
	}

	@Override
	public float getCooldownTime(EntityPlayer player) {
		return 10;
	}

	@Override
	public boolean canPlayerUseSkill(EntityPlayer player) {
		int arrowCount=0;
		ItemStack[] playerInv = player.inventory.mainInventory;
		for(int slot=0;slot<playerInv.length;slot++)
            if(playerInv[slot] != null && playerInv[slot].itemID == Item.arrow.itemID)
                arrowCount+=playerInv[slot].stackSize;
		return arrowCount>1;
	}

	@Override
	public boolean onSkillTrigger(EntityPlayer player) {
		if(!canPlayerUseSkill(player)) //Check again just in case the player threw his arrows away while charging.
			return false;

		World world = player.worldObj;
		float shotStrength = (player.experienceLevel+1)/1.7F;
		world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F/(rand.nextFloat() * 0.4F + 1.2F)+0.5F);
		
		for (int i=0;i<5;i++)
			if(player.inventory.consumeInventoryItem(Item.arrow.itemID) && !world.isRemote)		
				world.spawnEntityInWorld(new EntityArrow(world, player, shotStrength-(rand.nextFloat()*rand.nextFloat()*rand.nextFloat())));
		return true;
	}
}
