package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.HumanPlayer;
import clueGame.Player;
import clueGame.Solution;

/**
 * GameSetupTests: tests loading the players, creating the deck, and dealing.
 *
 * PLAYER SECTION (this file): Player class, HumanPlayer and ComputerPlayer
 * children, loading players from ClueSetup.txt, and the player side of the deal.
 * CARD SECTION: add the Card, CardType, deck, and Solution tests below the
 * marked divider at the bottom of this file.
 *
 * Expected player data comes from these lines in ClueSetup.txt:
 *   Player, Ada Byte, Red, Human, 0, 7
 *   Player, Grace Loop, Blue, Computer, 0, 18
 *   Player, Linus Stack, Green, Computer, 7, 0
 *   Player, Rosa Cache, Yellow, Computer, 7, 25
 *   Player, Alan Null, Orange, Computer, 16, 0
 *   Player, Charles Bit, Magenta, Computer, 23, 20
 */
public class GameSetupTests {
	// Expected data. Change these if you change the players in ClueSetup.txt.
	private static final int NUM_PLAYERS = 6;
	private static final int NUM_HUMAN = 1;
	private static final int NUM_COMPUTER = 5;
	private static final int SOLUTION_SIZE = 3;

	private static Board board;

	/**
	 * Load the board, players, and cards once. Setup and dealing don't change
	 * between tests, so one load is enough (and faster).
	 */
	@BeforeAll
	public static void setUp() {
		board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		board.initialize(); // assumed to also deal; call board.deal() here if yours is separate
	}

	// ------------------------------------------------------------------
	// Requirement: load people from ClueSetup.txt and create 6 players
	// (1 human and 5 computer)
	// ------------------------------------------------------------------

	/** Exactly 6 players are created from the setup file. */
	@Test
	public void testCorrectNumberOfPlayers() {
		assertEquals(NUM_PLAYERS, board.getPlayers().size());
	}

	/** The Human/Computer field in the file creates the right child class. */
	@Test
	public void testOneHumanFiveComputer() {
		int humans = 0;
		int computers = 0;
		for (Player p : board.getPlayers()) {
			if (p instanceof HumanPlayer) {
				humans++;
			} else if (p instanceof ComputerPlayer) {
				computers++;
			}
		}
		assertEquals(NUM_HUMAN, humans);
		assertEquals(NUM_COMPUTER, computers);
	}

	/** The human player is loaded with the right name, color, and start cell. */
	@Test
	public void testHumanPlayerLoaded() {
		Player p = findPlayer("Ada Byte");
		assertTrue(p instanceof HumanPlayer);
		assertEquals(Color.RED, p.getColor());
		assertEquals(0, p.getRow());
		assertEquals(7, p.getColumn());
	}

	/** First computer player in the file is loaded correctly. */
	@Test
	public void testFirstComputerPlayerLoaded() {
		Player p = findPlayer("Grace Loop");
		assertTrue(p instanceof ComputerPlayer);
		assertEquals(Color.BLUE, p.getColor());
		assertEquals(0, p.getRow());
		assertEquals(18, p.getColumn());
	}

	/** Last player in the file is loaded correctly (catches off-by-one in the loader). */
	@Test
	public void testLastComputerPlayerLoaded() {
		Player p = findPlayer("Charles Bit");
		assertTrue(p instanceof ComputerPlayer);
		assertEquals(Color.MAGENTA, p.getColor());
		assertEquals(23, p.getRow());
		assertEquals(20, p.getColumn());
	}

	/** Players are stored in file order, so the human (first line) is first. */
	@Test
	public void testPlayersInFileOrder() {
		List<Player> players = board.getPlayers();
		assertEquals("Ada Byte", players.get(0).getName());
		assertEquals("Charles Bit", players.get(NUM_PLAYERS - 1).getName());
	}

	/** Every player has a non-null color, and no two players share one. */
	@Test
	public void testColorsConvertedAndUnique() {
		List<Player> players = board.getPlayers();
		for (int i = 0; i < players.size(); i++) {
			assertNotNull(players.get(i).getColor());
			for (int j = i + 1; j < players.size(); j++) {
				assertNotEquals(players.get(i).getColor(), players.get(j).getColor());
			}
		}
	}

