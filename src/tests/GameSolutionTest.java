package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import clueGame.Board;
import clueGame.Card;
import clueGame.CardType;
import clueGame.ComputerPlayer;
import clueGame.HumanPlayer;
import clueGame.Player;
import clueGame.Solution;

//Aaron Nguyễn
//Eknara Dassanayake

public class GameSolutionTest {
	private static Board board;

	// person cards
	private static Card adaCard, graceCard, linusCard, rosaCard, alanCard, charlesCard;
	// weapon cards
	private static Card wrenchCard, ropeCard, ironCard, cableCard, trophyCard, droneCard;
	// room cards
	private static Card observatoryCard, armoryCard, greenhouseCard, theaterCard, nurseryCard, parlorCard;

	// The known answer used by every test: Grace Loop in the Armory with the Rope
	private static Solution answer;

	// Players used by the handleSuggestion tests, recreated before each test
	private HumanPlayer human;
	private ComputerPlayer computer1, computer2, computer3;

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
		armoryCard = new Card("Armory", CardType.ROOM);
		greenhouseCard = new Card("Greenhouse", CardType.ROOM);
		theaterCard = new Card("Theater", CardType.ROOM);
		nurseryCard = new Card("Nursery", CardType.ROOM);
		parlorCard = new Card("Parlor", CardType.ROOM);

