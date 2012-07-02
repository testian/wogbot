package core;

import java.util.*;
import org.schwering.irc.lib.*;

import utils.*;

public class SessionCommandManager implements MsgListener {

	private char prefix;

	private char delimiter;

	private Hashtable<String, SessionCommand> commands;

	Session bot;

	public SessionCommandManager(Session bot) {
		this(bot, '!', ' ');
	}

	public SessionCommandManager(Session bot, char prefix, char delimiter) {
		this.prefix = prefix;
		this.delimiter = delimiter;
		commands = new Hashtable<String, SessionCommand>();
		this.bot = bot;
	}

	synchronized public void add(SessionCommand command) {
		commands.put(command.getName(), command);
	}
	synchronized public SessionCommand getCommand(String commandName)
	{
		return commands.get(commandName);
	}
	synchronized public List<SessionCommand> getCommands()
	{
		LinkedList<SessionCommand> commandList = new LinkedList<SessionCommand>();
		for (SessionCommand t : commands.values())
		{
			commandList.add(t);
		}
		return commandList;
	}

	synchronized public void remove(String commandName) {
		commands.remove(commandName);
	}
	synchronized public void clear()
	{
		commands.clear();
	}

	synchronized public void onMessage(String channel, IRCUser user, String message) {

		SimpleStringTokenizer commandLine = new SimpleStringTokenizer(message, delimiter);
		if (commandLine.hasMoreTokens()) {
			String firstWord = commandLine.nextToken();
			if (firstWord.charAt(0) == prefix) { //Length >0 guaranteed by hasMoreTokens
				
				SessionCommand toExecute = commands.get(firstWord.substring(1));
				
				/*for (String t : commands.keySet())
				{
				System.out.println("Command: " + t);	
				}*/
				if (toExecute != null) {

					class ExecuteCommand extends Thread {
						Session bot;

						String channel;

						User user;

						String message;

						SessionCommand toExecute;

						ExecuteCommand(Session bot, String channel, IRCUser user,
								String message, SessionCommand toExecute) {
							this.bot = bot;
							
                                                        if (channel.equals(bot.getConnection().getNick())) {
                                                        this.channel = user.getNick();
                                                        } else {
                                                        this.channel = channel;
                                                        }
							this.user = new IrcLibUserDecorator(user);
							this.message = message;
							this.toExecute = toExecute;
						}

						public void run() {
							try {

                                                            toExecute.execute(bot, channel, user, message);
							} catch (SessionException ex) {
								bot.msg(channel, ex.getMessage());
							}
						}
					}

					ExecuteCommand execThread = new ExecuteCommand(bot, channel,
							user, commandLine.remainingString(), toExecute);
					execThread.start();

				}
			}
		}

	}

	public char getPrefix() {
		return prefix;
	}

	public char getDelimiter() {
		return delimiter;
	}

}
