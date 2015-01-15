package frame;
 
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Deck {
    private Queue<Card> blackCards;
    private Queue<Card> whiteCards;
    public Deck(Card[] black, Card[] white) {
        blackCards = new LinkedList<>(Arrays.asList(black));
        whiteCards = new LinkedList<>(Arrays.asList(white));
    }
    /**
     * Adds a black card to the bottom of the deck.
     * @param card - A black card to be added.
     */
    public void addBlack(Card card) {blackCards.add(card);}
    /**
     * Adds a white card to the bottom of the deck.
     * @param card A white card to be added.
     */
    public void addWhite(Card card) {whiteCards.add(card);}
    /**
     * Gives the top black card.
     * @return The top black card.
     */
    public Card getBlack() {return blackCards.peek();}
    
    /**
     * Takes the top black card.
     * @return The top black card.
     */
    public Card takeBlack() {return blackCards.remove();}
    /**
     * Gives the top white card.
     * @return The top white card.
     */
    public Card getWhite() {return whiteCards.peek();}
    /**
     *  Takes the top white card.
     * @return The top white card.
     */
    public Card takeWhite() {return whiteCards.remove();}
    
    /**
     * Gives the deck of white cards in an array.
     * @return Deck of white cards - array form.
     */
    public Card[] getWhiteCards() {
        return whiteCards.toArray(new Card[whiteCards.size()]);
    }
    /**
     * Gives the deck of black cards in an array.
     * @return Deck of black cards - array form.
     */
    public Card[] getBlackCards() {
        return whiteCards.toArray(new Card[whiteCards.size()]);
    }
    
    /**
     * Shuffles the deck in.
     * @param deck The deck to be added and shuffled.
     */
    public void joinDeck(Deck deck) {
        for (Card blackCard:deck.getBlackCards())
            blackCards.add(blackCard);
        for (Card whiteCard:deck.getWhiteCards())
            whiteCards.add(whiteCard);
        shuffle();
    }
    
    public void shuffle() {
        Card[] blacks = blackCards.toArray(new Card[whiteCards.size()]);
        Card[] whites = whiteCards.toArray(new Card[whiteCards.size()]);
        
        //Randomly switches two cards each iteration
        for (int i = 0; i < blacks.length / 2; i++) {
            int i1 = (int) Math.floor(Math.random() * whites.length);
            int i2 = (int) Math.floor(Math.random() * whites.length);
            Card temp = blacks[i1];
            blacks[i1] = whites[i2];
            blacks[i2] = temp;
        }
        
        //Same as above
        for(int i = 0; i < whites.length / 2; i++) {
            int i1 = (int) Math.floor(Math.random() * whites.length);
            int i2 = (int) Math.floor(Math.random() * whites.length);
            Card temp = whites[i1];
            whites[i1] = whites[i2];
            whites[i2] = temp;
        }
        blackCards = new LinkedList(Arrays.asList(blacks));
        whiteCards = new LinkedList(Arrays.asList(whites));
    }
    /**
     * Loads a deck based on a directory.
     * @param folderLocation The folder containing the black and white cards
     * @return A deck with these cards.
     */
    public static Deck PackLoader(File folderLocation) {
        
        //If folder doesn't exist return an empty deck
        if (!folderLocation.isDirectory()) return new Deck(new Card[0], new Card[0]);
        File black = new File(folderLocation.getAbsolutePath() + "black");
        File white = new File(folderLocation.getAbsolutePath() + "white");
        Deck returnDeck = new Deck(new Card[0], new Card[0]);
        
        //If there's a black file, investigate.
        if (black.exists() && black.canRead()) {
            try {
                for(String line:Files.readAllLines(black.toPath()))
                    returnDeck.addBlack(new Card(line, true));//Add to deck
            }
            catch(Exception e){System.out.println("Error loading black cards.");}
        }
        
        //If there's a white file, investigate.
        if (white.exists() && white.canRead()) {
            try {
                for (String line:Files.readAllLines(white.toPath()))
                    returnDeck.addWhite(new Card(line, false));//Add to deck
            }
            catch (Exception e){System.out.println("Error loading white cards.");}
        }
        return returnDeck;
    }
    
    /**
     * Makes a card pack.
     * @param black List of black cards by String to be packed.
     * @param white List of white cards by String to be packed.
     * @param location Folder to pack to.
     * @return True if pack was created without hiccups (problems printed in console).
     */
    public static boolean makePack(LinkedList<String> black, LinkedList<String> white, File location) {
        
        //Initializing Strings
        String blackFile = "";
        String whiteFile = "";
        
        //Adding file content, not encoded...
        for (String blackCard: black)
            blackFile += blackCard + "\n";
        for (String whiteCard:white)
            whiteFile += whiteCard + "\n";
        
        //Makes the folder to write to
        location.mkdir();
        
        //Checks if we created the packs without problems.
        boolean succeed = true;
        
        //Writes to the Path\white file
        if (whiteFile.length() > 0) {
            File whitePath = new File(location.getAbsolutePath() + "white");
            
            //Try to write otherwise, tell the user there is an error.
            try {
                whitePath.createNewFile();
                FileWriter whiteFW = new FileWriter(whitePath.getAbsolutePath());
                BufferedWriter whiteBW = new BufferedWriter(whiteFW);
                whiteBW.write(whiteFile.substring(0, whiteFile.length() - 1));
            }
            catch(IOException e) {
                System.out.println("IOException: Failure creating White Cards");
                succeed = false;//We didn't succeed in creating the white pack.
            }
        }
        
        //Writes to Path\black file - same as white file.
        if (blackFile.length() > 0) {
            File blackPath = new File(location.getAbsolutePath() + "black");
            try {
                blackPath.createNewFile();
                FileWriter blackFW = new FileWriter(blackPath.getAbsolutePath());
                BufferedWriter blackBW = new BufferedWriter(blackFW);
                blackBW.write(blackFile.substring(0, blackFile.length() - 1));
            }
            catch (IOException e) {
                System.out.println("IOException: Failure creating Black Cards");
                succeed = false;
            }
        }
        
        //Returns if the black and white were written without problems.
        return succeed;
    }
    
}
