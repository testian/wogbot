/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package core;
import quakestat.GameServer;
import quakestat.GameServerListener;
import quakestat.Player;
import java.net.InetAddress;
/**
 *
 * @author testi
 */
public class GameServerListenerIRC implements GameServerListener {
private Session session;
private String channel;
    public GameServerListenerIRC(Session session, String channel) {
    this.session = session;
    this.channel = channel;
    }

    public void onGametypeChange(GameServer server, String oldGameType, String newGameType) {
        session.msg(channel, shortDescription(server) + " changed game type from " + oldGameType + " to " + newGameType);
    }

    public void onJoin(GameServer server, Player joinPlayer) {
        session.msg(channel, shortDescription(server) + " Player joined: " + joinPlayer.playerDescription());
    }

    public void onLeave(GameServer server, Player leavePlayer) {
        session.msg(channel, shortDescription(server) + " Player left: " + leavePlayer.playerDescription());
    }

    public void onMapChange(GameServer server, String oldMapName, String newMapName) {
        session.msg(channel, shortDescription(server) + " changed map from " + oldMapName + " to " + newMapName);
    }

    public void onServerNameChange(GameServer server, String oldName, String newName) {
        session.msg(channel, shortDescription(server) + " changed server name from " + oldName + " to " + newName);
    }

    public void onShutdown(GameServer server) {
        session.msg(channel, shortDescription(server) + " is down");
    }

    public void onStart(GameServer server) {
        session.msg(channel, shortDescription(server) + " is up");
    }

    public void onPlayerCountChange(GameServer server, int playersJoined) {
        String joinLeave;
        if (playersJoined>0) {
        joinLeave = "joined";
        }
        else {
        playersJoined = -playersJoined;
        joinLeave = "left";
        }
        session.msg(channel, shortDescription(server) + " " + playersJoined + " players " + joinLeave);
    }

    private String shortDescription(GameServer server) {
    return "Gameserver (" + server.getGameName() + "," + denull(server.getHost()) + ":" + denull(server.getPort()) + "," + denull(server.getName()) + ")";
    }
    private String denull(String serverString) {
    if (serverString == null) return "";
    return serverString;
    }
    private String denull(int serverInt) {
    if (serverInt == -1) return "";
    return Integer.toString(serverInt);
    }
    private String denull(InetAddress address) {
    if (address == null) return "";
    return address.getHostAddress();
    }

}
