import static org.junit.Assert.*;

import org.junit.Test;

public class SolverTest {
    @Test
    public void isSolvableSingleTrueTest(){
        int[][] single = new int[1][1];
        Solver solver = new Solver(new Board(single));
        assertFalse(solver.isSolvable());
    }
    
    @Test
    public void isSolvableGoalTest(){
        int[][] goal = {{ 1, 2, 3 }, 
                        { 4, 5, 6 }, 
                        { 7, 8, 0 }};
        Solver solver = new Solver(new Board(goal));
        assertTrue(solver.isSolvable());;
    }
    
    @Test
    public void isSolvableTest(){
        int[][] blocks = {{ 0, 1, 3 }, 
                          { 4, 2, 5 }, 
                          { 7, 8, 6 }};
        Solver solver = new Solver(new Board(blocks));
        assertTrue(solver.isSolvable());;
    }
    
    @Test
    public void isNotSolvableTest(){
        int[][] blocks = {{ 1, 2, 3 }, 
                          { 4, 5, 6 }, 
                          { 8, 7, 0 }};
        Solver solver = new Solver(new Board(blocks));
        assertFalse(solver.isSolvable());;
    }      
}
