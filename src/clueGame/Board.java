
package clueGame;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Board {
    private static Board theInstance = new Board();

    private BoardCell[][] board;
    private int numRows;
    private int numColumns;

    private String layoutConfigFile;
    private String setupConfigFile;

    private Map<Character, Room> roomMap;
    private Set<BoardCell> targets;

    private Board() {
        roomMap = new HashMap<>();
        targets = new HashSet<>();
    }

    public static Board getInstance() {
        return theInstance;
    }

    public void setConfigFiles(String layout, String setup) {
        layoutConfigFile = layout;
        setupConfigFile = setup;
    }

    public void initialize() {
        try {
            roomMap.clear();
            targets.clear();

            loadSetupConfig();
            loadLayoutConfig();

        } catch (BadConfigFormatException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private File findFile(String filename) {
        File file = new File(filename);

        if (file.exists()) {
            return file;
        }

        return new File("data", filename);
    }

    public void loadSetupConfig() throws BadConfigFormatException, FileNotFoundException {

        roomMap.clear();

        Scanner scanner = new Scanner(findFile(setupConfigFile));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            //blank lines and comments
            if (line.isEmpty() || line.startsWith("//")) {
                continue;
            }

            String[] parts = line.split(",");

            if (parts.length != 3) {
                scanner.close();
                throw new BadConfigFormatException(
                    "Invalid setup line: " + line);
            }

            String type = parts[0].trim();
            String name = parts[1].trim();
            String initialText = parts[2].trim();

            if (initialText.length() != 1 || name.isEmpty()) {
                scanner.close();
                throw new BadConfigFormatException(
                    "Invalid setup line: " + line);
            }

            char initial = initialText.charAt(0);

            if (roomMap.containsKey(initial)) {
                scanner.close();
                throw new BadConfigFormatException(
                    "Duplicate room or space initial: " + initial);
            }

            if (type.equalsIgnoreCase("Room") || type.equalsIgnoreCase("Space")) {
                roomMap.put(initial, new Room(name));
            } else {
                scanner.close();
                throw new BadConfigFormatException(
                    "Invalid setup type: " + type);
            }
        }

        scanner.close();

        if (roomMap.isEmpty()) {
            throw new BadConfigFormatException(
                "setup file contains no rooms or spaces");
        }
    }

    public void loadLayoutConfig() throws BadConfigFormatException, FileNotFoundException {

        List<String[]> rows = new ArrayList<>();

        Scanner scanner = new Scanner(findFile(layoutConfigFile));

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (!line.isEmpty()) {
                rows.add(line.split(","));
            }
        }

        scanner.close();

        if (rows.isEmpty()) {
            throw new BadConfigFormatException(
                "layout file is empty");
        }

        numRows = rows.size();
        numColumns = rows.get(0).length;

        if (numColumns == 0) {
            throw new BadConfigFormatException(
                "layout file doesn't have columns");
        }

        board = new BoardCell[numRows][numColumns];

        //every row has the same number of columns?
        for (int row = 0; row < numRows; row++) {
            if (rows.get(row).length != numColumns) {
                throw new BadConfigFormatException(
                    "inconsistent number of columns in layout file");
            }
        }

        //make all board cells
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numColumns; col++) {
                String value = rows.get(row)[col].trim();

                if (value.isEmpty()) {
                    throw new BadConfigFormatException(
                        "Empty cell at row " + row + ", column " + col);
                }

                char initial = value.charAt(0);

                if (!roomMap.containsKey(initial)) {
                    throw new BadConfigFormatException(
                        "Layout contains an undefined initial: " + initial);
                }

                board[row][col] = new BoardCell(row, col, value);
            }
        }

        //find room centers and label cells
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numColumns; col++) {
                BoardCell cell = board[row][col];

                if (cell.isRoomCenter()) {
                    Room room = roomMap.get(cell.getInitial());

                    room.setCenterCell(cell);
                    room.setLabelCell(cell);
                    cell.setLabel(true);
                }
            }
            
        }

        calcAdjacencies();
    }

    private void calcAdjacencies() {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numColumns; col++) {
                board[row][col].clearAdjList();

                Set<BoardCell> adjacencies = getAdjList(row, col);

                for (BoardCell cell : adjacencies) {
                    board[row][col].addAdj(cell);
                }
            }
        }
    }

    public BoardCell getCell(int row, int col) {
        return board[row][col];
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public Room getRoom(char initial) {
        return roomMap.get(initial);
    }

    public Room getRoom(BoardCell cell) {
        return roomMap.get(cell.getInitial());
    }

    public Map<Character, Room> getRoomMap() {
        return roomMap;
    }

    public Set<BoardCell> getAdjList(int row, int col) {
        Set<BoardCell> adjList = new HashSet<>();
        BoardCell current = getCell(row, col);

        //room cells that connect to doorways and secret passages
        if (current.isRoom()) {
            char roomInitial = current.getInitial();

            for (int r = 0; r < numRows; r++) {
                for (int c = 0; c < numColumns; c++) {
                    BoardCell cell = getCell(r, c);

                    if (cell.isDoorway() && doorwayLeadsIntoRoom(r, c, cell.getDoorDirection(), roomInitial)) {
                        adjList.add(cell);
                    }

                    if (cell.getInitial() == roomInitial && cell.getSecretPassage() != ' ') {
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

        //walkway cells that connect to other walkways and doorways
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            if (newRow < 0 || newRow >= numRows || newCol < 0 || newCol >= numColumns) {
                continue;
            }

            BoardCell neighbor = getCell(newRow, newCol);

            if (neighbor.isWalkway()) {
                adjList.add(neighbor);
            } else if (neighbor.isDoorway() && doorwayFacesCell(neighbor.getDoorDirection(), newRow, newCol, row, col)) {
                adjList.add(neighbor);
            }
        }

        return adjList;
    }

    private boolean doorwayFacesCell(DoorDirection direction, int doorRow, int doorCol, int otherRow, int otherCol) {
        switch (direction) {
            case UP:
                return otherRow == doorRow + 1 && otherCol == doorCol;

            case DOWN:
                return otherRow == doorRow - 1 && otherCol == doorCol;

            case LEFT:
                return otherRow == doorRow && otherCol == doorCol + 1;

            case RIGHT:
                return otherRow == doorRow && otherCol == doorCol - 1;

            default:
                return false;
        }
    }

    private boolean doorwayLeadsIntoRoom(
            int row, int col,
            DoorDirection direction,
            char roomInitial) {

        int roomRow = row;
        int roomCol = col;

        switch (direction) {
            case UP:
                roomRow--;
                break;

            case DOWN:
                roomRow++;
                break;

            case LEFT:
                roomCol--;
                break;

            case RIGHT:
                roomCol++;
                break;

            default:
                return false;
        }

        if (roomRow < 0 || roomRow >= numRows || roomCol < 0 || roomCol >= numColumns) {
            return false;
        }

        return getCell(roomRow, roomCol).getInitial() == roomInitial;
    }

    public void calcTargets(BoardCell startCell, int pathLength) {
        targets = new HashSet<>();

        Set<BoardCell> visited = new HashSet<>();
        visited.add(startCell);

        findTargets(startCell, pathLength, visited);
    }

    private void findTargets(
            BoardCell current,
            int steps,
            Set<BoardCell> visited) {

        if (steps == 0) {
            if (current != null && !current.isOccupied()) {
                targets.add(current);
            }

            return;
        }

        for (BoardCell next : current.getAdjList()) {
            if (visited.contains(next) || next.isOccupied()) {
                continue;
            }

            visited.add(next);

            //entering room ends movement
            if (next.isRoom()) {
                targets.add(next);
            } else {
                findTargets(next, steps - 1, visited);
            }

            visited.remove(next);
        }
    }

    public Set<BoardCell> getTargets() {
        return targets;
    }
}
