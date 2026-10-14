package experiment;

import java.util.HashSet;
import java.util.Set;

public class TestBoard {

    private TestBoardCell[][] board;
    private Set<TestBoardCell> targets;

    public TestBoard() {

        board = new TestBoardCell[4][4];

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                board[row][col] = new TestBoardCell(row, col);
            }
        }

        targets = new HashSet<TestBoardCell>();
    }

    public void calcTargets(TestBoardCell startCell, int pathLength) {
        // nothing in here yet
    }

    public TestBoardCell getCell(int row, int col) {
        return board[row][col];
    }

    public Set<TestBoardCell> getTargets() {
        return targets;
    }
}