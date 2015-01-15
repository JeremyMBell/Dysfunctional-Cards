/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package server;

import frame.Card;
import frame.Deck;
import frame.Player;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jeremy
 */
public class JServer {
    private final ServerSocket server;
    private final int port;
    private final static HashMap<PrintWriter, Player> writers = new HashMap<>();
    private final Deck deck;
    public JServer(int port, Card[] black, Card[] white) throws IOException {
        this.port = port;
        server = new ServerSocket(port);
        deck = new Deck(black, white);
        System.out.println(server.getInetAddress());
        startServer();
    }
    public ServerSocket getServer() {return server;}
    public void startServer() {
        Thread run = new Thread(new Wait());
        run.start();
    }
    /**
     * Looks in the syntax to find the last index that includes the player's name.
     * @param fullString - the string (starting where the name starts)
     * @return Last index that has the player name (supposed to be a ')
     */
    public static int endPlayerName(String fullString) {
        //Looking for a name the player claims to be.
        for (int i = 1; i < fullString.length(); i++) {
            if (fullString.charAt(i) == '\'')
                return i;
        }
        return 0;
    }
    /**
     * Finds a player in the hashmap
     * @param name - The player being seeked
     * @return The Player object of the player, null if it isn't found.
     */
    public static Player findPlayer(String name) {
        for(Player player: writers.values())
            if (player.toString().equals(name))
                return player;
        return null;
    }
    public class Wait implements Runnable {
        private char PLAY_CARDS = 'A', NEW_BLACK = 'B', PICK_WINNER = 'C', TELL_CARDS = 'D', CHOOSE = 'E';
        
        public void deal(Player play, PrintWriter out) {
            out.println("DEALSTART");
            for (int i = 0; i < Player.MAX_CARDS; i++) {
                Card card = deck.takeWhite();
                play.addCard(card);
                out.println(card.toString());
            }
            out.println("DEALEND");
        }
        @Override
        public void run() {
            BufferedReader in;
            PrintWriter out;
            char phase;
            Card blackCard = new Card("", true);
            HashMap<Player, LinkedList<Card>> cardPool = new HashMap<>();
            int cardCzar = 0;
            try {
                Socket socket = server.accept();
                in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                while(true) {
                    out.println("INITIAL");
                    String name = in.readLine();
                    if (name == null)return;
                    synchronized(writers) {
                        if (!writers.containsKey(out)) {
                            Player play = new Player(name);
                            writers.put(out, play);
                            deal(play, out);
                            break;
                        }
                    }
                }
                phase = NEW_BLACK;
                while(true) {
                    String line = in.readLine();
                    if (line == null) return;
                    System.out.println("Client: " + line);
                    if (phase == NEW_BLACK) {
                        //If it's time for a new black card, take the black card from the top.
                        blackCard = deck.takeBlack();
                        cardPool = new HashMap<>();
                        
                        //Tell all the players that there's a new black card.
                        for (PrintWriter write: writers.keySet()){
                            write.println("BLACKCARD" + blackCard.toString() + blackCard.numCardsNeeded());
                        }
                        
                        //Progress the card czar and tell them they are card czar.
                        cardCzar++;
                        if (cardCzar == writers.size()) cardCzar = 0;
                        writers.keySet().toArray(new PrintWriter[writers.size()])[cardCzar].println("CARDCZAR");
                        
                        phase = PLAY_CARDS;
                    }
                    else if (phase == PLAY_CARDS && line.startsWith("PLAY")) {
                        //Syntax of incoming playing cards:
                        //PLAY'<Player_Name>'<Content of the card>
                        //Finds the last index of the name, so we can move on.
                        int i = endPlayerName(line.substring(5));
                        
                        //Finding the player.
                        Player play = findPlayer(line.substring(6, i));
                        
                        //Add the card they played to the card pool.
                        //No more cards will be added than what is on the black card.
                        if (play != null && cardPool.get(play).size() < blackCard.numCardsNeeded()) {
                            cardPool.get(play).add(new Card(line.substring(i + 1), false));
                        }
                    }
                    //Checking if the players have all the cards in.
                    //In that case, start phase of choosing a card.
                    else if (phase == PLAY_CARDS) {
                        boolean allIn = true;
                        for(LinkedList cards:cardPool.values()) {
                            if (cards.size() != blackCard.numCardsNeeded()) {
                                allIn = false;
                                break;
                            }
                        }
                        //All of the cards are in.
                        if (allIn) phase = TELL_CARDS;
                            
                    }
                    else if (phase == TELL_CARDS && line.startsWith("PICKING")) {
                        String name = line.substring(endPlayerName(line.substring(6)));
                        //Find the person and add a point, if they won, break loop
                        for (Player person: cardPool.keySet())
                            if (person.toString().equals(name))
                                if(person.addPoint()) return;
                    }
                    //Tells the players the cards
                    else if (phase == TELL_CARDS) {
                        out.println("PICK");
                        for(Player person: cardPool.keySet())
                            for (Card card: cardPool.get(person))
                                out.println("'" + person + "'" + card);
                        out.println("DONEPICK");
                    }
                }
                
            } catch (IOException ex) {
                Logger.getLogger(JServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
}
