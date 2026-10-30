
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

    public void loadLayoutConfig()
            throws BadConfigFormatException, FileNotFoundException {

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
                Room room = roomMap.get(cell.getInitial());
                
                if (cell.isRoomCenter()) {
                    room.setCenterCell(cell);
                }
                if (cell.isLabel()) {
                    room.setLabelCell(cell);
                    cell.setLabel(true);
                }
            }
        }

        calcAdjacencies();
    }

    //only walkways and room centers get adjacencies, all other cells stay empty
    private void calcAdjacencies() {
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numColumns; col++) {
                board[row][col].clearAdjList();
            }
        }

        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numColumns; col++) {
                BoardCell cell = board[row][col];

                if (cell.isWalkway()) {
                    addWalkwayAdjacencies(cell);
                }

                //secret passage connects this room's center to the other room's center
                if (cell.getSecretPassage() != ' ') {
                    BoardCell center = getRoom(cell).getCenterCell();
                    center.addAdj(getRoom(cell.getSecretPassage()).getCenterCell());
                }
            }
        }
    }

    private void addWalkwayAdjacencies(BoardCell cell) {
        int row = cell.getRow();
        int col = cell.getCol();

        //walkways connect to adjacent walkways (doors are walkways too)
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] direction : directions) {
            int newRow = row + direction[0];
            int newCol = col + direction[1];

            if (newRow < 0 || newRow >= numRows || newCol < 0 || newCol >= numColumns) {
                continue;
            }

            BoardCell neighbor = getCell(newRow, newCol);

            if (neighbor.isWalkway()) {
                cell.addAdj(neighbor);
            }
        }

        //doors connect both ways with the center of the room they open into
        if (cell.isDoorway()) {
            BoardCell center = getRoom(getDoorTarget(cell)).getCenterCell();
            cell.addAdj(center);
            center.addAdj(cell);
        }
    }

    //the room cell a door opens into
    private BoardCell getDoorTarget(BoardCell door) {
        int row = door.getRow();
        int col = door.getCol();

        switch (door.getDoorDirection()) {
            case UP:
                return getCell(row - 1, col);
            case DOWN:
                return getCell(row + 1, col);
            case LEFT:
                return getCell(row, col - 1);
            case RIGHT:
                return getCell(row, col + 1);
            default:
                return null;
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

    //adjacencies are built once in calcAdjacencies()
    public Set<BoardCell> getAdjList(int row, int col) {
        return getCell(row, col).getAdjList();
    }

    public void calcTargets(BoardCell startCell, int pathLength) {
        targets = new HashSet<>();

        Set<BoardCell> visited = new HashSet<>();
        visited.add(startCell);

        findTargets(startCell, pathLength, visited);
    }

    private void findTargets(BoardCell current, int steps, Set<BoardCell> visited) {

        for (BoardCell neighbor : current.getAdjList()) {
            if (visited.contains(neighbor)) {
                continue;
            }

            //entering a room ends movement, even if another player is in it
            if (neighbor.isRoomCenter()) {
                targets.add(neighbor);
                continue;
            }

            //players block walkways
            if (neighbor.isOccupied()) {
                continue;
            }

            if (steps == 1) {
                targets.add(neighbor);
            } else {
                visited.add(neighbor);
                findTargets(neighbor, steps - 1, visited);
                visited.remove(neighbor);
            }
        }
    }

    public Set<BoardCell> getTargets() {
        return targets;
    }
}