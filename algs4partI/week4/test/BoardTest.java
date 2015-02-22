import static org.junit.Assert.*;

import org.junit.Test;


public class BoardTest { 
    @Test
    public void testZeroDimension() {
        Board board = new Board(new int[0][0]); 
        assertEquals(0, board.dimension());
    }
    
    @Test
    public void testDimension() {
        int N = 1;
        Board board = new Board(new int[N][N]); 
        assertEquals(N, board.dimension());
        
        N = 10;
        board = new Board(new int[N][N]); 
        assertEquals(N, board.dimension());
    }
    
    @Test
    public void testZeroHamming() {
        Board board = new Board(new int[0][0]); 
        assertEquals(0, board.hamming());
    }
    
    @Test
    public void testOneHammingNotInPosition() {
        int[][] blocks ={{2}};
        Board board = new Board(blocks);
        assertEquals(1, board.hamming());
    }
    
    @Test
    public void testOneHammingInPosition() {
        int[][] blocks ={{1}};
        Board board = new Board(blocks);
        assertEquals(1, board.hamming());
    }
}
