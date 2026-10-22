package clueGame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Board {

    private static Board instance = new Board();

    private Map<String, BoardCell> cells;

    private Board() {
        cells = new HashMap<>();
    }

    public static Board getInstance() {
        return instance;
    }

    public void setConfigFiles(String layout, String setup) {
        // nothing here yet
    }

    public void initialize() {
        // nothing here yet
    }

    public BoardCell getCell(int row, int col) {
        String key = row + "," + col;

        if (!cells.containsKey(key)) {
            cells.put(key, new BoardCell(row, col));
        }

        return cells.get(key);
    }

    public Set<BoardCell> getAdjList(int row, int col) {
        Set<BoardCell> adjList = new HashSet<>();

        return adjList;
    }
}