	/** Every player starts on a walkway cell, and no two start on the same cell. */
	@Test
	public void testStartLocationsValid() {
		List<Player> players = board.getPlayers();
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			assertEquals('W', board.getCell(p.getRow(), p.getColumn()).getInitial(),
					p.getName() + " does not start on a walkway");
			for (int j = i + 1; j < players.size(); j++) {
				Player q = players.get(j);
				assertFalse(p.getRow() == q.getRow() && p.getColumn() == q.getColumn(),
						p.getName() + " and " + q.getName() + " share a start cell");
			}
		}
	}

	// ------------------------------------------------------------------
	// Requirement: Player class with human and computer children
	// (unit tests that don't need the board)
	// ------------------------------------------------------------------

	/** Both child classes can be stored as a Player and keep their data. */
	@Test
	public void testPlayerConstructors() {
		Player human = new HumanPlayer("Test Human", Color.RED, 3, 4);
		Player computer = new ComputerPlayer("Test Computer", Color.BLUE, 5, 6);
		assertEquals("Test Human", human.getName());
		assertEquals(Color.RED, human.getColor());
		assertEquals(3, human.getRow());
		assertEquals(4, human.getColumn());
		assertEquals("Test Computer", computer.getName());
		assertEquals(Color.BLUE, computer.getColor());
		assertEquals(5, computer.getRow());
		assertEquals(6, computer.getColumn());
	}

	/** A new player starts with an empty hand, and updateHand adds cards to it. */
	@Test
	public void testUpdateHand() {
		Player p = new ComputerPlayer("Test Computer", Color.BLUE, 0, 0);
		assertEquals(0, p.getHand().size());
		Card wrench = new Card("Wrench", CardType.WEAPON);
		Card parlor = new Card("Parlor", CardType.ROOM);
		p.updateHand(wrench);
		p.updateHand(parlor);
		assertEquals(2, p.getHand().size());
		assertTrue(p.getHand().contains(wrench));
		assertTrue(p.getHand().contains(parlor));
	}

	// ------------------------------------------------------------------
	// Requirement: deal cards to the players
	// (all cards dealt, roughly equal hands, no card dealt twice).
	// Nothing here hard-codes the deck size, so adding weapons won't break it.
	// ------------------------------------------------------------------

	/** Every player received at least one card. */
	@Test
	public void testEveryPlayerHasCards() {
		for (Player p : board.getPlayers()) {
			assertFalse(p.getHand().isEmpty(), p.getName() + " has no cards");
		}
	}

	/** Hand sizes differ by at most one card. */
	@Test
	public void testHandsRoughlyEqual() {
		int min = Integer.MAX_VALUE;
		int max = 0;
		for (Player p : board.getPlayers()) {
			min = Math.min(min, p.getHand().size());
			max = Math.max(max, p.getHand().size());
		}
		assertTrue(max - min <= 1, "Hand sizes range from " + min + " to " + max);
	}

	/** Every card outside the solution ends up in some player's hand. */
	@Test
	public void testAllCardsDealt() {
		int dealt = 0;
		for (Player p : board.getPlayers()) {
			dealt += p.getHand().size();
		}
		assertEquals(board.getDeck().size() - SOLUTION_SIZE, dealt);
	}

	/** No card appears in more than one hand (or twice in the same hand). */
	@Test
	public void testNoCardDealtTwice() {
		List<Card> seen = new ArrayList<Card>();
		for (Player p : board.getPlayers()) {
			for (Card c : p.getHand()) {
				assertFalse(seen.contains(c), c.getCardName() + " was dealt twice");
				seen.add(c);
			}
		}
	}

	/** No player is holding a solution card. */
	@Test
	public void testHandsExcludeSolution() {
		Solution answer = board.getSolution();
		for (Player p : board.getPlayers()) {
			assertFalse(p.getHand().contains(answer.getRoom()));
			assertFalse(p.getHand().contains(answer.getPerson()));
			assertFalse(p.getHand().contains(answer.getWeapon()));
		}
	}

	// ------------------------------------------------------------------
	// Helper
	// ------------------------------------------------------------------

	/** Returns the loaded player with this name, or fails the test if none exists. */
	private Player findPlayer(String name) {
		for (Player p : board.getPlayers()) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		fail("No player named " + name + " was loaded");
		return null;
	}

	// ==================================================================
	// CARD SECTION: Card, CardType, deck, and Solution tests go below.
	// ==================================================================
	@Test
    public void testCardEquals() {
        // Two cards with the same name and type should be equal.
        Card card1 = new Card("Miss Scarlet", CardType.PERSON);
        Card card2 = new Card("Miss Scarlet", CardType.PERSON);

        assertEquals(card1, card2);
    }
}