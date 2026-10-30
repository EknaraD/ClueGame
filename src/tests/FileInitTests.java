package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.Room;

public class FileInitTests {

    private static Board board;

    @BeforeAll
    public static void setUp() {
        board = Board.getInstance();
        board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
        board.initialize();
    }

    @Test
    public void testBoardDimensions() {
        assertEquals(24, board.getNumRows());
        assertEquals(26, board.getNumColumns());
    }

    @Test
    public void testRoomNames() {
        assertEquals("Observatory", board.getRoom('O').getName());
        assertEquals("Armory", board.getRoom('A').getName());
        assertEquals("Greenhouse", board.getRoom('G').getName());
        assertEquals("Music Room", board.getRoom('M').getName());
        assertEquals("Theater", board.getRoom('T').getName());
        assertEquals("Wine Cellar", board.getRoom('C').getName());
        assertEquals("Nursery", board.getRoom('N').getName());
        assertEquals("Parlor", board.getRoom('P').getName());
        assertEquals("Bowling Alley", board.getRoom('B').getName());
    }

    @Test
    public void testRoomCenters() {
        BoardCell cell = board.getCell(3, 2);
        assertTrue(cell.isRoomCenter());
        assertEquals("Observatory", board.getRoom(cell).getName());

        cell = board.getCell(3, 12);
        assertTrue(cell.isRoomCenter());
        assertEquals("Armory", board.getRoom(cell).getName());

        cell = board.getCell(4, 22);
        assertTrue(cell.isRoomCenter());
        assertEquals("Greenhouse", board.getRoom(cell).getName());

        cell = board.getCell(11, 2);
        assertTrue(cell.isRoomCenter());
        assertEquals("Music Room", board.getRoom(cell).getName());

        cell = board.getCell(12, 23);
        assertTrue(cell.isRoomCenter());
        assertEquals("Theater", board.getRoom(cell).getName());

        cell = board.getCell(21, 2);
        assertTrue(cell.isRoomCenter());
        assertEquals("Wine Cellar", board.getRoom(cell).getName());

        cell = board.getCell(21, 10);
        assertTrue(cell.isRoomCenter());
        assertEquals("Nursery", board.getRoom(cell).getName());

        cell = board.getCell(21, 16);
        assertTrue(cell.isRoomCenter());
        assertEquals("Parlor", board.getRoom(cell).getName());

        cell = board.getCell(21, 23);
        assertTrue(cell.isRoomCenter());
        assertEquals("Bowling Alley", board.getRoom(cell).getName());
    }

    @Test
    public void testSecretPassages() {
        //observatory to bowling alley
        BoardCell cell = board.getCell(0, 0);
        assertEquals('O', cell.getInitial());
        assertEquals('B', cell.getSecretPassage());

        //bowling alley to observatory
        cell = board.getCell(23, 25);
        assertEquals('B', cell.getInitial());
        assertEquals('O', cell.getSecretPassage());
    }

    @Test
    public void testWalkway() {
        BoardCell cell = board.getCell(7, 0);

        assertTrue(cell.isWalkway());
        assertFalse(cell.isRoomCenter());
        assertEquals("Walkway", board.getRoom(cell).getName());
    }

    @Test
    public void testUnusedSpace() {
        BoardCell cell = board.getCell(0, 6);

        assertEquals('X', cell.getInitial());
        assertFalse(cell.isRoomCenter());
        assertEquals("Unused", board.getRoom(cell).getName());
    }

    @Test
    public void testRoomLookup() {
        Room room = board.getRoom('O');

        assertNotNull(room);
        assertEquals("Observatory", room.getName());

        BoardCell cell = board.getCell(3, 2);
        assertSame(room, board.getRoom(cell));
    }
}
