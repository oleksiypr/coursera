/* *****************************************************************************
 *  Name: Oleksii Prosianko
 *  Date: 2019/01/24
 *  Description: Shortest ancestral path solution
 **************************************************************************** */

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * Shortest ancestral path. An ancestral path between two vertices v and w in a
 * digraph is a directed path from v to a common ancestor x, together with a
 * directed path from w to the same ancestor x. A shortest ancestral path is an
 * ancestral path of minimum total length. We refer to the common ancestor in a
 * shortest ancestral path as a shortest common ancestor. Note also that an
 * ancestral path is a path, but not a directed path.
 */
public class SAP {

    private final Digraph G;

    private static final class Solution {
        public final int length;
        public final int ancestor;

        public Solution(int length, int ancestor) {
            this.length = length;
            this.ancestor = ancestor;
        }
    }

    /**
     * Сonstructor takes a digraph (not necessarily a DAG)
     * @param G a digreph
     */
    public SAP(Digraph G) {
        this.G = new Digraph(G);
    }

    /**
     * Length of shortest ancestral path between v and w; -1 if no such path
     * @param v a vertice of the graph
     * @param w a vertoice  of the graph
     * @return length of the SAP
     */
    public int length(int v, int w) {
        return solve(v, w).length;
    }

    /**
     * A common ancestor of v and w that participates in a shortest ancestral
     * path; -1 if no such path
     * @param v a vertice of the graph
     * @param w a vertice of the graph
     * @return A common ancestor for the SAP
     */
    public int ancestor(int v, int w) {
        return solve(v, w).ancestor;
    }

    /**
     * Length of shortest ancestral path between any vertex in v and any vertex
     * in w; -1 if no such path
     * @param v a vertice of the graph
     * @param w a vertice of the graph
     * @return length of the SAP
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return solve(v, w).length;
    }

    /**
     * A common ancestor that participates in shortest ancestral path; -1 if no
     * such path
     * @param v a vertice of the graph
     * @param w a vertice of the graph
     * @return A common ancestor for the SAP
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return solve(v, w).ancestor;
    }
    
    private Solution solve(int v, int w) {
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        return solution(bfsV, bfsW);
    }

    private Solution solve(Iterable<Integer> vs, Iterable<Integer> ws) {
        validate(vs);
        validate(ws);
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, vs);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, ws);
        return solution(bfsV, bfsW);
    }

    private Solution solution(
        BreadthFirstDirectedPaths bfsV,
        BreadthFirstDirectedPaths bfsW
    ) {
        int length = Integer.MAX_VALUE;
        int ancestor = -1;

        for (int k = 0; k < G.V(); k++) {
            if (bfsV.hasPathTo(k) && bfsW.hasPathTo(k)) {
                int s = bfsV.distTo(k) + bfsW.distTo(k);
                if (s < length) {
                    length = s;
                    ancestor = k;
                }
            }
        }

        if (ancestor == -1) length = -1;
        return new Solution(length, ancestor);
    }

    private void validate(Iterable<Integer> vs) {
        if (vs == null) {
            throw new IllegalArgumentException("argument is null");
        }
        for (Integer v : vs) {
            if (v == null)
                throw new IllegalArgumentException("vertex is null ");
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}