package core;
import org.jdom.*;
import org.jdom.input.*;
import java.io.*;
import java.util.*;

public class SessionMain {
	public static final String SETTINGS_FILE = "bot.xml";
	public static Session wogbot = null;
        public static Object SyncObject = new Object(); //Not nice, encapsulate stuff up here please
        public static void main(String[] args)
	{
                            Runtime.getRuntime().addShutdownHook(new Thread() {
                
               
            public void run() {
                synchronized (SyncObject) {
                if (wogbot != null) {
                wogbot.teardown("Shutdown signal");
                } else {
                System.exit(0);
                }
                }
                }
            });
             
             synchronized (SyncObject) {
		try {
		Document settings = new SAXBuilder().build(SETTINGS_FILE);
		
		Element root = settings.getRootElement();
		Element network = root.getChild("network");
		String host = network.getAttributeValue("host");
		int port = 6667;
		String portString = network.getAttributeValue("port");
		if (portString != null) {
		
		port = Integer.parseInt(portString);
		
		}
		String nickname = network.getChild("nickname").getText();
		String password = network.getChild("password").getText();
		
		String username = network.getChild("username").getText();
		String email = network.getChild("email").getText();
		
		List<String> admins = new LinkedList<String>();
		
		List<Element> adminElements = network.getChildren("admin");
		for (Element t : adminElements) {
			admins.add(t.getText());			
		}
		
		
List<String> channels = new LinkedList<String>();
		
		List<Element> channelElements = network.getChildren("channel");
		for (Element t : channelElements) {
			channels.add(t.getText());			
		}
		
		



		wogbot = new Session(host, port, nickname,password, username,email, "");
		for (String t : admins) {
		wogbot.addAdminHost(t);
		}
		//wogbot.addChannel("#wog");
		for (String t : channels) {
			
			wogbot.addChannel(t);
		}
		//wogbot.addChannel("#java");
		

                wogbot.connect();
		/*utils.RingList<Long> fisch = new utils.RingList<Long>(1);
		for (long i = 0;i<99;i++) {
		fisch.put(i);
		}
		for (Long t : fisch)
		{
			System.out.println(t);
		}*/
		
		} catch (IOException ex) {
			
			System.err.println("Failed to read " + SETTINGS_FILE + ": "  + ex);
		}
		catch (JDOMException ex) {
			
			System.err.println("Failed to parse " + SETTINGS_FILE + ": "  + ex);
		} catch (NumberFormatException ex) {
			System.err.println("Failed to parse " + SETTINGS_FILE + ": "  + ex);
			
		}
		catch (NullPointerException ex) {
			System.err.println("Failed to parse " + SETTINGS_FILE + ": Missing mandatory entries");
		}
	}
        }
	
	
}
