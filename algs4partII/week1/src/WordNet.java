import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.ST;

/**
 * WordNet groups words into sets of synonyms called nouns and describes
 * semantic relationships between them. One such relationship is the is-a
 * relationship, which connects a hyponym (more specific synset) to a hypernym
 * (more general synset).
 */
public class WordNet {

    private final ST<String, SET<Integer>> nouns;
    private final ST<Integer, String> ids;
    private final SAP sap;

	/**
	 * Construct new instance.
	 * @param synsets nouns input file name
	 * @param hypernyms hypernyms input file name
	 */
	public WordNet(String synsets, String hypernyms) {
        nouns = new ST<String, SET<Integer>>();
        ids = new ST<Integer, String>();

        In inS = new In(synsets);
        while (!inS.isEmpty()) {
            String[] line = inS.readLine().split(",");
            int v = Integer.parseInt(line[0]);
            ids.put(v, line[1]);

            for (String n: line[1].split("\\s+")) {
                if (nouns.contains(n)) {
                    nouns.get(n).add(v);
                } else {
                    SET<Integer> s = new SET<Integer>();
                    s.add(v);
                    nouns.put(n, s);
                }
            }
        }

        Digraph G = new Digraph(ids.size());
        In inH = new In(hypernyms);
        while (!inH.isEmpty()) {
            String[] line = inH.readLine().split(",");
            int v = Integer.parseInt(line[0]);
            for (int i = 1; i < line.length; i++) {
                int w = Integer.parseInt(line[i]);
                G.addEdge(v, w);
            }
        }

        DirectedCycle dc = new DirectedCycle(G);
        if (dc.hasCycle()) throw new IllegalArgumentException("Not a DAG");

        int sink = 0;
        for (int v = 0; v < G.V(); v++) {
            if (sink > 1) break;
            if (G.outdegree(v) == 0) sink++;
        }
        if (sink != 1) throw new IllegalArgumentException("Not a rooted DAG");

        this.sap = new SAP(G);
	}

    /**
    * All WordNet nouns.
    * @return all WordNet nouns.
    */
    public Iterable<String> nouns() {
        return nouns.keys();
    }

    /**
    * Define is the word a WordNet noun?
    * @param word the word
    * @return true iff the word is a WordNet noun
    */
    public boolean isNoun(String word) {
        return nouns.contains(word);
    }

   /**
    * Compute distance between nounA and nounB (defined in SAP).
    * @param nounA the first noun
    * @param nounB the second noun
    * @return distance between the first and the second nouns 
    */
   public int distance(String nounA, String nounB) {
       validate(nounA, nounB);
	   return sap.length(nouns.get(nounA), nouns.get(nounB));
   }

    /**
     * Define a synset (second field of nouns.txt) that is the common ancestor
     * of nounA and nounB in a shortest ancestral path (defined below).
     * @return common ancestor of two nouns
     */
   public String sap(String nounA, String nounB) {
       validate(nounA, nounB);
       int ancestor = sap.ancestor(nouns.get(nounA),  nouns.get(nounB));
	   return ids.get(ancestor);
   }

    private void validate(String nounA, String nounB) {
        if (!isNoun(nounA)) throw new IllegalArgumentException("Not a WordNet noun: " + nounA);
        if (!isNoun(nounB)) throw new IllegalArgumentException("Not a WordNet noun: " + nounB);
    }

    // do unit testing of this class
    public static void main(String[] args) {
    }
}
