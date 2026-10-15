package tests;

import experiment.TestBoard;
import experiment.TestBoardCell;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class BoardTestExp {

    TestBoard board;

    @BeforeEach
    public void setUp() {
        // Board should create adjacency list
        board = new TestBoard();
    }

    //Adjacency Test

    //tests top left 
    @Test
    public void testAdjacencyTL() {
        TestBoardCell cell = board.getCell(0, 0);
        Set<TestBoardCell> testList = cell.getAdjList();

        assertTrue(testList.contains(board.getCell(1, 0)));
        assertTrue(testList.contains(board.getCell(0, 1)));
        assertEquals(2, testList.size());
    }

    //tests bottom right
    @Test
    public void testAdjacencyBR() {
        TestBoardCell cell = board.getCell(3, 3);
        Set<TestBoardCell> testList = cell.getAdjList();

        assertTrue(testList.contains(board.getCell(2, 3)));
        assertTrue(testList.contains(board.getCell(3, 2)));
        assertEquals(2, testList.size());
    }

    //tests right edge
    @Test
    public void testAdjacencyRE() {

        TestBoardCell cell = board.getCell(1, 3);
        Set<TestBoardCell> testList = cell.getAdjList();

        assertTrue(testList.contains(board.getCell(0, 3)));
        assertTrue(testList.contains(board.getCell(2, 3)));
        assertTrue(testList.contains(board.getCell(1, 2)));
        assertEquals(3, testList.size());
    }

    //tests left edge
    @Test
    public void testAdjacencyLE() {

        TestBoardCell cell = board.getCell(3, 0);
        Set<TestBoardCell> testList = cell.getAdjList();

        assertTrue(testList.contains(board.getCell(2, 0)));
        assertTrue(testList.contains(board.getCell(3, 1)));
        assertEquals(2, testList.size());
    }

    //tests middle
    @Test
    public void testAdjacencyM() {

        TestBoardCell cell = board.getCell(2, 2);
        Set<TestBoardCell> testList = cell.getAdjList();

        assertTrue(testList.contains(board.getCell(1, 2)));
        assertTrue(testList.contains(board.getCell(2, 1)));
        assertTrue(testList.contains(board.getCell(2, 3)));
        assertTrue(testList.contains(board.getCell(3, 2)));
        assertEquals(4, testList.size());
    }

    //Target Testing

    //tests one step
    @Test
    public void testTargetsOneStep() {
        board.calcTargets(board.getCell(0, 0), 1);

        Set<TestBoardCell> targets = board.getTargets();

        assertEquals(2, targets.size());
        assertTrue(targets.contains(board.getCell(0, 1)));
        assertTrue(targets.contains(board.getCell(1, 0)));
    }

    //tests two steps
    @Test
    public void testTargetsTwoSteps() {
        board.calcTargets(board.getCell(0, 0), 2);

        Set<TestBoardCell> targets = board.getTargets();

        assertEquals(3, targets.size());
        assertTrue(targets.contains(board.getCell(0, 2)));
        assertTrue(targets.contains(board.getCell(1, 1)));
        assertTrue(targets.contains(board.getCell(2, 0)));
    }

    //tests step from different starting location
    @Test
    public void testTargetsDiffStart() {
        board.calcTargets(board.getCell(1, 1), 1);

        Set<TestBoardCell> targets = board.getTargets();

        assertEquals(4, targets.size());
        assertTrue(targets.contains(board.getCell(0, 1)));
        assertTrue(targets.contains(board.getCell(1, 0)));
        assertTrue(targets.contains(board.getCell(1, 2)));
        assertTrue(targets.contains(board.getCell(2, 1)));
    }

    //tests max dice roll of 6
    @Test
    public void testTargetsSix() {
        board.calcTargets(board.getCell(0, 0), 6);

        Set<TestBoardCell> targets = board.getTargets();

        assertFalse(targets.isEmpty());
    }

    //tests empty board with targets
    @Test
    public void testTargetsNormal() {
        TestBoardCell cell = board.getCell(0, 0);

        board.calcTargets(cell, 3);

        Set<TestBoardCell> targets = board.getTargets();

        assertEquals(6, targets.size());
        assertTrue(targets.contains(board.getCell(3, 0)));
        assertTrue(targets.contains(board.getCell(2, 1)));
        assertTrue(targets.contains(board.getCell(0, 1)));
        assertTrue(targets.contains(board.getCell(1, 2)));
        assertTrue(targets.contains(board.getCell(0, 3)));
        assertTrue(targets.contains(board.getCell(1, 0)));
    }

    //tests room board with targets
    @Test
    public void testTargetRoom() {
        // Enter room and end turn
        board.getCell(0, 1).setRoom(true);

        board.calcTargets(board.getCell(0, 0), 3);

        Set<TestBoardCell> targets = board.getTargets();

        assertTrue(targets.contains(board.getCell(0, 1)));
        assertFalse(targets.contains(board.getCell(0, 2)));
    }

    //tests occupied board with targets
    @Test
    public void testTargetsOccupied() {
        board.getCell(0, 1).setOccupied(true);

        board.calcTargets(board.getCell(0,0), 2);

        Set<TestBoardCell> targets = board.getTargets();

        assertFalse(targets.contains(board.getCell(0, 1)));
        assertFalse(targets.contains(board.getCell(0, 2)));
    }

    //tests room and occupied board with targets
    @Test
    public void testTargetsMixed() {
        // Set up occupied and room cells
        board.getCell(0, 2).setOccupied(true);
        board.getCell(1, 2).setRoom(true);

        TestBoardCell cell = board.getCell(0, 3);

        board.calcTargets(cell, 3);

        Set<TestBoardCell> targets = board.getTargets();

        assertEquals(3, targets.size());
        assertTrue(targets.contains(board.getCell(1, 2)));
        assertTrue(targets.contains(board.getCell(2, 2)));
        assertTrue(targets.contains(board.getCell(3, 3)));
    }

}