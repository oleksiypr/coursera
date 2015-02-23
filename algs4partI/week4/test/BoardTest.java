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
    public void testOneHamming() {
        int[][] blocks ={{0}};
        Board board = new Board(blocks);
        assertEquals(0, board.hamming());
    }    
    
    @Test
    public void testHammingAllInPosition() {
        int[][] blocks = {{ 1, 2, 3 }, 
                          { 4, 5, 6 }, 
                          { 7, 8, 0 }};
        Board board = new Board(blocks);
        assertEquals(0, board.hamming());
    }
    
    @Test
    public void testHammingOneSwapNotInPosition() {
        int[][] blocks1 = {{ 2, 1, 3 }, 
                           { 4, 5, 6 }, 
                           { 7, 8, 0 }};
        Board board = new Board(blocks1);
        assertEquals(2, board.hamming());
        
        int[][] blocks2 = {{ 1, 2, 3 }, 
                           { 4, 6, 5 }, 
                           { 7, 8, 0 }};
        board = new Board(blocks2);
        assertEquals(2, board.hamming());
        
        int[][] blocks3 = {{ 1, 2, 3 }, 
                           { 4, 5, 6 }, 
                           { 7, 0, 8 }};
        board = new Board(blocks3);
        assertEquals(1, board.hamming());
        
        int[][] blocks4 = {{ 4, 2, 3 }, 
                           { 1, 5, 6 }, 
                           { 7, 8, 0 }};
        board = new Board(blocks4);
        assertEquals(2, board.hamming());

        int[][] blocks5 = {{ 1, 2, 3 }, 
                           { 4, 8, 6 }, 
                           { 7, 5, 0 }};
        board = new Board(blocks5);
        assertEquals(2, board.hamming());

        int[][] blocks6 = {{ 1, 2, 3 }, 
                           { 4, 5, 0 }, 
                           { 7, 8, 6 }};
        board = new Board(blocks6);
        assertEquals(1, board.hamming());
    }
    
    @Test
    public void testHamming() {
        int[][] blocks = {{ 8, 1, 3 }, 
                          { 4, 0, 2 }, 
                          { 7, 6, 5 }};  
        Board board = new Board(blocks);
        assertEquals(5, board.hamming());
    }
    
    @Test
    public void testZeroManhattan() {
        Board board = new Board(new int[0][0]); 
        assertEquals(0, board.manhattan());
    }    
  
    @Test
    public void testOneManhattan() {
        int[][] blocks ={{0}};
        Board board = new Board(blocks);
        assertEquals(0, board.manhattan());
    }    
    
    
    @Test
    public void testManhattanAllInPosition() {
        int[][] blocks = {{ 1, 2, 3 }, 
                          { 4, 5, 6 }, 
                          { 7, 8, 0 }};
        Board board = new Board(blocks);
        assertEquals(0, board.manhattan());
    }
    
    @Test
    public void testManhattanOneSwapNotInPosition() {
        int[][] blocks1 = {{ 2, 1, 3 }, 
                           { 4, 5, 6 }, 
                           { 7, 8, 0 }};
        Board board = new Board(blocks1);
        assertEquals(2, board.manhattan());
        
        int[][] blocks2 = {{ 1, 2, 3 }, 
                           { 4, 6, 5 }, 
                           { 7, 8, 0 }};
        board = new Board(blocks2);
        assertEquals(2, board.manhattan());
        
        int[][] blocks3 = {{ 1, 2, 3 }, 
                           { 4, 5, 6 }, 
                           { 7, 0, 8 }};
        board = new Board(blocks3);
        assertEquals(1, board.manhattan());
        
        int[][] blocks4 = {{ 4, 2, 3 }, 
                           { 1, 5, 6 }, 
                           { 7, 8, 0 }};
        board = new Board(blocks4);
        assertEquals(2, board.manhattan());

        int[][] blocks5 = {{ 1, 2, 3 }, 
                           { 4, 8, 6 }, 
                           { 7, 5, 0 }};
        board = new Board(blocks5);
        assertEquals(2, board.manhattan());

        int[][] blocks6 = {{ 1, 2, 3 }, 
                           { 4, 5, 0 }, 
                           { 7, 8, 6 }};
        board = new Board(blocks6);
        assertEquals(1, board.manhattan());
    }
    
    @Test
    public void testManhattan() {
        int[][] blocks = {{ 8, 1, 3 }, 
                          { 4, 0, 2 }, 
                          { 7, 6, 5 }};  
        Board board = new Board(blocks);
        assertEquals(10, board.manhattan());
    }
    
    @Test
    public void isGoalTrue() {
        int[][] single = {{0}};
        Board board = new Board(single);
        assertTrue(board.isGoal()); 
        
        int[][] blocks = {{ 1, 2, 3 }, 
                          { 4, 5, 6 }, 
                          { 7, 8, 0 }};  
        board = new Board(blocks);
        assertTrue(board.isGoal());        
    }
    
    @Test
    public void isGoalFalse() {
        int[][] single = {{1}};
        Board board = new Board(single);
        assertFalse(board.isGoal()); 
        
        int[][] blocks = {{ 2, 1, 3 }, 
                          { 4, 5, 6 }, 
                          { 7, 8, 0 }};  
        board = new Board(blocks);
        assertFalse(board.isGoal());        
    }
    
    @Test
    public void testTwinDirect() {
        int[][] blocks = {{ 2, 1, 3 }, 
                          { 4, 5, 6 }, 
                          { 7, 8, 0 }};  
        Board board = new Board(blocks);
        for (int i = 0; i < 1000; i++) {
            Board twin = board.twin();
            assertNotEquals(board, twin);
        }
    }
    
    @Test(timeout = 4)
    public void testTwinViceVersa() {
        int[][] blocks = {{ 2, 1, 3 }, 
                          { 4, 5, 6 }, 
                          { 7, 8, 0 }};  
        Board board = new Board(blocks);        
        Board twin = board.twin();
        while (true) {
            if (twin.twin().equals(board)) break;
        }
    } 
    
    @Test 
    public void testTwinSingle() {
        int[][] single = {{1}};
        Board board = new Board(single);
        assertEquals(board, board.twin());
    }
    
    @Test
    public void testNeighboursSingle() {
        int[][] single = {{0}};
        Board board = new Board(single);
        assertFalse(board.neighbors().iterator().hasNext());
    }
    
    @Test
    public void testNeighboursTop() {
        int[][] blocks0 = {{ 2, 0, 3 }, 
                           { 4, 5, 6 }, 
                           { 7, 8, 1 }}; 
        
        int[][] blocks1 = {{ 0, 2, 3 }, 
                           { 4, 5, 6 }, 
                           { 7, 8, 1 }}; 
        
        int[][] blocks2 = {{ 2, 3, 0 }, 
                           { 4, 5, 6 }, 
                           { 7, 8, 1 }}; 
        
        int[][] blocks3 = {{ 2, 5, 3 }, 
                           { 4, 0, 6 }, 
                           { 7, 8, 1 }}; 
        Board board = new Board(blocks0);
        Stack<Board> expected = new Stack<Board>();
        expected.push(new Board(blocks1));
        expected.push(new Board(blocks2));
        expected.push(new Board(blocks3));
                
        Stack<Board> neighbours = (Stack<Board>) board.neighbors();  
        assertEquals(3, neighbours.size());        
        assertNeighbours(expected, neighbours);
    }
    
    @Test
    public void testNeighbours() {
        int[][] blocks0 = {{ 2, 5, 3 }, 
                           { 4, 0, 6 }, 
                           { 7, 8, 1 }}; 
        
        int[][] blocks1 = {{ 2, 0, 3 }, 
                           { 4, 5, 6 }, 
                           { 7, 8, 1 }}; 
        
        int[][] blocks2 = {{ 2, 5, 3 }, 
                           { 4, 6, 0 }, 
                           { 7, 8, 1 }}; 
        
        int[][] blocks3 = {{ 2, 5, 3 }, 
                           { 0, 4, 6 }, 
                           { 7, 8, 1 }}; 
        
        int[][] blocks4 = {{ 2, 5, 3 }, 
                           { 4, 8, 6 }, 
                           { 7, 0, 1 }}; 
        
        Board board = new Board(blocks0);
        Stack<Board> expected = new Stack<Board>();
        expected.push(new Board(blocks1));
        expected.push(new Board(blocks2));
        expected.push(new Board(blocks3));
        expected.push(new Board(blocks4));
                
        Stack<Board> neighbours = (Stack<Board>) board.neighbors();  
        assertEquals(4, neighbours.size());        
        assertNeighbours(expected, neighbours);
    }
    
    private void assertNeighbours(Stack<Board> expected, Stack<Board> neighbours) {
        int size = expected.size();
        for (int i = 0; i < size; i++) {
            Board exp = expected.peek();
            for (Board b: neighbours) {
                if (b.equals(exp)) expected.pop();
            }
        }
        assertEquals(0, expected.size());
    }
}