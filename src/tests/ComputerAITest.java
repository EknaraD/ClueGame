package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.BoardCell;
import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.Solution;

//Aaron Nguyễn
//Eknara Dassanayake


public class ComputerAITest {
	// number of times to repeat random choices; enough that missing a choice is very unlikely
	private static final int TRIALS = 200;

	private static Board board;

	// person cards
	private static Card adaCard, graceCard, linusCard, rosaCard, alanCard, charlesCard;
	// weapon cards
	private static Card wrenchCard, ropeCard, ironCard, cableCard, trophyCard, droneCard;
	// room cards
	private static Card observatoryCard, nurseryCard, parlorCard;

	@BeforeAll
	public static void setUp() {
		board = Board.getInstance();
		board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
		board.initialize();

		adaCard = new Card("Ada Byte", CardType.PERSON);
		graceCard = new Card("Grace Loop", CardType.PERSON);
		linusCard = new Card("Linus Stack", CardType.PERSON);
		rosaCard = new Card("Rosa Cache", CardType.PERSON);
		alanCard = new Card("Alan Null", CardType.PERSON);
		charlesCard = new Card("Charles Bit", CardType.PERSON);

		wrenchCard = new Card("Wrench", CardType.WEAPON);
		ropeCard = new Card("Rope", CardType.WEAPON);
		ironCard = new Card("Soldering Iron", CardType.WEAPON);
		cableCard = new Card("Ethernet Cable", CardType.WEAPON);
		trophyCard = new Card("Trophy", CardType.WEAPON);
		droneCard = new Card("Drone Propeller", CardType.WEAPON);

		observatoryCard = new Card("Observatory", CardType.ROOM);
		nurseryCard = new Card("Nursery", CardType.ROOM);
		parlorCard = new Card("Parlor", CardType.ROOM);
	}


	// A computer player standing on the Observatory center at (3,2)
	private ComputerPlayer playerInObservatory() {
		return new ComputerPlayer("Grace Loop", Color.BLUE, 3, 2);
	}

	// The suggested room is the room the player is in
	@Test
	public void testSuggestionRoomMatchesLocation() {
		ComputerPlayer player = playerInObservatory();
		Solution suggestion = player.createSuggestion();
		assertEquals(observatoryCard, suggestion.getRoom());
	}

	// Only one person and one weapon not seen: those are suggested.
	// Cards in the hand count as seen, so some are in the hand and some in the seen list.
	@Test
	public void testSuggestionOnlyOneUnseen() {
		ComputerPlayer player = playerInObservatory();
		player.updateHand(adaCard);
		player.updateHand(wrenchCard);
		player.updateSeen(graceCard);
		player.updateSeen(linusCard);
		player.updateSeen(rosaCard);
		player.updateSeen(alanCard);
		player.updateSeen(ropeCard);
		player.updateSeen(ironCard);
		player.updateSeen(cableCard);
		player.updateSeen(trophyCard);
		// left unseen: Charles Bit and Drone Propeller

		Solution suggestion = player.createSuggestion();
		assertEquals(observatoryCard, suggestion.getRoom());
		assertEquals(charlesCard, suggestion.getPerson());
		assertEquals(droneCard, suggestion.getWeapon());
	}

	// Several persons and weapons not seen: each is picked at random, and seen cards never are
	@Test
	public void testSuggestionMultipleUnseenRandom() {
		ComputerPlayer player = playerInObservatory();
		player.updateHand(adaCard);
		player.updateHand(wrenchCard);
		player.updateSeen(graceCard);
		player.updateSeen(linusCard);
		player.updateSeen(rosaCard);
		player.updateSeen(ropeCard);
		player.updateSeen(ironCard);
		player.updateSeen(cableCard);
		// left unseen: Alan Null, Charles Bit, Trophy, Drone Propeller

		int alanCount = 0;
		int charlesCount = 0;
		int trophyCount = 0;
		int droneCount = 0;

		for (int i = 0; i < TRIALS; i++) {
			Solution suggestion = player.createSuggestion();

			if (alanCard.equals(suggestion.getPerson())) {
				alanCount++;
			} else if (charlesCard.equals(suggestion.getPerson())) {
				charlesCount++;
			} else {
				fail("Suggested a person that was already seen: " + suggestion.getPerson());
			}

			if (trophyCard.equals(suggestion.getWeapon())) {
				trophyCount++;
			} else if (droneCard.equals(suggestion.getWeapon())) {
				droneCount++;
			} else {
				fail("Suggested a weapon that was already seen: " + suggestion.getWeapon());
			}
		}

		assertTrue(alanCount > 0, "Alan Null was never suggested");
		assertTrue(charlesCount > 0, "Charles Bit was never suggested");
		assertTrue(trophyCount > 0, "Trophy was never suggested");
		assertTrue(droneCount > 0, "Drone Propeller was never suggested");
	}


