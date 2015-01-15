package frame;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.net.ServerSocket;
public class Lobby {
    static final int MIN_PLAYERS = 3;
    private final ArrayList<Player> lobbyMembers;
    private ServerSocket server;
    private PrintWriter serverConsole;
    public Lobby(Player host) {
        lobbyMembers = new ArrayList<>();
        lobbyMembers.add(host);
        try {
            server = new ServerSocket(9797);
            host.setSocket(server.accept());
        }
        catch(IOException e) {
            System.out.println("Server failed.");
        }
        
        
    }
    /**
     * Gives the members of the lobby
     * @return Lobby Members in an array format
     */
    public Player[] getMembers() {return lobbyMembers.toArray(new Player[lobbyMembers.size()]);}
    
    /**
     * Removes a member from the lobby.
     * @param member - Player wishing to leave lobby.
     */
    public void removeMember(Player member){lobbyMembers.remove(member);}
    
    /**
     * Removes a member from the lobby.
     * @param i - Index of player wishing to leave.
     */
    public void removeMember(int i){lobbyMembers.remove(i);}
    
    public void join(Player play) throws IOException{
        play.setSocket(server.accept());
        lobbyMembers.add(play);
        
    }
    public void dealOutput() {
        for (Player member: lobbyMembers)
            member.getOutput().println("WTF this works.");
    }
}
