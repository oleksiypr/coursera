import edu.princeton.cs.algs4.*;

/**
 * Percolation implementation based on QuickFindUF.
 * 
 * @author Oleksiy_Prosyanko
 */
public class Percolation {
    private static final int UP = 0;
    private static final int RIGHT = 1;
    private static final int DOWN = 2;
    private static final int LEFT = 3;

    private final int top;
    private final int bottom;
    private final int N;

    private WeightedQuickUnionUF cluster;
    private boolean[][] grid;

    /**
     * Create N-by-N grid, with all sites blocked.
     * @param N size of the grid
     */
    public Percolation(int N) {
        if (N <= 0) throw new IllegalArgumentException("N should be positive, N: " + N);

        int size = N*N + 2;
        this.N = N;
        this.top = 0;
        this.bottom = size - 1;
        this.cluster = new WeightedQuickUnionUF(size);
        this.grid = new boolean[N][N];
    }

    /**
     * Open site (row i, column j) if it is not open already.
     * @param i site row, 1..N
     * @param j site column, 1..N
     */
    public void open(int i, int j) {
        check(i, j);
        if (isOpen(i, j)) return;

        grid[index(i)][index(j)] = true;
        if (i == 1) connectWithTop(i, j);
        if (i == N) connectWithBottom(i, j);

        if (hasNeighbour(i, j, DOWN))   connet(i, j, DOWN);
        if (hasNeighbour(i, j, UP))     connet(i, j, UP);
        if (hasNeighbour(i, j, RIGHT))  connet(i, j, RIGHT);
        if (hasNeighbour(i, j, LEFT))   connet(i, j, LEFT);
    }

    /**
     * Is site (row i, column j) open? 
     * @param i site row, 1..N
     * @param j site column, 1..N
     * @return true if site open, else false
     */
    public boolean isOpen(int i, int j) {
        check(i, j);
        return grid[index(i)][index(j)];
    }

    /**
     * Is site (row i, column j) full? 
     * @param i site row, 1..N
     * @param j site column, 1..N
     * @return true if site full, else false
     */
    public boolean isFull(int i, int j) {
        check(i, j);
        int p = number(i, j);
        return cluster.connected(p, top);
    }

    /**
     * Does the system percolate? 
     * @return true is system percolate, else false
     */
    public boolean percolates() {
        return cluster.connected(top, bottom);
    }

    private void check(int i, int j) {
        if (i < 1 || i > N) throw new IndexOutOfBoundsException("i: " + i);
        if (j < 1 || j > N) throw new IndexOutOfBoundsException("j: " + j);
    }

    private void connectWithTop(int i, int j) {
        int p = number(i, j);
        cluster.union(p, top);
    }

    private void connectWithBottom(int i, int j) {
        int p = number(i, j);
        cluster.union(p, bottom);
    }

    private boolean hasNeighbour(int i, int j, int direction) {
        switch (direction) {
            case DOWN:  return (i < N) && isOpen(i + 1, j);
            case UP:    return (i > 1) && isOpen(i - 1, j);
            case RIGHT: return (j < N) && isOpen(i, j + 1);
            case LEFT:  return (j > 1) && isOpen(i, j - 1);
            default:    throw new IllegalArgumentException("Unknown direction: " + direction);
        }
    }

    private void connet(int i, int j, int direction) {
        int p = number(i, j);
        int q;
        switch (direction) {
            case DOWN:  q = number(i + 1, j); break;
            case UP:    q = number(i - 1, j); break;
            case RIGHT: q = number(i, j + 1); break;
            case LEFT:  q = number(i, j - 1); break;
            default: throw new IllegalArgumentException("Unknown direction: " + direction);
        }

        if (!cluster.connected(p, q)) cluster.union(p, q);
    }

    // (i: 1..N, j: 1..N) -> number: 1..size-1, 0 preserved for top
    private int number(int i, int j) {
        return N*(i - 1) + j;
    }

    // Grid position i: 1..N -> array index: 0..N - 1
    private int index(int i) {
        return i - 1;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    }
}