package experiment;

import java.util.HashSet;
import java.util.Set;

public class TestBoardCell {

    private int row, column;
    private boolean room, occupied; 


    private Set<TestBoardCell> adjList;

    public TestBoardCell(int row, int column) {
        this.row = row;
        this.column = column;

        adjList = new HashSet<TestBoardCell>();

        room = false;
        occupied = false;
    }

    public void addAdjacency(TestBoardCell cell) {
        // nothing here yet
        adjList.add(cell); // would be in here when implementing the solution
    }

    public Set<TestBoardCell> getAdjList() {
        return adjList;
    }

    public void setRoom(boolean room) {
        this.room = room;
    }

    public boolean isRoom() {
        return room;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public boolean getOccupied() {
        return occupied;
    }
}