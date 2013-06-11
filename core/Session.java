package core;

import java.io.*;

import org.schwering.irc.lib.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


import networkreporter.*;

import utils.LinkedListDecorator;

public class Session implements TaskManager {

    final static long CONNECTION_TIMER = 300000; //Check every 5 minutes if connected
    private IRCConnection conn;
    private MainEventHandler eventHandler;

    private NetReporter nr;
    private ModuleManager moduleManager;
    private LinkedList<SessionTask> taskList;
    private SessionCommandManager commandManager;
    private LinkedList<String> channelList;
    private LinkedList<String> adminHosts;
    private String network;
    private int port;
    private String password;
    private String user;
    private String username;
    private String email;
    private Timer connectionTimer;
    private boolean timerOn;
    private final ConcurrentHashMap<String,TimedMonitor> monitors;

    public void addChannel(String channel) {
        synchronized (channelList) {
            channelList.add(channel);
        }
    }

    public void addAdminHost(String host) {
        synchronized (adminHosts) {
            adminHosts.add(host);
        }
    }

    public boolean isAdmin(User user) {
        synchronized (adminHosts) {
            return adminHosts.contains(user.getHost());
        }
    }

    public void assertAdmin(User user) throws PermissionDeniedException {
        if (!isAdmin(user)) {
            throw new PermissionDeniedException();
        }
    }

    public List<String> getChannels() {
        synchronized (channelList) {
            return new LinkedListDecorator<String>(channelList).clone();
        }
    }

    public List<String> getAdminHosts() {
        synchronized (adminHosts) {
            return new LinkedListDecorator<String>(adminHosts).clone();
        }
    }

    public List<SessionTask> getTasks() {
        synchronized (taskList) {
            return new LinkedListDecorator<SessionTask>(taskList).clone();
        }
    }

    public IRCConnection getConnection() {
        return conn;
    }

    public MainEventHandler getEventHandler() {
        return eventHandler;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    

    public SessionCommandManager getCommandManager() {
        return commandManager;
    }

    public Session(String network, int port, String user, String password, String username, String email, String root) {
        if (root.length() > 0 && root.charAt(root.length() - 1) != File.separatorChar) {
            root = root + File.separatorChar;
        }
        this.network = network;
        this.port = port;
        this.password = password;
        this.user = user;
        this.username = username;
        this.email = email;
        monitors = new ConcurrentHashMap<String, TimedMonitor>();

        channelList = new LinkedList<String>();
        adminHosts = new LinkedList<String>();
        taskList = new LinkedList<SessionTask>();

        connectionTimer = new Timer();
        timerOn = false;

        eventHandler = new MainEventHandler(this);
    
    
        


        commandManager = new SessionCommandManager(this);
        eventHandler.add(commandManager);
        moduleManager = new ModuleManager(root, commandManager);


        //eventHandler.add(new ReloadCommand());
        //commandManager.add(new ReloadCommand());

        moduleManager.reload("modules");
        //System.out.println("Loaded " + moduleManager.reload("modules") + " modules");

        setupConn();



    }
    public TimedMonitor getMonitor(String channel) {
        synchronized (monitors) {
        if (monitors.containsKey(channel)) {
        return monitors.get(channel);
        }
        
        TimedMonitor newMonitor = new TimedMonitor();
        monitors.put(channel, newMonitor);
        return newMonitor;
        }
        //return monitor;
    }

    public void removeMonitor(String channel) {
        monitors.remove(channel);
    }
    public Enumeration<String> getMonitoredChannels() {
    return monitors.keys();
    }
    public NetReporter getNetReporter() {
    return nr;
    }
    public synchronized void connect() {
        setupConn();
        try {
            conn.connect();
        } catch (IOException ex) {
            System.err.println(ex);
        }
        if (!timerOn) {

            startConnectionTimer();
        }

    }

    synchronized public void teardown() {
        teardown(null);
    }

    synchronized public void teardown(String quitMsg) {
        stopConnectionTimer();

        killAll();
        conn.removeIRCEventListener(eventHandler);
        if (quitMsg == null) {
            conn.doQuit();
        } else {
            conn.doQuit(quitMsg);
        }
        conn.close();

    }

    private void setupConn() {
        conn = new IRCConnection(network, port, port, password, user, username, email);

        conn.setDaemon(false);
        conn.setColors(false);
        conn.setPong(true);
        conn.setEncoding("UTF-8");
        conn.addIRCEventListener(eventHandler);


    }

    synchronized public void reconnect() {
        conn.removeIRCEventListener(eventHandler);
        conn.doQuit();
        conn.close();



        connect();
    }

    synchronized public void killAll() {
        synchronized (taskList) {
            for (Iterator<SessionTask> i = taskList.iterator(); i.hasNext();) {
                i.next().kill();
            }
        }
    }

    public void msg(String channel, String msg) {
        if (conn.isConnected()) {
            conn.doPrivmsg(channel, msg);
        } else {
            System.err.println("Not connected, cannot send: (" + channel + ") " + msg);
        }
    }

    public void add(SessionTask task) {
        synchronized (taskList) {
            taskList.add(task);
        }
    }

    public void remove(SessionTask task) {
        synchronized (taskList) {
            taskList.remove(task);
        }
    }

    private synchronized void startConnectionTimer() { //Starts a timer that triggers connection check to reconnect
        timerOn = true;
        connectionTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                synchronized (syncObject()) {
                    //System.out.print("Check connection state: ");
                    if (!conn.isConnected()) {
                        //System.out.println("not connected, reconnect");
                        connect();
                    }
                    /*else {
                    System.out.println("okay");
                    }*/
                }

            }
        }, CONNECTION_TIMER, CONNECTION_TIMER);

    }

    private Object syncObject() {
        return this;
    }

    private synchronized void stopConnectionTimer() {

        connectionTimer.cancel();
        connectionTimer.purge();
        timerOn = false;

    }
}
