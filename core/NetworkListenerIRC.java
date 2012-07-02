/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package core;
import java.net.InetAddress;
import networkreporter.HostListener;
import java.util.Enumeration;
/**
 *
 * @author testi
 */
public class NetworkListenerIRC implements HostListener {
    private Session bot;
    public NetworkListenerIRC(Session bot) {
    this.bot = bot;
    }
    public void hostChanged(InetAddress oldAddr, InetAddress newAddr) {
        msg("Local network event - host changed: " + hostToString(oldAddr) + " becomes " + hostToString(newAddr));
    }

    public void hostJoined(InetAddress addr) {
        msg("Local network event - host joined: " + hostToString(addr));
    }

    public void hostLeft(InetAddress addr) {
        msg("Local network event - host left: " + hostToString(addr));
    }

    private String hostToString(InetAddress addr) {
    return addr.getHostName() + "/" + addr.getHostAddress();
    }

    private void msg(String msg) {
    Enumeration<String> channels = bot.getMonitoredChannels();
    while (channels.hasMoreElements()) {
    String channel = channels.nextElement();
    TimedMonitor monitor = bot.getMonitor(channel);
    synchronized (bot) {
    if (monitor.doMonitor()) {
    bot.msg(channel, msg);
    }
     else {
    bot.removeMonitor(channel);
    }
    }
    }

    }


}
