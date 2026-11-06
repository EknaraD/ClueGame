package clueGame;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class ComputerPlayer extends Player {
    ArrayList<Card> hand;
    public ComputerPlayer(String name, Color color, boolean isHuman, Point2D start_location){
        super(name, color, isHuman, start_location);
        hand = new ArrayList<>();
    }

    @Override 
    public void updateHand(Card card){
        
    }

    @Override
    public int getRow() {
        return (int) start_location.getX();

    }

    @Override
    public int getColumn() {
        return (int) start_location.getY();
    }

    @Override
    protected List<Card> getHand() {
        return hand;
    }
}
