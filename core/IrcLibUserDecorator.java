/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package core;
import org.schwering.irc.lib.IRCUser;
/**
 *
 * @author testi
 */
public class IrcLibUserDecorator implements User {
IRCUser decorate;

public IrcLibUserDecorator(IRCUser user) {
this.decorate = user;
}
    public String toString() {
        return decorate.toString();
    }

    public String getUsername() {
        return decorate.getUsername();
    }

    public String getServername() {
        return decorate.getServername();
    }

    public String getNick() {
        return decorate.getNick();
    }

    public String getHost() {
        return decorate.getHost();
    }

}
