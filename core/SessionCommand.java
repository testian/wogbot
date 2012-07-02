package core;

import org.schwering.irc.lib.*;

public interface SessionCommand {

	public void execute(Session bot, String channel, User user, String message) throws SessionException;
	public String getName();
	public String getUsage();
}
