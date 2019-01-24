/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
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

    private final class Solver {

        private final int v;
        private final int w;

        private int length;
        private int ancestor;

        public Solver(int v, int w) {
            this.v = v;
            this.w = w;
            this.length = Integer.MAX_VALUE;
            this.ancestor = -1;

            solve();
        }

        private void solve() {
            BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
            BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);

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
        }

        int length() {
            return length;
        }

        int ancestor() {
            return ancestor;
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
        Solver s = new Solver(v, w);
        return s.length();
    }

    /**
     * A common ancestor of v and w that participates in a shortest ancestral
     * path; -1 if no such path
     * @param v a vertice of the graph
     * @param w a vertice of the graph
     * @return A common ancestor for the SAP
     */
    public int ancestor(int v, int w) {
        Solver s = new Solver(v, w);
        return s.ancestor();
    }

    /**
     * Length of shortest ancestral path between any vertex in v and any vertex
     * in w; -1 if no such path
     * @param v a vertice of the graph
     * @param w a vertice of the graph
     * @return length of the SAP
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        return -1;
    }

    /**
     * A common ancestor that participates in shortest ancestral path; -1 if no
     * such path
     * @param v a vertice of the graph
     * @param w a vertice of the graph
     * @return A common ancestor for the SAP
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        return -1;
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