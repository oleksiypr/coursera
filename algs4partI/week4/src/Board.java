public class Board {
    private final static int BLANK = 0; //blank block
    private final static int NA = -1;   //not available yet
    
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;
    
    private final int[][] blocks;
    private final int N;
    private final int iBlank;
    private final int jBlank;
    
    private int hamming = NA;
    private int manhattan = NA;
    
    /**
     * Construct a board from an N-by-N array of blocks
     * (where blocks[i][j] = block in row i, column j)
     * 
     * @param blocks an array of blocks
     */
    public Board(int[][] blocks) {
        this.N = blocks.length;
        this.blocks = new int[N][N];
        
        int iBlank = NA;
        int jBlank = NA;
        for (int i = 0; i < N; i++)
        for (int j = 0; j < N; j++) {
            this.blocks[i][j] = blocks[i][j]; 
            if (blocks[i][j] == BLANK) {
                iBlank = i;
                jBlank = j;
            }
        }
        
        this.iBlank = iBlank;
        this.jBlank = jBlank;
    }         
    
    /**
     * Board dimension N
     * @return board dimension N
     */
    public int dimension() {
        return N;
    }   
    
    /**
     * Number of blocks out of place
     * @return number of blocks out of place
     */
    public int hamming() {
        if (hamming != NA) return hamming;
        
        hamming = 0;
        for (int i = 0; i < N; i++)
        for (int j = 0; j < N; j++)
            if (number(i, j) !=  blocks[i][j]) hamming++;
        
        return hamming;
    }
    
    /**
     * Sum of Manhattan distances between blocks and goal
     * @return sum of Manhattan distances between blocks and goal
     */
    public int manhattan() {
        if (manhattan != NA) return manhattan;
        
        manhattan = 0;
        for (int i = 0; i < N; i++)
        for (int j = 0; j < N; j++) {
            int n = blocks[i][j];
            int di = Math.abs(i - i(n));
            int dj = Math.abs(j - j(n));
            
            manhattan += di + dj;
        }
            
        
        return manhattan;
    }   
    
    /**
     * Is this board the goal board?
     * @return  true iff this board the goal board
     */
    public boolean isGoal() {
        for (int i = 0; i < N; i++)
        for (int j = 0; j < N; j++) {
            if (blocks[i][j] != number(i, j)) return false;
        }
        
        return true;
    }                
    
    /**
     * A board that is obtained by exchanging two adjacent blocks in the same row.
     * @return new board that is obtained by exchanging two adjacent blocks in the same row
     */
    public Board twin() {
        //TODO
        return null;
    }       
   
    /**
     * All neighboring boards
     * @return neighbors
     */
    public Iterable<Board> neighbors()  {
        Stack<Board> neighbours = new Stack<Board>();        
        if (hasNeighbour(iBlank, jBlank, DOWN))   neighbours.push(swap(iBlank, jBlank, DOWN));
        if (hasNeighbour(iBlank, jBlank, UP))     neighbours.push(swap(iBlank, jBlank, UP));
        if (hasNeighbour(iBlank, jBlank, RIGHT))  neighbours.push(swap(iBlank, jBlank, RIGHT));
        if (hasNeighbour(iBlank, jBlank, LEFT))   neighbours.push(swap(iBlank, jBlank, LEFT));
        
        return neighbours;
    }  
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (this.getClass() != obj.getClass()) return false;
        
        Board that = (Board) obj;
        if (this.N != that.N) return false;        
        for (int i = 0; i < N; i++)
        for (int j = 0; j < N; j++) {
            if (this.blocks[i][j] != that.blocks[i][j]) return false;
        }
        
        return true;
    } 

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", blocks[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }    
    
    // (i: 0..N - 1, j: 0..N - 1) -> number: 1..N^2-1, 0 preserved for blank
    private int number(int i, int j) {
        if (i == N - 1 && j == N - 1) return BLANK;
        return i*N + j + 1;
    }
    
    private int i(int n) {
        if (n == 0) return N - 1;
        return n/N;
    }
    
    private int j(int n) {
        if (n%N == 0) return N - 1;
        return n%N - 1; 
    }
    
    private boolean hasNeighbour(int i, int j, int direction) {
        switch (direction) {
            case DOWN:  return (i < N - 1);
            case UP:    return (i > 0);
            case RIGHT: return (j < N - 1);
            case LEFT:  return (j > 0);
            default:    throw new IllegalArgumentException("Unknown direction: " + direction);
        }
    }
    
    private Board swap(int i, int j, int direction) {
        int i1 = i;
        int j1 = j;
        
        switch (direction) {
            case DOWN:  i1++; break;
            case UP:    i1--; break;
            case RIGHT: j1++; break;
            case LEFT:  j1--; break;
            default:    throw new IllegalArgumentException("Unknown direction: " + direction);
        }    
        
        swap(i, j, i1, j1);
        Board swapped = new Board(blocks);
        swap(i1, j1, i, j);
        return swapped;
    }

    private void swap(int i1, int j1, int i2, int j2) {
        int tmp = blocks[i1][j1];
        blocks[i1][j1] = blocks[i2][j2];
        blocks[i2][j2] = tmp;        
    }

    public static void main(String[] args) {
        int[][] blocks = {
                {10, 0, 3}, 
                {4, 2, 5}, 
                {7, 8, 6}};
        Board board = new Board(blocks);
        StdOut.println(board);
        StdOut.println();
        
        StdOut.println("Neighbours:");
        for (Board b: board.neighbors()) {
            StdOut.println(b);
        }
        StdOut.println();
        
        StdOut.println(board);
        StdOut.println();
     }
}