package utils;

import java.util.*;
import org.schwering.irc.lib.*;
import core.*;


public class ChannelRequest implements ReplyListener, SessionTask {
	private static char[] prefixChars = {'*','!','@','%','+'};
	boolean ready;
	String name;
	LinkedList<String> names;
	ReplyObservable observable;
	IRCConnection conn;
	TaskManager taskmanager;
	
	public ChannelRequest(Session bot, String name)
	{
		this(bot,bot.getEventHandler(),bot.getConnection(),name);
	}
	public ChannelRequest(TaskManager taskmanager, ReplyObservable observable, IRCConnection conn, String name)
	{
	this.name=name;
	ready=false;
	names=new LinkedList<String>();
	this.taskmanager=taskmanager;
	this.observable=observable;
	this.conn=conn;
	
	observable.add(this);
	conn.doNames(name);
	registerTask();
	}
    public void onReply(int replyId, String context, String data)
	{
		
		if (replyId == 353 && context.equals(conn.getNick() + " = " + name)) {
			//System.out.println("Creating list now");
			synchronized(names)
		{
				//System.out.println("Entered names monitor");
			if (ready){return;}
			SimpleStringTokenizer nickTokens = new SimpleStringTokenizer(data);
		while (nickTokens.hasMoreTokens())
		{
			
			
			names.add(stripPrefix(nickTokens.nextToken()));
		}
		
		synchronized(this) {
		ready=true;

		
		this.notifyAll();
		deregisterTask();
		}


		}
		observable.remove(this);
		}
	}
	public List<String> getNames() throws RequestInterruptedException
	{
		synchronized(this) {
		if (!ready){
		
			try {
			wait();
			}catch(InterruptedException ex) {
				throw new RequestInterruptedException("Channel-Anfrage unterbrochen: " + ex.getMessage());
				}
			if (!ready){throw new RequestInterruptedException("Channel-Anfrage unterbrochen");}
		
			
		}
		}
		synchronized(names)
		{
			return new LinkedListDecorator<String>(names).clone();
		}
	}
	private String stripPrefix(String nickname)
	{
		if (nickname.length()>0)
		{	
		for (int i = 0;i<prefixChars.length;i++)
		{
			if (prefixChars[i] == nickname.charAt(0))
			{
				return nickname.substring(1);
			}
		}
		}
		return nickname;
	}
	public void kill()
	{
		deregisterTask();
		observable.remove(this);
		synchronized(this){
			this.notifyAll();
		}
		
	}
	public void exit()
	{
		kill();
	}
	private void deregisterTask()
	{
		taskmanager.remove(this);
	}
	private void registerTask()
	{
		taskmanager.add(this);
	}
	public String getDescription()
	{
		return "ChannelRequest: " + name;
	}
}
