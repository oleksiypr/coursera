/**
 * This class in designed to find a solution to the initial board (using the A* algorithm).
 * We define a search node of the game to be a board, the number of moves made to reach the board, and 
 * the previous search node. First, insert the initial search node (the initial board, 0 moves, and a 
 * null previous search node) into a priority queue. Then, delete from the priority queue the search node 
 * with the minimum priority, and insert onto the priority queue all neighboring search nodes (those that can 
 * be reached in one move from the dequeued search node). Repeat this procedure until the search node dequeued 
 * corresponds to a goal board. The success of this approach hinges on the choice of priority function for a search 
 * node. 
 * 
 * @author Oleksiy_Prosyanko
 */
public class Solver {    
    private static class SearchNode implements Comparable<SearchNode> {
        private final Board board;
        private final int moves;
        private final SearchNode previous;
        public SearchNode(Board board, int moves, SearchNode previous) {
            this.board = board;
            this.moves = moves;
            this.previous = previous;
        }  
        
        Iterable<Board> iterable() {
            SearchNode previous = this.previous;
            Stack<Board> boards = new Stack<Board>();
            boards.push(this.board);
            while (previous != null) {            
                boards.push(previous.board);
                previous = previous.previous;
            }
            return boards;
        }

        @Override
        public int compareTo(SearchNode that) {
            return moves + board.manhattan() - (that.moves + that.board.manhattan());
        } 
    }
   
    private final MinPQ<SearchNode> nodes;
    private final MinPQ<SearchNode> twins;
    
    private final SearchNode initialNode;
    private SearchNode solution; 
    
    /**
     * Find a solution to the initial board (using the A* algorithm)
     * @param initial
     */
    public Solver(Board initial) {
        nodes = new MinPQ<Solver.SearchNode>();
        twins = new MinPQ<Solver.SearchNode>();
        initialNode = new SearchNode(initial, 0, null);             
        solution = null;
        
        nodes.insert(initialNode);
        twins.insert(new SearchNode(initial.twin(), 0, null));        
    }     
    
    /**
     * Is the initial board solvable?
     * @return true iff solvable
     */
    public boolean isSolvable()  {        
        if (initialNode.board.dimension() < 2) return false;
        if (initialNode.board.isGoal()) { 
            solution = initialNode;   
            return true;
        }
        if (solution != null) return true;
        
        while (true) {  
            SearchNode node = nodes.delMin();
            SearchNode twin = twins.delMin();
            
            if (node.board.isGoal()) {
                solution = node;
                return true;
            }
            
            if (twin.board.isGoal()) {
                return false;                
            }
            
            processNeighbours(node, node.board.neighbors(), nodes);
            processNeighbours(twin, twin.board.neighbors(), twins);
        }
    }   

    /**
     * Min number of moves to solve initial board; -1 if unsolvable
     * @return min number of moves to solve initial board; -1 if unsolvable
     */
    public int moves() {
        if (solution == null && !isSolvable()) return -1;
        return solution.moves;
    } 
    
    /**
     * Sequence of boards in a shortest solution; null if unsolvable
     * @return solutions
     */
    public Iterable<Board> solution() {
        if (solution == null && !isSolvable()) return null;
        return solution.iterable();
    }   
    
    private void processNeighbours(SearchNode node, Iterable<Board> neighbors, MinPQ<SearchNode> queue) {
        for (Board neignour: node.board.neighbors()) {
            if (alreadyPassed(neignour, node)) continue;
            queue.insert(new SearchNode(neignour, node.moves + 1, node));
        }        
    }
    
    private boolean alreadyPassed(Board current, SearchNode parent) {
        if (parent.previous == null) return false;
        return current.equals(parent.previous.board);
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