package clueGame;

public class Room {
    private String name;
    private BoardCell centerCell;
    private BoardCell labelCell;

    public Room(String name) {
        this.name = name;
        centerCell = null;
        labelCell = null;
    }

    public String getName() {
        return name;
    }

    public BoardCell getCenterCell() {
        return centerCell;
    }

    public BoardCell getLabelCell() {
        return labelCell;
    }

    public void setCenterCell(BoardCell centerCell) {
        this.centerCell = centerCell;
    }

    public void setLabelCell(BoardCell labelCell) {
        this.labelCell = labelCell;
    }
}
