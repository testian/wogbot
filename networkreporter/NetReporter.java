/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package networkreporter;
import java.util.Set;
import java.util.HashSet;
import java.net.InetAddress;
import java.util.List;
/**
 *
 * @author Dimitri Nüscheler
 */
abstract public class NetReporter {
private Set<HostListener> listeners;
public NetReporter() {
listeners = new HashSet<HostListener>();
}

    public boolean removeListener(Object o) {
        return listeners.remove(o);
    }

    public boolean addListener(HostListener e) {
        return listeners.add(e);
    }
    protected void notifyJoin(InetAddress addr) {
    for (HostListener h : listeners) {
    h.hostJoined(addr);
    }
    }

    protected void notifyLeave(InetAddress addr) {
    for (HostListener h : listeners) {
    h.hostLeft(addr);
    }
    }

    protected void notifyChange(InetAddress oldAddr, InetAddress newAddr) {
    for (HostListener h : listeners) {
    h.hostChanged(oldAddr, newAddr);
    }
    }
    public abstract void scan();
    public abstract List<InetAddress> onlineHosts();

}
