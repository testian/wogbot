package staticmodules;



import core.*;

public class ReloadCommand implements SessionCommand {

	public String getUsage() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName()
	{
		return "reload";
	}

	public void execute(Session bot, String channel, User user, String message) throws PermissionDeniedException {
		// TODO Auto-generated method stub
		bot.assertAdmin(user);
		int moduleCount = bot.getModuleManager().reload("modules");
		bot.msg(channel, "Loaded " + moduleCount + " modules");
	}

	
	
}
