package clueGame;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class KnownCardsPanel extends JPanel {

    private Player human;
    private List<Player> players;

    private JPanel personPanel;
    private JPanel roomPanel;
    private JPanel weaponPanel;

    // Constructor
    public KnownCardsPanel(Player human, List<Player> players) {
        this.human = human;
        this.players = players;

        // Main panel layout: 3 rows stacked vertically with "Known Cards" border
        setLayout(new GridLayout(3, 1));
        setBorder(new TitledBorder(new EtchedBorder(), "Known Cards"));

        personPanel = new JPanel();
        roomPanel = new JPanel();
        weaponPanel = new JPanel();

        updatePanels();
    }

    // Rebuilds all three card panels
    public void updatePanels() {
        personPanel.removeAll();
        roomPanel.removeAll();
        weaponPanel.removeAll();

        personPanel.setLayout(new GridLayout(0, 1));
        roomPanel.setLayout(new GridLayout(0, 1));
        weaponPanel.setLayout(new GridLayout(0, 1));

        personPanel.setBorder(new TitledBorder(new EtchedBorder(), "People"));
        roomPanel.setBorder(new TitledBorder(new EtchedBorder(), "Rooms"));
        weaponPanel.setBorder(new TitledBorder(new EtchedBorder(), "Weapons"));

        // Add cards by category in order: People, Rooms, Weapons
        addCategoryCards(CardType.PERSON, personPanel);
        addCategoryCards(CardType.ROOM, roomPanel);
        addCategoryCards(CardType.WEAPON, weaponPanel);

        // Add the three panels to the main panel
        removeAll();
        add(personPanel);
        add(roomPanel);
        add(weaponPanel);

        revalidate();
        repaint();
    }

    // Helper to populate "In Hand" and "Seen" cards for a specific card type category
    private void addCategoryCards(CardType type, JPanel panel) {
        // --- In Hand Section ---
        addSectionLabel(panel, "In Hand:");

        boolean hasHandCards = false;
        for (Card card : human.getHand()) {
            if (card.getCardType() == type) {
                hasHandCards = true;
                JLabel label = createCardLabel(card, null, true);
                panel.add(label);
            }
        }
        if (!hasHandCards) {
            JLabel noneLabel = new JLabel("None", SwingConstants.CENTER);
            noneLabel.setOpaque(true);
            noneLabel.setBackground(Color.WHITE);
            noneLabel.setBorder(new EtchedBorder());
            panel.add(noneLabel);
        }

        // --- Seen Section ---
        addSectionLabel(panel, "Seen:");

        boolean hasSeenCards = false;
        for (Card card : human.getSeenCards()) {
            // Do not display cards that are already in hand
            if (human.getHand().contains(card)) {
                continue;
            }

            if (card.getCardType() == type) {
                hasSeenCards = true;
                Player owner = findOwner(card);
                JLabel label = createCardLabel(card, owner, false);
                panel.add(label);
            }
        }
    }

    // Creates a styled label for a card
    private JLabel createCardLabel(Card card, Player owner, boolean isInHand) {
        JLabel label = new JLabel(card.getCardName(), SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBorder(new EtchedBorder());

        if (!isInHand && owner != null) {
            label.setBackground(lightenColor(owner.getColor()));
            label.setToolTipText("Held by " + owner.getName());
        } else {
            label.setBackground(Color.WHITE);
            label.setToolTipText(isInHand ? "In hand" : "Owner unknown");
        }

        return label;
    }

    // Finds which player has the card in their hand
    private Player findOwner(Card card) {
        for (Player player : players) {
            if (player.getHand().contains(card)) {
                return player;
            }
        }
        return null;
    }

    // Makes player colors lighter so the text is easier to read
    private Color lightenColor(Color color) {
        int red = (color.getRed() + 255) / 2;
        int green = (color.getGreen() + 255) / 2;
        int blue = (color.getBlue() + 255) / 2;

        return new Color(red, green, blue);
    }

    // Adds a left-aligned heading to a card section
    private void addSectionLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(label);
    }

    // Main method to test the panel
    public static void main(String[] args) {
        HumanPlayer human = new HumanPlayer("Ada Byte", Color.RED, 0, 7);
        ComputerPlayer computer1 = new ComputerPlayer("Grace Loop", Color.BLUE, 0, 18);
        ComputerPlayer computer2 = new ComputerPlayer("Linus Stack", Color.GREEN, 7, 0);

        // Give the human some cards
        human.updateHand(new Card("Observatory", CardType.ROOM));
        human.updateHand(new Card("Wrench", CardType.WEAPON));
        human.updateHand(new Card("Ada Byte", CardType.PERSON));

        // Give the computers some cards
        Card rope = new Card("Rope", CardType.WEAPON);
        Card armory = new Card("Armory", CardType.ROOM);
        Card grace = new Card("Grace Loop", CardType.PERSON);

        computer1.updateHand(rope);
        computer1.updateHand(grace);
        computer2.updateHand(armory);

        // Record cards seen by the human
        human.updateSeen(rope);
        human.updateSeen(armory);
        human.updateSeen(grace);

        // Create the player list
        List<Player> players = new ArrayList<>();

        players.add(human);
        players.add(computer1);
        players.add(computer2);

        // Create the panel
        KnownCardsPanel panel = new KnownCardsPanel(human, players);

        // Display the panel
        JFrame frame = new JFrame("Known Cards");

        frame.setContentPane(panel);
        frame.setSize(300, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}