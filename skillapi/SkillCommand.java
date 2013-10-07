package skillapi;

import java.util.List;

import net.minecraft.command.CommandGive;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;

public class SkillCommand extends CommandGive{

	@Override
	public String getCommandName() {
		return "teach";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return getCommandName()+"<username> <skill>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] command) {
		if (command.length == 2)
        {
            EntityPlayerMP entityplayermp = getPlayer(sender, command[0]);
            if (SkillRegistry.isSkillRegistered(command[1]) && !SkillRegistry.isSkillKnown(entityplayermp, command[0]))
            {
            	SkillRegistry.learnSkill(entityplayermp, command[1]);
                notifyAdmins(sender, command[0]+ " learned "+command[1], new Object[] {entityplayermp.getEntityName()});
            }
        }
        else
        {
            throw new WrongUsageException(getCommandUsage(sender), new Object[0]);
        }
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] command)
    {
		if(command.length == 2){
			return getListOfStringsMatchingLastWord(command,(String[]) SkillRegistry.getSkillMap().keySet().toArray());
		}
        return super.addTabCompletionOptions(sender, command);
    }
}
