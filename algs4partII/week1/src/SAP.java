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
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);

        int minS = Integer.MAX_VALUE;
        boolean noWay = true;
        for (int k = 0; k < G.V(); k++) {
            if (bfsV.hasPathTo(k) && bfsW.hasPathTo(k)) {
                noWay = false;
                int s = bfsV.distTo(k) + bfsW.distTo(k);
                minS = Math.min(minS, s);
            }
        }

        return noWay ? -1 : minS;
    }

    /**
     * A common ancestor of v and w that participates in a shortest ancestral
     * path; -1 if no such path
     * @param v a vertice of the graph
     * @param w a vertice of the graph
     * @return A common ancestor for the SAP
     */
    public int ancestor(int v, int w) {
        return -1;
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
            StdOut.printf("length = %d\n", length);
        }
    }
}