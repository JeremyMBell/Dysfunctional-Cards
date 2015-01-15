/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dysfunctional.cards;

import Includes.CardPack;
import frame.Card;
import frame.Player;
import java.io.IOException;
import server.JClient;
import server.JServer;

/**
 *
 * @author Jeremy
 */
public class DysfunctionalCards {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Card[] whiteCards = new Card[CardPack.DEFAULT_WHITE_CARDS.length];
        for (int i = 0; i < whiteCards.length; i++)
            whiteCards[i] = new Card(CardPack.DEFAULT_WHITE_CARDS[i], false);
        Card[] blackCards = new Card[CardPack.DEFAULT_BLACK_CARDS.length];
        for (int i = 0; i < blackCards.length; i++)
            blackCards[i] = new Card(CardPack.DEFAULT_BLACK_CARDS[i], false);
        try {
            JServer server = new JServer(9797, blackCards, whiteCards);
            
        }catch(IOException e) {
            
        }
    }
    
}
