package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;

//Aaron Nguyễn
//Eknara Dassanayake

/*
 * Adjacency and target tests for our own board (ClueLayout.csv / ClueSetup.txt)
 */
public class BoardAdjTargetTest {
	private static Board board;

	@BeforeAll
	public static void setUp() {
		// Board is singleton, get the only instance and load our config files
		board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		board.initialize();
	}

	// Room centers connect only to their doors, plus a secret passage room if there is one
	@Test
	public void testAdjacencyRoomCenters() {
		// Observatory: two doors and a secret passage to the Bowling Alley
		Set<BoardCell> testList = board.getAdjList(3, 2);
		assertEquals(3, testList.size());
		assertTrue(testList.contains(board.getCell(2, 6)));
		assertTrue(testList.contains(board.getCell(6, 2)));
		assertTrue(testList.contains(board.getCell(21, 23)));

		// Armory: two doors, no secret passage
		testList = board.getAdjList(3, 12);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(2, 8)));
		assertTrue(testList.contains(board.getCell(5, 12)));
	}

	// Room cells that are not the center have no adjacencies
	@Test
	public void testAdjacencyRoomNotCenter() {
		// Observatory edge cell
		assertTrue(board.getAdjList(1, 0).isEmpty());
		// Music Room cell
		assertTrue(board.getAdjList(13, 3).isEmpty());
	}

	// Doorways connect to walkways and the center of the room they enter
	@Test
	public void testAdjacencyDoorways() {
		// Theater door facing RIGHT
		Set<BoardCell> testList = board.getAdjList(12, 19);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(11, 19)));
		assertTrue(testList.contains(board.getCell(13, 19)));
		assertTrue(testList.contains(board.getCell(12, 18)));
		assertTrue(testList.contains(board.getCell(12, 23)));

		// Nursery door facing DOWN
		testList = board.getAdjList(17, 10);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(16, 10)));
		assertTrue(testList.contains(board.getCell(17, 9)));
		assertTrue(testList.contains(board.getCell(17, 11)));
		assertTrue(testList.contains(board.getCell(21, 10)));
	}

	// Walkway surrounded only by walkways
	@Test
	public void testAdjacencyWalkways() {
		Set<BoardCell> testList = board.getAdjList(16, 5);
		assertEquals(4, testList.size());
		assertTrue(testList.contains(board.getCell(15, 5)));
		assertTrue(testList.contains(board.getCell(17, 5)));
		assertTrue(testList.contains(board.getCell(16, 4)));
		assertTrue(testList.contains(board.getCell(16, 6)));
	}

	// Walkways on each edge of the board
	@Test
	public void testAdjacencyEdges() {
		// top edge
		Set<BoardCell> testList = board.getAdjList(0, 7);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(1, 7)));

		// bottom edge
		testList = board.getAdjList(23, 7);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(22, 7)));

		// left edge
		testList = board.getAdjList(7, 0);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(7, 1)));

		// right edge
		testList = board.getAdjList(7, 25);
		assertEquals(1, testList.size());
		assertTrue(testList.contains(board.getCell(7, 24)));
	}

	// Walkway beside room cells that are not doors, between two Armory cells
	@Test
	public void testAdjacencyBesideRoom() {
		Set<BoardCell> testList = board.getAdjList(4, 9);
		assertEquals(2, testList.size());
		assertTrue(testList.contains(board.getCell(4, 8)));
		assertTrue(testList.contains(board.getCell(5, 9)));
		assertFalse(testList.contains(board.getCell(3, 9)));
	}

	/*
	 * TARGET TESTS
	 */

	// Targets along walkways at distances 1 and 2
	@Test
	public void testTargetsWalkway() {
		board.calcTargets(board.getCell(16, 5), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(4, targets.size());
		assertTrue(targets.contains(board.getCell(15, 5)));
		assertTrue(targets.contains(board.getCell(16, 6)));

		board.calcTargets(board.getCell(16, 5), 2);
		targets = board.getTargets();
		assertEquals(6, targets.size());
		assertTrue(targets.contains(board.getCell(16, 3)));
		assertTrue(targets.contains(board.getCell(16, 7)));
		assertTrue(targets.contains(board.getCell(17, 4)));
	}

	// A roll of 2 from above the Nursery door can enter the Nursery
	@Test
	public void testTargetsEnterRoom() {
		board.calcTargets(board.getCell(16, 10), 2);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(7, targets.size());
		assertTrue(targets.contains(board.getCell(21, 10)));
		assertTrue(targets.contains(board.getCell(17, 9)));
		assertTrue(targets.contains(board.getCell(15, 11)));
	}

	// Leaving the Armory, which has no secret passage
	@Test
	public void testTargetsLeaveRoom() {
		board.calcTargets(board.getCell(3, 12), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(2, targets.size());
		assertTrue(targets.contains(board.getCell(2, 8)));
		assertTrue(targets.contains(board.getCell(5, 12)));
	}

	// Leaving the Observatory, which has a secret passage to the Bowling Alley
	@Test
	public void testTargetsLeaveRoomSecretPassage() {
		board.calcTargets(board.getCell(3, 2), 1);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(3, targets.size());
		assertTrue(targets.contains(board.getCell(2, 6)));
		assertTrue(targets.contains(board.getCell(6, 2)));
		assertTrue(targets.contains(board.getCell(21, 23)));
	}

	// Another player blocks one of the walkway targets
	@Test
	public void testTargetsOccupied() {
		board.getCell(16, 6).setOccupied(true);
		board.calcTargets(board.getCell(16, 5), 1);
		board.getCell(16, 6).setOccupied(false);
		Set<BoardCell> targets = board.getTargets();
		assertEquals(3, targets.size());
		assertFalse(targets.contains(board.getCell(16, 6)));
	}
}
