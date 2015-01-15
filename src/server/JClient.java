package server;

import frame.Card;
import frame.Player;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import javax.swing.JFrame;

/**
 *
 * @author Jeremy
 */
public class JClient extends JFrame {
    private Socket socket;
    private Player player;
    private static char DEAL_PHASE = 'A', PLAY_PHASE = 'B', CARD_CZAR = 'C', CARD_CZAR_PICK = 'D', PICK = 'E', NO_PHASE = 'Z';
    private boolean cardCzar = false;
    public JClient (Player player, String ip, int port) {
        super("Dysfunctional Cards");
        this.player = player;
        connect(ip, port);
    }
    public void connect(String ip, int port) {
        char phase = NO_PHASE;
        try {
            socket = new Socket(ip, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Card blackCard;
            HashMap<String, LinkedList<Card>> cardPool = new HashMap<>();
            while(true) {
                String line = in.readLine();
                if (line == null) return;
                System.out.println("Server: " + line);
                //Server is asking for a name.
                if (line.equals("INITIAL")) out.println(player.toString());
                
                //Server wants to deal cards to player.
                else if (line.equals("DEALSTART")) phase = DEAL_PHASE;
                
                //Server is done dealing cards to player.
                else if (line.equals("DEALEND")) phase = NO_PHASE;
                //You're card czar. Just wait.
                else if (line.equals("CARDCZAR")) phase = CARD_CZAR;
                //Server has a black card, time to respond.
                else if (line.startsWith("BLACKCARD")) {
                    
                    //Generating black card with the following syntax.
                    //BLACKCARD<Card Content><Number of Cards to play on black>
                    blackCard = new Card(line.substring(8, line.length() - 1),//General content of the card
                            true,//This is a black card
                            (short) Integer.parseInt("" + line.charAt(line.length() - 1)));//How many cards to place
                    
                    //If the player isn't the card czar, play cards.
                    if (phase != CARD_CZAR) {
                        phase = PLAY_PHASE;
                        for (int i = 0; i < blackCard.numCardsNeeded(); i++) {
                            out.println("PLAY'" + player.toString() + "'" + player.getCard(i));
                            player.removeCard(i);
                        }
                    }
                }
                //If card czar, send into card czar picking phase
                else if (phase == CARD_CZAR && line.startsWith("PICK"))
                    phase = CARD_CZAR_PICK;
                //If normal player, send into card picking phase
                else if (line.startsWith("PICK")) {
                    phase = PICK;
                    cardPool = new HashMap<>();
                }
                
                //Done printing stream of cards, card czar will now pick.
                else if (line.startsWith("DONEPICK")) {
                    if (phase == CARD_CZAR_PICK) {
                        out.println("PICKING'" + cardPool.keySet().toArray()[0] +"'");
                    }
                }
                //Read out the cards
                else if (phase == PICK || phase == CARD_CZAR_PICK) {
                    //Card syntax:
                    //'<Player Name>'<Content of card>
                    int i = JServer.endPlayerName(line);
                    String playerName = line.substring(1, i);
                    
                    //Put a card to this hashmap
                    if (!cardPool.containsKey(playerName)) {
                        cardPool.put(playerName, new LinkedList<>());
                    }
                    cardPool.get(playerName).add(new Card(line.substring(i + 1), false));
                    
                }
                
                //Server is giving a card to the player.
                else if (phase == DEAL_PHASE)
                    player.addCard(new Card(line, false));
                
                
                
            }
        } catch(IOException e) {
            System.out.println("Socket failed to connect.");
        }
    }
    
}
