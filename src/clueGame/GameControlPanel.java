
package clueGame;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class GameControlPanel extends JPanel {

    private JTextField theTurn;
    private JTextField theRoll;
    private JTextField theGuess;
    private JTextField theGuessResult;

    // Constructor for the panel
    public GameControlPanel() {

        // Main panel: 2 rows
        setLayout(new GridLayout(2, 1));

        //TOP ROW:
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 4));

        // First section: Whose turn?
        JPanel turnPanel = new JPanel();
        turnPanel.setLayout(new GridLayout(2, 1));

        JLabel turnLabel = new JLabel("Whose turn?");
        theTurn = new JTextField();

        theTurn.setEditable(false);

        turnPanel.add(turnLabel);
        turnPanel.add(theTurn);

        // Second section: Roll
        JPanel rollPanel = new JPanel();
        rollPanel.setLayout(new GridLayout(1, 2));

        JLabel rollLabel = new JLabel("Roll:");
        theRoll = new JTextField();

        theRoll.setEditable(false);

        rollPanel.add(rollLabel);
        rollPanel.add(theRoll);

        // Third section: Make accusation button
        JButton accusation = new JButton("Make Accusation");

        // Fourth section: Next button
        JButton next = new JButton("NEXT!");

        topPanel.add(turnPanel);
        topPanel.add(rollPanel);
        topPanel.add(accusation);
        topPanel.add(next);

        //BOTTOM ROW:
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 2));

        // Guess section
        JPanel guessPanel = new JPanel();
        guessPanel.setBorder(new TitledBorder(new EtchedBorder(), "Guess"));

        theGuess = new JTextField();
        theGuess.setEditable(false);

        guessPanel.add(theGuess);

        // Guess result section
        JPanel resultPanel = new JPanel();
        resultPanel.setBorder(new TitledBorder(new EtchedBorder(), "Guess Result"));

        theGuessResult = new JTextField();
        theGuessResult.setEditable(false);

        resultPanel.add(theGuessResult);

        bottomPanel.add(guessPanel);
        bottomPanel.add(resultPanel);

        // Add both main rows
        add(topPanel);
        add(bottomPanel);
    }

    // Updates whose turn it is and the dice roll
    public void setTurn(Player player, int roll) {
        theTurn.setText(player.getName());
        theTurn.setBackground(player.getColor());
        theRoll.setText(Integer.toString(roll));
    }

    // Updates the guess text
    public void setGuess(String guess) {
        theGuess.setText(guess);
    }

    // Updates the guess result text
    public void setGuessResult(String result) {
        theGuessResult.setText(result);
    }

    //Main to test the panel
    public static void main(String[] args) {

        GameControlPanel panel = new GameControlPanel();

        JFrame frame = new JFrame();
        frame.setContentPane(panel);
        frame.setSize(750, 180);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Test filling in the data
        panel.setTurn(new ComputerPlayer("Grace Loop", Color.BLUE, 0, 18), 5);
        panel.setGuess("I have no guess!");
        panel.setGuessResult("So you have nothing?");
    }
}
