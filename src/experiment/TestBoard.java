package experiment;

import java.util.HashSet;
import java.util.Set;

public class TestBoard {

    private TestBoardCell[][] board;
    private Set<TestBoardCell> targets;
    private Set<TestBoardCell> visited;

    public TestBoard() {

        board = new TestBoardCell[4][4];

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                board[row][col] = new TestBoardCell(row, col);
            }
        }

        visited = new HashSet<TestBoardCell>();
        targets = new HashSet<TestBoardCell>();

        // Creates adj list of all neighboring cells regardless of if there is a connection on board
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (col - 1 >= 0) {
                    board[row][col].addAdjacency(board[row][col-1]);
                }
                if (col + 1 < 4) {
                    board[row][col].addAdjacency(board[row][col+1]);
                }
                if (row - 1 >= 0) {
                    board[row][col].addAdjacency(board[row-1][col]);
                }
                if (row + 1 < 4) {
                    board[row][col].addAdjacency(board[row+1][col]);
                }
            }
        }
    }

    public void calcTargets(TestBoardCell startCell, int pathLength) {
        // Resets variables so func can be reused between calls 
        targets = new HashSet<TestBoardCell>();
        visited = new HashSet<TestBoardCell>();

        visited.add(startCell);
        findAllTargets(startCell, pathLength-1);
    }

    private void findAllTargets(TestBoardCell cell, int stepsRemaining) {
        // visits every neighbor from current cells adjacency list
        for (TestBoardCell neighbor: cell.getAdjList()) {
            // checks if that neighbor has been visited in current loop and if neighbor is an occupied cell
            // if that neighbor is not a room and has more steps recurse to find neighbors neighbor
            // if not, then it is a possible target 

            if (!visited.contains(neighbor) && !neighbor.getOccupied()) {
                visited.add(neighbor);
                if (stepsRemaining > 0 && !neighbor.isRoom()) {
                    findAllTargets(neighbor, stepsRemaining-1);
                    visited.remove(neighbor);
                }
                else {
                    targets.add(neighbor);
                    visited.remove(neighbor);
                }
            }
        }

    }

    public TestBoardCell getCell(int row, int col) {
        return board[row][col];
    }

    public Set<TestBoardCell> getTargets() {
        return targets;
    }
}