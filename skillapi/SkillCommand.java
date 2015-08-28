package skillapi;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandGive;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;

import java.util.List;

public final class SkillCommand extends CommandGive {
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] command, BlockPos pos) {
		if (command.length == 2) {
			return getListOfStringsMatchingLastWord(command, SkillRegistry.getSkillMap().keySet());
		}
		return super.addTabCompletionOptions(sender, command, pos);
	}

	@Override
	public String getCommandName() {
		return "teach";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender) {
		return "commands.teach.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] command) throws CommandException {
		if (command.length >= 2) {
			EntityPlayerMP entityplayermp = getPlayer(sender, command[0]);
            StringBuilder name = new StringBuilder(command[1]);
            for(int i = 2; i < command.length; i++){
                name.append(" ").append(command[i]);
            }
            String skill = name.toString();
			if (SkillRegistry.isSkillRegistered(skill))
                if(!SkillRegistry.isSkillKnown(entityplayermp, skill)) {
                    if(SkillRegistry.learnSkill(entityplayermp, skill))
                        notifyOperators(sender, this, "commands.teach.skill", entityplayermp.getCommandSenderName(), skill);
                    else
                        throw new WrongUsageException("commands.teach.skill.invalidPlayer", entityplayermp.getCommandSenderName(), skill);
                }else
                    throw new WrongUsageException("commands.teach.skill.known", entityplayermp.getCommandSenderName(), skill);
            else
                throw new WrongUsageException("commands.teach.skill.notFound", skill);
		} else
			throw new WrongUsageException(getCommandUsage(sender));
	}
}
