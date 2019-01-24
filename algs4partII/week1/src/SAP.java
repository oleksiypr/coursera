/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;


/**
 * Shortest ancestral path. An ancestral path between two vertices v and w in a
 * digraph is a directed path from v to a common ancestor x, together with a
 * directed path from w to the same ancestor x. A shortest ancestral path is an
 * ancestral path of minimum total length. We refer to the common ancestor in a
 * shortest ancestral path as a shortest common ancestor. Note also that an
 * ancestral path is a path, but not a directed path.
 */
public class SAP {

    /**
     * Сonstructor takes a digraph (not necessarily a DAG)
     * @param G a digreph
     */
    public SAP(Digraph G) {

    }

    /**
     * Length of shortest ancestral path between v and w; -1 if no such path
     * @param v a vertice of the graph
     * @param w a vertoice  of the graph
     * @return length of the SAP
     */
    public int length(int v, int w) {
        return -1;
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

    }
}