		answer = new Solution(armoryCard, graceCard, ropeCard);
	}

	@BeforeEach
	public void setUpPlayers() {
		board.setSolution(answer);

		human = new HumanPlayer("Ada Byte", Color.RED, 0, 7);
		human.updateHand(adaCard);
		human.updateHand(wrenchCard);
		human.updateHand(observatoryCard);

		computer1 = new ComputerPlayer("Linus Stack", Color.GREEN, 7, 0);
		computer1.updateHand(linusCard);
		computer1.updateHand(ironCard);
		computer1.updateHand(greenhouseCard);

		computer2 = new ComputerPlayer("Rosa Cache", Color.YELLOW, 7, 25);
		computer2.updateHand(rosaCard);
		computer2.updateHand(cableCard);
		computer2.updateHand(theaterCard);

		computer3 = new ComputerPlayer("Alan Null", Color.ORANGE, 16, 0);
		computer3.updateHand(alanCard);
		computer3.updateHand(trophyCard);
		computer3.updateHand(nurseryCard);

		ArrayList<Player> players = new ArrayList<>();
		players.add(human);
		players.add(computer1);
		players.add(computer2);
		players.add(computer3);
		board.setPlayers(players);
	}

	// Put the real players and a random answer back for any test class that runs next
	@AfterAll
	public static void tearDown() {
		board.initialize();
	}

	// Accusation with the correct person, weapon and room is true
	@Test
	public void testAccusationCorrect() {
		assertTrue(board.checkAccusation(new Solution(armoryCard, graceCard, ropeCard)));
	}

	// Accusation with the wrong person is false
	@Test
	public void testAccusationWrongPerson() {
		assertFalse(board.checkAccusation(new Solution(armoryCard, linusCard, ropeCard)));
	}

	// Accusation with the wrong weapon is false
	@Test
	public void testAccusationWrongWeapon() {
		assertFalse(board.checkAccusation(new Solution(armoryCard, graceCard, wrenchCard)));
	}

	// Accusation with the wrong room is false
	@Test
	public void testAccusationWrongRoom() {
		assertFalse(board.checkAccusation(new Solution(theaterCard, graceCard, ropeCard)));
	}

	// Player with exactly one matching card returns it, whether it's a person, weapon or room
	@Test
	public void testDisproveOneMatch() {
		// only the person matches (computer1 has Linus Stack)
		assertEquals(linusCard, computer1.disproveSuggestion(new Solution(armoryCard, linusCard, ropeCard)));
		// only the weapon matches (computer1 has Soldering Iron)
		assertEquals(ironCard, computer1.disproveSuggestion(new Solution(armoryCard, graceCard, ironCard)));
		// only the room matches (computer1 has Greenhouse)
		assertEquals(greenhouseCard, computer1.disproveSuggestion(new Solution(greenhouseCard, graceCard, ropeCard)));
	}

	// Player with more than one matching card returns each of them at random
	@Test
	public void testDisproveMultipleMatchesRandom() {
		// computer2 has all three: Theater, Rosa Cache, Ethernet Cable
		Solution suggestion = new Solution(theaterCard, rosaCard, cableCard);
		int roomCount = 0;
		int personCount = 0;
		int weaponCount = 0;

		for (int i = 0; i < 100; i++) {
			Card card = computer2.disproveSuggestion(suggestion);
			if (theaterCard.equals(card)) {
				roomCount++;
			} else if (rosaCard.equals(card)) {
				personCount++;
			} else if (cableCard.equals(card)) {
				weaponCount++;
			} else {
				fail("Returned a card that does not match the suggestion: " + card);
			}
		}

		// each matching card should show up at least once in 100 tries
		assertTrue(roomCount > 0, "Room card was never chosen");
		assertTrue(personCount > 0, "Person card was never chosen");
		assertTrue(weaponCount > 0, "Weapon card was never chosen");
	}

	// Player with no matching cards returns null
	@Test
	public void testDisproveNoMatch() {
		assertNull(computer1.disproveSuggestion(new Solution(armoryCard, graceCard, ropeCard)));
	}


	// Suggestion that no player can disprove returns null
	@Test
	public void testSuggestionNoOneCanDisprove() {
		// these cards are the answer, so nobody holds them
		Solution suggestion = new Solution(armoryCard, graceCard, ropeCard);
		assertNull(board.handleSuggestion(suggestion, computer1));
	}

	// Suggestion that only the suggesting player can disprove returns null
	@Test
	public void testSuggestionOnlySuggesterCanDisprove() {
		// computer2 holds Theater; nobody else holds any of these
		Solution suggestion = new Solution(theaterCard, graceCard, ropeCard);
		assertNull(board.handleSuggestion(suggestion, computer2));
	}

	// Suggestion that only the human can disprove returns the human's card
	@Test
	public void testSuggestionOnlyHumanCanDisprove() {
		// only the human holds the Wrench
		Solution suggestion = new Solution(armoryCard, graceCard, wrenchCard);
		assertEquals(wrenchCard, board.handleSuggestion(suggestion, computer1));
	}

	// Two players can disprove: the first one after the suggester answers
	@Test
	public void testSuggestionTwoCanDisproveFirstAnswers() {
		// computer2 holds Theater and computer3 holds Trophy.
		// computer1 suggests, so computer2 is asked first and answers.
		Solution suggestion = new Solution(theaterCard, graceCard, trophyCard);
		assertEquals(theaterCard, board.handleSuggestion(suggestion, computer1));

		// The human suggests: computer1 is next, then computer2, then computer3.
		// computer1 (Greenhouse) and computer3 (Nursery) can both disprove; computer1 answers.
		suggestion = new Solution(greenhouseCard, graceCard, ropeCard);
		Solution suggestion2 = new Solution(nurseryCard, linusCard, ropeCard);
		assertEquals(greenhouseCard, board.handleSuggestion(suggestion, human));
		// computer1 can disprove with Linus Stack before computer3 is asked about the Nursery
		assertEquals(linusCard, board.handleSuggestion(suggestion2, human));
	}

	// Order wraps around the end of the list back to the start
	@Test
	public void testSuggestionOrderWrapsAround() {
		// computer3 is last, so the human (player 0) is asked next, then computer1.
		// The human (Observatory) and computer1 (Soldering Iron) can both disprove; the human answers.
		Solution suggestion = new Solution(observatoryCard, graceCard, ironCard);
		assertEquals(observatoryCard, board.handleSuggestion(suggestion, computer3));
	}
}
