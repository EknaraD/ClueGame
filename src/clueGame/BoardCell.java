
package clueGame;

public class BoardCell {
    private int row, column;
    private char initial;
    private char secretPassage, doorDirection;
    private boolean roomCenter, occupied;

    public BoardCell(int row, int column, String val) {
        this.row = row;
        this.column = column;
        initial = val.charAt(0);
        
        secretPassage = ' ';
        doorDirection = ' ';
        
        roomCenter = false;
        occupied = false;

        for (int i = 1; i < val.length(); i++) {
            char symbol = val.charAt(i);

            if (symbol == '#') {
                roomCenter = true;
            } else if (symbol == '*') {
                switch (initial) {
                    case 'S': 
                        secretPassage = 'K'; // Study to Kitchen
                        break;
                    case 'K': 
                        secretPassage = 'S'; // Kitchen to Study
                        break;
                    case 'O': 
                        secretPassage = 'C'; // Lounge to Conservatory
                        break;
                    case 'C': 
                        secretPassage = 'O'; // Conservatory to Lounge
                        break;
                    default: 
                        secretPassage = ' ';
                        break;
                }
            } else if (symbol == '^' || symbol == 'v' || symbol == '<' || symbol == '>') {
                doorDirection = symbol;
            }
        }
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public char getInitial() {
        return initial;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public boolean isRoom() {
        return initial != 'W' && initial != 'X';
    }

    public boolean isWalkway() {
        return initial == 'W';
    }

    public boolean isRoomCenter() {
        return roomCenter;
    }

    public boolean isDoorway() {
        return doorDirection != ' ';
    }

    public char getDoorDirection() {
        return doorDirection;
    }

    public char getSecretPassage() {
        return secretPassage;
    }
}
