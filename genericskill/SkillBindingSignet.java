package genericskill;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;

public class SkillBindingSignet extends SkillGeneric{

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
		return player.inventory.hasItem(GenericSkills.heritageAmulet.itemID);
	}

	@Override
	public boolean onSkillTrigger(EntityPlayer player) {
		if(!canPlayerUseSkill(player))
			return false;
    	if(!player.worldObj.isRemote){
    		if(!player.worldObj.provider.canRespawnHere())
    			player.travelToDimension(0);
	    	ChunkCoordinates chunkcoordinates = player.worldObj.getSpawnPoint();
	    	int posX=chunkcoordinates.posX;
	    	int posY=chunkcoordinates.posY;
	    	int posZ=chunkcoordinates.posZ;
	    	while(!player.worldObj.isAirBlock(posX, posY, posZ))
	    		posY++; //So you don't spawn in the floor.
			player.setPositionAndUpdate((float)posX+0.5F, (float)posY+0.1F, (float)posZ+0.5F);
    	}
		player.inventory.consumeInventoryItem(GenericSkills.heritageAmulet.itemID);
    	player.worldObj.playSoundAtEntity(player, "mob.ghast.fireball", 1.0F, 1.0F);
		return true;
	}
}
