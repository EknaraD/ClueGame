package clueGame;

import java.util.HashSet;
import java.util.Set;

public class BoardCell {
    private int row;
    private int col;
    private char initial;
    private DoorDirection doorDirection;
    private boolean roomLabel;
    private boolean roomCenter;
    private char secretPassage;
    private Set<BoardCell> adjList;

    private boolean occupied;

    public BoardCell(int row, int col, String value) {
        this.row = row;
        this.col = col;

        initial = value.charAt(0);
        doorDirection = DoorDirection.NONE;
        roomLabel = false;
        roomCenter = false;
        secretPassage = ' ';
        occupied = false;

        adjList = new HashSet<>();

        // Why is this a for loop? -Samah
        if (value.length() > 1) {
            char symbol = value.charAt(1);

            if (symbol == '#') {
                roomLabel = true;
            } else if (symbol == '*') {
                roomCenter = true;
            } else if (symbol == '^') {
                doorDirection = DoorDirection.UP;
            } else if (symbol == 'v') {
                doorDirection = DoorDirection.DOWN;
            } else if (symbol == '<') {
                doorDirection = DoorDirection.LEFT;
            } else if (symbol == '>') {
                doorDirection = DoorDirection.RIGHT;
            } else if(Character.isLetter(symbol)){
                secretPassage = symbol;
            }
        }
    }

    private void setSecretPassage() {
        switch (initial) {
            case 'S':
                secretPassage = 'K';
                break;
            case 'K':
                secretPassage = 'S';
                break;
            case 'O':
                secretPassage = 'C';
                break;
            case 'C':
                secretPassage = 'O';
                break;
            default:
                secretPassage = ' ';
                break;
        }
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getColumn() {
        return col;
    }

    public char getInitial() {
        return initial;
    }

    public DoorDirection getDoorDirection() {
        return doorDirection;
    }

    public boolean isDoorway() {
        return doorDirection != DoorDirection.NONE;
    }

    public boolean isRoomCenter() {
        return roomCenter;
    }

    public boolean isLabel() {
        return roomLabel;
    }

    public void setLabel(boolean roomLabel) {
        this.roomLabel = roomLabel;
    }

    public char getSecretPassage() {
        return secretPassage;
    }

    public boolean isRoom() {
        return initial != 'W' && initial != 'X';
    }

    public boolean isWalkway() {
        return initial == 'W';
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public void addAdj(BoardCell cell) {
        adjList.add(cell);
    }

    public Set<BoardCell> getAdjList() {
        return adjList;
    }

    public void clearAdjList() {
        adjList.clear();
    }
}