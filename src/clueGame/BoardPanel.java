package clueGame;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;

public class BoardPanel extends JPanel {

    private Board board;

    public BoardPanel() {
        board = Board.getInstance();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        
        int rows = board.getNumRows();
        int columns = board.getNumColumns();
        if (rows == 0 || columns == 0) {
            return;
        }

        //calculate cell dimensions
        int cellWidth = getWidth() / columns;
        int cellHeight = getHeight() / rows;

        //draw all the cells
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                BoardCell cell = board.getCell(row, col);
                cell.draw(g, cellWidth, cellHeight);
            }
        }

        //draw room names
        drawRoomNames(g, cellWidth, cellHeight);

        //draw players
        drawPlayers(g, cellWidth, cellHeight);
    }

    private void drawRoomNames(Graphics g, int cellWidth, int cellHeight) {

        g.setColor(Color.BLUE);
        g.setFont(new Font("Arial", Font.BOLD, 14));

        for (Room room : board.getRoomMap().values()) {
            BoardCell label = room.getLabelCell();
            if (label != null) {
                int x = label.getCol() * cellWidth;
                int y = label.getRow() * cellHeight;

                //center the name around the label cell
                int textWidth = g.getFontMetrics().stringWidth(room.getName());
                g.drawString(room.getName(), x + cellWidth / 2 - textWidth / 2, y + cellHeight);
            }
        }
    }

    private void drawPlayers(Graphics g, int cellWidth, int cellHeight) {

        for (Player player : board.getPlayers()) {

            int row = player.getRow();
            int col = player.getColumn();

            //make sure the player's location is on the board
            if (row < 0 || row >= board.getNumRows() || col < 0 || col >= board.getNumColumns()) {
                continue;
            }

            int x = col * cellWidth;
            int y = row * cellHeight;

            //draw circle in the center of the cell
            int diameter = Math.min(cellWidth, cellHeight) - 4;
            g.setColor(player.getColor());
            g.fillOval(x + (cellWidth - diameter) / 2, y + (cellHeight - diameter) / 2, diameter, diameter);

            //black outline
            g.setColor(Color.BLACK);
            g.drawOval(x + (cellWidth - diameter) / 2, y + (cellHeight - diameter) / 2, diameter, diameter);
        }
    }
}
