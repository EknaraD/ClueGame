package clueGame;

import java.io.File;
import java.io.FileNotFoundException;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
    private ArrayList<Player> players;
    private ArrayList<Card> deck;

    private Solution solution;

    private Random random = new Random();
    private Board() {
        roomMap = new HashMap<>();
        targets = new HashSet<>();
        players = new ArrayList<>();
        deck = new ArrayList<>();
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
            players.clear();
            deck.clear();

            loadSetupConfig();
            loadLayoutConfig();
            deal();

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

    /**
     * Reads the setup file. Each line is a Room, Space, Player, or Weapon.
     * Rooms and spaces go in roomMap, players go in players, and every
     * Room, Player, and Weapon line also adds a card to the deck.
     */
    public void loadSetupConfig() throws BadConfigFormatException, FileNotFoundException {
        roomMap.clear();
        players.clear();
        deck.clear();

        Scanner scanner = new Scanner(findFile(setupConfigFile));
       
        try {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                // skip blank lines and comments
                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }

                String[] parts = line.split(",");
                String type = parts[0].trim();

                // use equalsIgnoreCase, not ==, to compare Strings
                if (type.equalsIgnoreCase("Room") || type.equalsIgnoreCase("Space")) {
                    loadRoomLine(parts, line, type.equalsIgnoreCase("Room"));
                } else if (type.equalsIgnoreCase("Player")) {
                    loadPlayerLine(parts, line);
                } else if (type.equalsIgnoreCase("Weapon")) {
                    loadWeaponLine(parts, line);
                } else {
                    throw new BadConfigFormatException("Invalid setup type: " + type);
                }
            }
        } finally {
            // runs whether or not an exception was thrown, so the file always closes
            scanner.close();
        }

        if (roomMap.isEmpty()) {
            throw new BadConfigFormatException("setup file contains no rooms or spaces");
        }
    }


    // Room/Space line format: Room, name, initial
    private void loadRoomLine(String[] parts, String line, boolean isRoom) throws BadConfigFormatException {
        if (parts.length != 3) {
            throw new BadConfigFormatException("Invalid setup line: " + line);
        }

        String name = parts[1].trim();
        String initialText = parts[2].trim();

        if (initialText.length() != 1 || name.isEmpty()) {
            throw new BadConfigFormatException("Invalid setup line: " + line);
        }

        char initial = initialText.charAt(0);

        if (roomMap.containsKey(initial)) {
            throw new BadConfigFormatException("Duplicate room or space initial: " + initial);
        }

        roomMap.put(initial, new Room(name));

        // only real rooms are cards; Walkway and Unused are not
        if (isRoom) {
            deck.add(new Card(name, CardType.ROOM));
        }
    }

    // Player line format: Player, name, color, Human/Computer, start row, start col
    private void loadPlayerLine(String[] parts, String line) throws BadConfigFormatException {
        if (parts.length != 6) {
            throw new BadConfigFormatException("Invalid setup line: " + line);
        }

        String name = parts[1].trim();
        Color color = convertColor(parts[2]);
        String humanOrComputer = parts[3].trim();
        int row;
        int col;

        try {
            row = Integer.parseInt(parts[4].trim());
            col = Integer.parseInt(parts[5].trim());
        } catch (NumberFormatException e) {
            throw new BadConfigFormatException("Bad start location: " + line);
        }

        if (name.isEmpty()) {
            throw new BadConfigFormatException("Player has no name: " + line);
        }

        if (humanOrComputer.equalsIgnoreCase("Human")) {
            players.add(new HumanPlayer(name, color, row, col));
        } else if (humanOrComputer.equalsIgnoreCase("Computer")) {
            players.add(new ComputerPlayer(name, color, row, col));
        } else {
            throw new BadConfigFormatException("Player must be Human or Computer: " + line);
        }

        //every player also represents a person card
        deck.add(new Card(name, CardType.PERSON));
    }

    //Weapon line format: Weapon, name
    private void loadWeaponLine(String[] parts, String line)
            throws BadConfigFormatException {

        if (parts.length != 2 || parts[1].trim().isEmpty()) {
            throw new BadConfigFormatException("Invalid setup line: " + line);
        }

        String name = parts[1].trim();

        deck.add(new Card(name, CardType.WEAPON));
    }

    // helper function to decode the color name to a Color object
    private Color convertColor(String colorName) throws BadConfigFormatException {
        switch (colorName.trim().toLowerCase()) {
            case "red":
                return Color.RED;
            case "blue":
                return Color.BLUE;
            case "green":
                return Color.GREEN;
            case "yellow":
                return Color.YELLOW;
            case "orange":
                return Color.ORANGE;
            case "magenta":
                return Color.MAGENTA;
            case "pink":
                return Color.PINK;
            case "cyan":
                return Color.CYAN;
            case "white":
                return Color.WHITE;
            case "black":
                return Color.BLACK;
            case "gray":
                return Color.GRAY;
            default:
                throw new BadConfigFormatException("Unknown player color: " + colorName);
        }
    }

    /**
     * Selects one room, one person, and one weapon
     * for the solution. Deals all remaining cards
     * to the players.
     */
    public void deal() {

        solution = null;

        // Clear all player hands before dealing
        for (Player player : players) {
            player.clearHand();
        }

        // Make sure we have players and all three card types
        if (players.isEmpty() || !hasCardType(CardType.ROOM) || !hasCardType(CardType.PERSON) || !hasCardType(CardType.WEAPON)) {
            return;
        }

        //copy of the deck
        ArrayList<Card> cardsToDeal = new ArrayList<>(deck);

        //randomly select one card of each type
        Card room = pickRandom(cardsToDeal, CardType.ROOM);
        Card person = pickRandom(cardsToDeal, CardType.PERSON);
        Card weapon = pickRandom(cardsToDeal, CardType.WEAPON);

        //store the three cards in the solution
        solution = new Solution(room, person, weapon);

        //remove solution cards from the cards being dealt
        cardsToDeal.remove(room);
        cardsToDeal.remove(person);
        cardsToDeal.remove(weapon);

        // Shuffle the remaining cards
        Collections.shuffle(cardsToDeal, random);

        // Deal cards one at a time to each player
        for (int i = 0; i < cardsToDeal.size(); i++) {
            players.get(i % players.size()).updateHand(cardsToDeal.get(i));
        }
    }

    // Checks whether the deck contains a card of a certain type
    private boolean hasCardType(CardType type) {

        for (Card card : deck) {
            if (card.getCardType() == type) {
                return true;
            }
        }

        return false;
    }

    // Returns a random card of the requested type
    private Card pickRandom(List<Card> cards, CardType type) {

        ArrayList<Card> matchingCards = new ArrayList<>();

        for (Card card : cards) {
            if (card.getCardType() == type) {
                matchingCards.add(card);
            }
        }

        return matchingCards.get(random.nextInt(matchingCards.size()));
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

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public Solution getSolution() {
        return solution;
    }

}