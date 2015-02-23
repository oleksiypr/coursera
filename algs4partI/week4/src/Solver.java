import java.util.Comparator;

public class Solver {
    private static class HammingComparator implements Comparator<SearchNode> {
        @Override
        public int compare(SearchNode n1, SearchNode n2) {
            int h1 = n1.moves + n1.board.hamming();
            int h2 = n2.moves + n2.board.hamming();
            return h1 - h2;
        }        
    }
    
    private static class SearchNode {
        private final Board board;
        private final int moves;
        private final SearchNode previous;
        public SearchNode(Board board, int moves, SearchNode previous) {
            this.board = board;
            this.moves = moves;
            this.previous = previous;
        }        
    }
    
    private MinPQ<SearchNode> priorityQueue;
    
    /**
     * Find a solution to the initial board (using the A* algorithm)
     * @param initial
     */
    public Solver(Board initial) {
        priorityQueue = new MinPQ<Solver.SearchNode>(new HammingComparator());
        priorityQueue.insert(new SearchNode(initial, 0, null));        
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