	// No rooms in the target list: every target is chosen at random
	@Test
	public void testSelectTargetNoRooms() {
		// a roll of 2 from (16,5) reaches 6 walkway cells and no rooms
		ComputerPlayer player = new ComputerPlayer("Grace Loop", Color.BLUE, 16, 5);
		board.calcTargets(board.getCell(16, 5), 2);
		Set<BoardCell> targets = new HashSet<>(board.getTargets());
		assertEquals(6, targets.size());

		Map<BoardCell, Integer> counts = countSelections(player, targets);
		// each of the 6 walkway targets is picked at least once
		assertEquals(6, counts.size());
	}

	// A room in the list that has not been seen is always chosen
	@Test
	public void testSelectTargetUnseenRoom() {
		// a roll of 2 from (16,10) reaches the Nursery center (21,10) and 6 walkways
		ComputerPlayer player = new ComputerPlayer("Grace Loop", Color.BLUE, 16, 10);
		board.calcTargets(board.getCell(16, 10), 2);
		Set<BoardCell> targets = new HashSet<>(board.getTargets());
		assertTrue(targets.contains(board.getCell(21, 10)));

		for (int i = 0; i < TRIALS; i++) {
			assertEquals(board.getCell(21, 10), player.selectTarget(targets));
		}
	}

	// Two unseen rooms in the list: one of them is chosen at random, never a walkway
	@Test
	public void testSelectTargetTwoUnseenRooms() {
		ComputerPlayer player = new ComputerPlayer("Grace Loop", Color.BLUE, 16, 10);
		Set<BoardCell> targets = new HashSet<>();
		targets.add(board.getCell(16, 5));   // walkway
		targets.add(board.getCell(21, 10));  // Nursery center
		targets.add(board.getCell(21, 16));  // Parlor center

		Map<BoardCell, Integer> counts = countSelections(player, targets);
		assertFalse(counts.containsKey(board.getCell(16, 5)), "Chose a walkway over an unseen room");
		assertTrue(counts.containsKey(board.getCell(21, 10)), "Nursery was never chosen");
		assertTrue(counts.containsKey(board.getCell(21, 16)), "Parlor was never chosen");
	}

	// A room in the list that has been seen: every target, including the room, is chosen at random
	@Test
	public void testSelectTargetSeenRoom() {
		ComputerPlayer player = new ComputerPlayer("Grace Loop", Color.BLUE, 16, 10);
		player.updateSeen(nurseryCard);
		board.calcTargets(board.getCell(16, 10), 2);
		Set<BoardCell> targets = new HashSet<>(board.getTargets());
		assertEquals(7, targets.size());

		Map<BoardCell, Integer> counts = countSelections(player, targets);
		// all 7 targets (6 walkways and the Nursery) are picked at least once
		assertEquals(7, counts.size());
	}

	// A room card in the player's hand counts as seen, so the room is not forced
	@Test
	public void testSelectTargetRoomInHand() {
		ComputerPlayer player = new ComputerPlayer("Grace Loop", Color.BLUE, 16, 10);
		player.updateHand(nurseryCard);
		board.calcTargets(board.getCell(16, 10), 2);
		Set<BoardCell> targets = new HashSet<>(board.getTargets());

		Map<BoardCell, Integer> counts = countSelections(player, targets);
		assertEquals(7, counts.size());
	}

	/**
	 * Calls selectTarget TRIALS times and counts how often each cell is chosen.
	 * Fails right away if a choice is null or not in the target list.
	 */
	private Map<BoardCell, Integer> countSelections(ComputerPlayer player, Set<BoardCell> targets) {
		Map<BoardCell, Integer> counts = new HashMap<>();
		for (int i = 0; i < TRIALS; i++) {
			BoardCell choice = player.selectTarget(targets);
			assertNotNull(choice, "selectTarget returned null");
			assertTrue(targets.contains(choice), "selectTarget returned a cell not in the target list");
			counts.merge(choice, 1, Integer::sum);
		}
		return counts;
	}
}
