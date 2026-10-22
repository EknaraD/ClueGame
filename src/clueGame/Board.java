package clueGame;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Board {

    private static Board instance = new Board();

    private BoardCell[][] board;

    private String layoutConfigFile;
    private String setupConfigFile;

    private int numRows;
    private int numColumns;

    private Set<BoardCell> targets;

    private Board() {
    }

    public static Board getInstance() {
        return instance;
    }

    public void setConfigFiles(String layout, String setup) {
        layoutConfigFile = layout;
        setupConfigFile = setup;
    }

    public void initialize() {
        loadBoard();
    }

    
    private void loadBoard() {
        try {
            Scanner scanner = new Scanner(new File(layoutConfigFile));
            java.util.List<String[]> rows = new java.util.ArrayList<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (!line.isEmpty()) {
                    String[] cells = line.split(",");
                    rows.add(cells);
                }
            }

            scanner.close();

            numRows = rows.size();
            numColumns = rows.get(0).length;
            board = new BoardCell[numRows][numColumns];

            for (int row = 0; row < numRows; row++) {
                String[] cells = rows.get(row);

                if (cells.length != numColumns) {
                    throw new IllegalArgumentException(
                        "Row " + row + " has " + cells.length
                        + " columns; expected " + numColumns);
                }

                for (int col = 0; col < numColumns; col++) {
                    board[row][col] = new BoardCell(row, col, cells[col].trim());
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Could not find layout file");
        }
    }

    public BoardCell getCell(int row, int col) {
        return board[row][col];
    }

    
    public Set<BoardCell> getAdjList(int row, int col) {
        Set<BoardCell> adjList = new HashSet<>();
        BoardCell current = getCell(row, col);

        // room connects from doors or passage
        if (current.isRoom()) {
            char roomInitial = current.getInitial();

            // Find doorways that face this room.
            for (int r = 0; r < numRows; r++) {
                for (int c = 0; c < numColumns; c++) {
                    BoardCell cell = getCell(r, c);

                    if (!cell.isDoorway()) {
                        continue;
                    }

                    int roomRow = r;
                    int roomCol = c;

                    char direction = cell.getDoorDirection();

                    if (direction == '^') {
                        roomRow--;
                    } else if (direction == 'v') {
                        roomRow++;
                    } else if (direction == '<') {
                        roomCol--;
                    } else if (direction == '>') {
                        roomCol++;
                    }

                    if (roomRow >= 0 && roomRow < numRows && roomCol >= 0 && roomCol < numColumns && getCell(roomRow, roomCol).getInitial() == roomInitial) {
                        adjList.add(cell);
                    }
                }
            }

            // Find passage markers anywhere inside this room.
            for (int r = 0; r < numRows; r++) {
                for (int c = 0; c < numColumns; c++) {
                    BoardCell cell = getCell(r, c);

                    if (cell.getInitial() == current.getInitial() && cell.getSecretPassage() != ' ') {
                        char destination = cell.getSecretPassage();

                        for (int rr = 0; rr < numRows; rr++) {
                            for (int cc = 0; cc < numColumns; cc++) {
                                BoardCell destinationCell = getCell(rr, cc);

                                if (destinationCell.getInitial() == destination && destinationCell.getSecretPassage() != ' ') {
                                    adjList.add(destinationCell);
                                }
                            }
                        }
                    }
                }
            }

            return adjList;
        }

        //up, down, left, right
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            if (newRow >= 0 && newRow < numRows && newCol >= 0 && newCol < numColumns) {
                BoardCell neighbor = getCell(newRow, newCol);

                if (neighbor.getInitial() != 'X') {
                    adjList.add(neighbor);
                }
            }
        }

        return adjList;
    }

    public void calcTargets(BoardCell startCell, int pathLength) {
        //nothing here yet
    }

    public Set<BoardCell> getTargets() {
        return targets;
    }
}