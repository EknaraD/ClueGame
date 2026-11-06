package clueGame;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;



public abstract class Player {
    private String name; 
    private Color color;
    private int row;
    private int col;  
    private ArrayList<Card> hand;

    public Player(String name, Color color, int row, int col){
        this.name = name;
        this.color = color;
        this.row = row;
        this.col = col;
        this.hand = new ArrayList<>();
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

    public abstract void updateHand(Card card);

}
