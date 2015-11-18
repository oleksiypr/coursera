package week1;

/**
 * WordNet groups words into sets of synonyms called synsets and describes semantic relationships between them. One such 
 * relationship is the is-a relationship, which connects a hyponym (more specific synset) to a hypernym (more general synset). 
 * @author Oleksiy Prosyanko
 */
public class WordNet {
	/**
	 * Construct new instance.
	 * @param synsets lists all the (noun) synsets in WordNet.
	 * @param hypernyms file that contains the hypernym relationships.
	 */
	public WordNet(String synsets, String hypernyms) {
	}

   /**
    * All WordNet nouns.
    * @return all WordNet nouns.
    */
   public Iterable<String> nouns() {
		return null;
	}

   /**
    * Define is the word a WordNet noun?
    * @param word the word
    * @return true iff the word is a WordNet noun
    */
   public boolean isNoun(String word) {	   
	   return false;
   }

   /**
    * Compute distance between nounA and nounB (defined below).
    * @param nounA the first noun
    * @param nounB the second noun
    * @return distance between the first and the second nouns 
    */
   public int distance(String nounA, String nounB) {
	   return -1;
   }

    /**
     * Define a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
     * in a shortest ancestral path (defined below).
     * @return common ancestor of two nouns
     */
   public String sap(String nounA, String nounB) {
	   return null;
   }

   // do unit testing of this class
   public static void main(String[] args) {}
}
