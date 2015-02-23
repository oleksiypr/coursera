public class Solver {
    /**
     * Find a solution to the initial board (using the A* algorithm)
     * @param initial
     */
    public Solver(Board initial) {
        //TODO        
    }          
    
    /**
     * Is the initial board solvable?
     * @return true iff solvable
     */
    public boolean isSolvable()  {
        //TODO
        return false;
    }   
    
    /**
     * Min number of moves to solve initial board; -1 if unsolvable
     * @return min number of moves to solve initial board; -1 if unsolvable
     */
    public int moves() {
        //TODO
        return -1;
    } 

    /**
     * Sequence of boards in a shortest solution; null if unsolvable
     * @return solutions
     */
    public Iterable<Board> solution() {
        //TODO
        return null;
    }     
    
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}