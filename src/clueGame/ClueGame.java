package clueGame;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class ClueGame extends JFrame {

    private Board board;
    private BoardPanel boardPanel;
    private GameControlPanel controlPanel;
    private KnownCardsPanel knownCardsPanel;

    public ClueGame() {

        board = Board.getInstance();

        //load board and game information
        board.setConfigFiles("ClueLayout.csv", "ClueSetup.txt");
        board.initialize();

        //find human player
        Player human = null;

        for (Player player : board.getPlayers()) {
            if (player instanceof HumanPlayer) {
                human = player;
                break;
            }
        }

        if (human == null) {
            throw new IllegalStateException("No human player found!");
        }

        //create panels
        boardPanel = new BoardPanel();
        controlPanel = new GameControlPanel();
        knownCardsPanel = new KnownCardsPanel(human, board.getPlayers());

        //JFrame setup
        setTitle("Clue Game - CSCI306");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //add panels
        add(boardPanel, BorderLayout.CENTER);
        add(knownCardsPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        ClueGame game = new ClueGame();
    }
}
