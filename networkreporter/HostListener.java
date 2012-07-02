/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package networkreporter;
import java.net.InetAddress;
/**
 *
 * @author Dimitri Nüscheler
 */
public interface HostListener {

    public void hostJoined(InetAddress addr);
    public void hostLeft(InetAddress addr);
    public void hostChanged(InetAddress oldAddr, InetAddress newAddr);
}
