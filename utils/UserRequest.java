package utils;

import org.schwering.irc.lib.*;

import java.util.*;
import core.*;

public class UserRequest implements ReplyListener, SessionTask {
	
	
		boolean ready;
		//boolean usernameAndHost;
		//boolean server;
		String username, hostname;
		String name;
		LinkedList<User> users;
		
		ReplyObservable observable;
		IRCConnection conn;
		TaskManager taskmanager;
		
		public UserRequest(Session bot, String name)
		{
			this(bot,bot.getEventHandler(),bot.getConnection(),name);
		}
		public UserRequest(TaskManager taskmanager, ReplyObservable observable, IRCConnection conn, String name)
		{
		this.name=name;
		ready=false;
		users=new LinkedList<User>();
		username=null;
		hostname=null;
		//servername=null;

		
		
		this.taskmanager=taskmanager;
		this.observable=observable;
		this.conn=conn;
		
		observable.add(this);
		conn.doWhois(name);
		

		
		registerTask();
		}
		public void onReply(int id, String context, String data)
		{
		SimpleStringTokenizer userToken = new SimpleStringTokenizer(context);
		if (id == 311)
		{
			if(!ready)
			{
				userToken.nextToken();
				if (userToken.nextToken().equals(name))
				{
					userToken.nextToken();
					hostname=userToken.nextToken();
					username=data;
					
					synchronized(this){
						ready=true;
						notifyAll();
						deregisterTask();
						}
					observable.remove(this);
				}
			}
		}
		/*else if (id == 312)
		{
			if(!server)
			{
				userToken.nextToken();
				if (userToken.nextToken().equals(name))
				{
					synchronized(servername){servername=userToken.nextToken();}
				}
			}			
		}*/

		}
		public User getUser() throws RequestInterruptedException
		{
			synchronized(this) {
			if (!ready){
				try{
					
					this.wait();

				}
				catch(InterruptedException ex){throw new RequestInterruptedException("User-Anfrage unterbrochen: " + ex.getMessage());}
				if (!ready) {throw new RequestInterruptedException("User-Anfrage unterbrochen");}
			}
			}
			return new IrcLibUserDecorator(new IRCUser(name, username, hostname));
				
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
			return "UserRequest: " + name;
		}

	
}
