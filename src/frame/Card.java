package frame;
import java.awt.Point;
import java.util.LinkedList;
public class Card {
    
    /**
     * Is this a black card?
     */
    private final boolean isBlack;
    
    /**
     * Is the card selected?
     */
    private boolean selected = false;
    
    /**
     * How many cards are needed to be played for this card?
     */
    private short cardsPlay;
    
    /**
     * What does the card say?
     */
    private final String text;
    
    /**
     * this.text, but wrapped for drawing.
     */
    private final LinkedList<String> wrappedText = new LinkedList<>();
    
    /**
     * What is the width and height of each SpriteSheet block?
     */
    public static final int CARD_WIDTH = 300, CARD_HEIGHT = 500;
   
    /**
     * Where is the card located in the window?
     */
    private final Point location;
    
    public Card(String content, boolean isBlackCard) {
        isBlack = isBlackCard;
        cardsPlay = 0;
        if (isBlack) {
            
            //Reading the black card line.
            //Syntax: Begins with Tabs (# of Tabs = number of cards to play) Then card content.
            for (short i = 0; i < content.length(); i++) {
                //There's no more tabs, so stop.
                if (!("" + content.charAt(i)).equals("\t")) {
                    cardsPlay = i;
                    break;
                }
            }
        }
        text = content.substring(cardsPlay);//Getting rid of tabs (if any)
        //We were not given a location.
        location = new Point(0, 0);
    }
    public Card(String content, boolean isBlackCard, short numNeeded) {
        //Making a card that doesn't have formatting
        isBlack = isBlackCard;
        if (isBlack) this.cardsPlay = numNeeded;
        else this.cardsPlay = 0;
        text = content;
        location = new Point(0, 0);
    }
    @Override
    public String toString() {return text;}
    
    /**
     * Gives the number of cards needed to be played for this card.
     * @return Number of Cards needed to play.
     */
    public short numCardsNeeded() {return cardsPlay;}
    
    /**
     * Tells if the card is a black card.
     * @return True if black.
     */
    public boolean isBlack() {return isBlack;}
    
    /**
     * Selects card for selection.
     */
    public void select() {
        if (!isBlack && !selected) {
            selected = true;
        }
    }
    
    /**
     * Makes a card no longer selected.
     */
    public void deselect() {
        if (!isBlack && selected) {
            selected = false;
        }
    }

}
