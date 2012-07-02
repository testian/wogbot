package core;
import org.schwering.irc.lib.*;
public interface MsgListener {

	public void onMessage(String channel, IRCUser user, String message);

}
