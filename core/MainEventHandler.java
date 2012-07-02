package core;
import java.util.*;
import org.schwering.irc.lib.*;
import utils.*;

public class MainEventHandler implements IRCEventListener, MsgObservable, ReplyObservable {

	LinkedList<MsgListener> msglisteners;
	LinkedList<ReplyListener> replylisteners;
	Session bot;
	

	
	public MainEventHandler(Session bot)
	{
	this.bot=bot;
	msglisteners = new LinkedList<MsgListener>();
	replylisteners = new LinkedList<ReplyListener>();
	}
	public void add(MsgListener msglistener)
	{
	synchronized (msglisteners) {
	if (msglisteners.contains(msglistener)){return;}
		
		msglisteners.add(msglistener);
	}
		
	}
	public void remove(MsgListener msglistener)
	{
		synchronized (msglisteners) {
		msglisteners.remove(msglistener);
		}
	}
	
	
	public void add(ReplyListener replylistener)
	{
	synchronized (replylisteners) {
	if (replylisteners.contains(replylistener)){return;}
		
		replylisteners.add(replylistener);
	}
		
	}
	public void remove(ReplyListener replylistener)
	{
		synchronized (replylisteners) {
			replylisteners.remove(replylistener);
		}
	}
	
	
	
	
	public void onDisconnected() {
		// TODO Auto-generated method stub
		//System.out.println("Disconnected");
	bot.reconnect();
	}

	public void onError(String arg0) {
		// TODO Auto-generated method stub
		//System.err.println("Error: " + arg0);
	}

	public void onError(int arg0, String arg1) {
		// TODO Auto-generated method stub
		//System.err.println("Error: " + arg0);
		//System.err.println("       " + arg1);
	}

	public void onInvite(String arg0, IRCUser arg1, String arg2) {
		// TODO Auto-generated method stub
//		System.out.println("Invited by " + arg1 + " to " + arg2);
	}

	public void onJoin(String arg0, IRCUser arg1) {
		// TODO Auto-generated method stub
//		System.out.println(arg1 + " joined " + arg0);
	}

	public void onKick(String arg0, IRCUser arg1, String arg2, String arg3) {
	if (arg2.equals(bot.getConnection().getNick()) && bot.getChannels().contains(arg3)) {
        bot.getConnection().doPrivmsg("chanserv", "clear " + arg3 + " users");
        bot.getConnection().doPrivmsg("chanserv", "clear " + arg3 + " bans");
        bot.getConnection().doJoin(arg3);
        }

	}

	public void onMode(String arg0, IRCUser arg1, IRCModeParser arg2) {
		// TODO Auto-generated method stub
		//System.out.println("Mode1");
	}

	public void onMode(IRCUser arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
//		System.out.println("Mode2");

	}

	public void onNick(IRCUser arg0, String arg1) {
		// TODO Auto-generated method stub
	//	System.out.println("Nickchange");
	}

	public void onNotice(String arg0, IRCUser arg1, String arg2) {
		// TODO Auto-generated method stub
		//System.out.println("Notice");
	}

	public void onPart(String arg0, IRCUser arg1, String arg2) {
		// TODO Auto-generated method stub
		//System.out.println("Part");
	}

	public void onPing(String arg0) {
		// TODO Auto-generated method stub

	}

	public void onPrivmsg(String arg0, IRCUser arg1, String arg2) {
		// TODO Auto-generated method stub
		List<MsgListener> copy;
		synchronized (msglisteners) {
			copy = new LinkedListDecorator<MsgListener>(msglisteners).clone();
		}
		
		for (Iterator<MsgListener> i = copy.iterator();i.hasNext();)
		{
		i.next().onMessage(arg0,arg1, arg2);
		}
		
		/*class MsgEventThread extends Thread
		{
			String channel;
			IRCUser user;
			String message;
			List<MsgListener> listeners;
			MsgEventThread(String channel, IRCUser user, String message, List<MsgListener> listeners)
			{
				this.channel=channel;
				this.user=user;
				this.message=message;
				this.listeners=listeners;
			}
			public void run()
			{
				for (Iterator<MsgListener> i = listeners.iterator();i.hasNext();)
				{
				i.next().onMessage(channel,user, message);
				}
			}
		}*/
		
		//MsgEventThread notifier = new MsgEventThread(arg0, arg1, arg2, copy);
		//notifier.start();
	}

	public void onQuit(IRCUser arg0, String arg1) {
		// TODO Auto-generated method stub
//		System.out.println("Quit");
	}

	public void onRegistered() {
		// TODO Auto-generated method stub
	//	System.out.println("Registered");
		//for (Iterator<String> i = bot.getChannels().iterator();i.hasNext();)
		for (String t: bot.getChannels())
		{
			//conn.doJoin(i.next());
			bot.getConnection().doJoin(t);
		}
	}

	public void onReply(int arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
	//	System.out.println("Reply");
	//System.out.println("Reply: " + arg0 + "\nReplyString1: " + arg1 + "\nReplyString2: " + arg2);
	
	List<ReplyListener> copy;
	synchronized (replylisteners) {
		copy = new LinkedListDecorator<ReplyListener>(replylisteners).clone();
	}

	for (Iterator<ReplyListener> i = copy.iterator();i.hasNext();)
	{
	i.next().onReply(arg0, arg1, arg2);
	}
	
	
	}

	public void onTopic(String arg0, IRCUser arg1, String arg2) {
		// TODO Auto-generated method stub
		//System.out.println("Topic change");
	}

	public void unknown(String arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		//System.out.println("UnknownEvent");
	}

}
