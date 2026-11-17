package clueGame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ComputerPlayer extends Player {
    public ComputerPlayer(String name, Color color, int row, int col){
        super(name, color, row, col);
    }

    // suggests the room the player is in, plus a random person and weapon it hasn't seen
    public Solution createSuggestion() {
        Board board = Board.getInstance();
        BoardCell location = board.getCell(getRow(), getColumn());
        Card room = roomCard(board, location);

        Card person = pickRandom(unseenCards(board, CardType.PERSON));
        Card weapon = pickRandom(unseenCards(board, CardType.WEAPON));

        return new Solution(room, person, weapon);
    }

    // picks an unseen room if one is reachable (random if more than one), otherwise any random target
    public BoardCell selectTarget(Set<BoardCell> targets) {
        Board board = Board.getInstance();
        List<BoardCell> unseenRooms = new ArrayList<>();

        for (BoardCell target : targets) {
            if (target.isRoomCenter() && !hasSeen(roomCard(board, target))) {
                unseenRooms.add(target);
            }
        }

        if (!unseenRooms.isEmpty()) {
            return pickRandom(unseenRooms);
        }
        return pickRandom(new ArrayList<>(targets));
    }

    // cards of this type in the deck that are not in the hand or the seen list
    private List<Card> unseenCards(Board board, CardType type) {
        List<Card> unseen = new ArrayList<>();
        for (Card card : board.getDeck()) {
            if (card.getCardType() == type && !hasSeen(card)) {
                unseen.add(card);
            }
        }
        return unseen;
    }

    // a card in the hand counts as seen
    private boolean hasSeen(Card card) {
        return getHand().contains(card) || getSeenCards().contains(card);
    }

    // the room card for the room this cell is in
    private Card roomCard(Board board, BoardCell cell) {
        return new Card(board.getRoom(cell).getName(), CardType.ROOM);
    }
}
