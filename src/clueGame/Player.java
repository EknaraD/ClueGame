package clueGame;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;



public abstract class Player {
    private String name; 
    private Color color;
    private int row;
    private int col;  
    private ArrayList<Card> hand;
    private Set<Card> seenCards;
    private Random random;

    public Player(String name, Color color, int row, int col){
        this.name = name;
        this.color = color;
        this.row = row;
        this.col = col;
        this.hand = new ArrayList<>();
        this.seenCards = new HashSet<>();
        random = new Random();
    }

    public String getName(){
        return name;
    }

    public Color getColor(){
        return color;
    }

    public int getRow() {
        return row;

    }

    public int getColumn() {
        return col;
    }

    public  List<Card> getHand() {
        return hand;
    }

    public void updateHand(Card card){
        hand.add(card);
    }

    public void clearHand() {
        hand.clear();
    }

    public Set<Card> getSeenCards() {
        return seenCards;
    }

    public void updateSeen(Card seenCard) {
        seenCards.add(seenCard);
    }

    // returns a random item from a non-empty list (protected so ComputerPlayer can use it)
    protected <T> T pickRandom(List<T> items) {
        return items.get(random.nextInt(items.size()));
    }

    // returns a card from the hand that matches the suggestion (random if more than one), or null
    public Card disproveSuggestion(Solution suggestion) {
        ArrayList<Card> matchingCards = new ArrayList<>();
        for (Card card : hand) {
            if (card.equals(suggestion.getPerson()) || card.equals(suggestion.getRoom()) || card.equals(suggestion.getWeapon())) {
                matchingCards.add(card);
            }
        }
        if (matchingCards.isEmpty()) {
            return null;
        }
        return pickRandom(matchingCards);
    }

}
