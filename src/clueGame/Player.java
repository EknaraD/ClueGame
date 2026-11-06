package clueGame;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;

import org.jspecify.annotations.Nullable;



public abstract class Player {
    String name; 
    Color color;
    boolean isHuman;
    Point2D start_location; 

    public Player(String name, Color color, boolean isHuman, Point2D start_location){
        this.name = name;
        this.color = color;
        this.isHuman = isHuman;
        this.start_location = start_location;
    }

    public String getName(){
        return name;
    }

    public Color getColor(){
        return color;
    }

    public boolean isHuman(){
        return isHuman;
    }

    public int getStartingLocation(){
        // return start_X, start_y
        return 0;
    }

    public abstract void updateHand(Card card);

    public abstract int getRow();

    public abstract int getColumn();

    protected abstract List<Card> getHand();
}
