/* *****************************************************************************
 *  Name: Oleksii Prosianko
 *  Date: 2019/01/29
 *  Description: Outcast detection.
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * Outcast detection. Given a list of WordNet nouns x1, x2, ..., xn, which noun
 * is the least related to the others? To identify an outcast, compute the sum
 * of the distances between each noun and every other one:
 *
 *     di = distance(xi, x1) + distance(xi, x2) + ... + distance(xi, xn)
 *
 * and return a noun xt for which dt is maximum.
 * Note that distance(xi, xi) = 0, so it will not contribute to the sum.
 */
public class Outcast {

    private final WordNet wordnet;

    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    /**
     * Assume that argument to outcast() contains only valid wordnet nouns
     * (and that it contains at least two such nouns).
     * @param nouns array of WordNet nouns
     * @return an outcast
     */
    public String outcast(String[] nouns) {
        int max = -1;
        int t = -1;
        for (int i = 0; i < nouns.length; i++) {
            int d = 0;
            for (int j = 0; j < nouns.length; j++) {
                d += wordnet.distance(nouns[i], nouns[j]);
            }
            if (d > max) {
                max = d;
                t = i;
            }
        }
        return nouns[t];
    }

    /**
     * The following test client takes from the command line the name of a
     * synset file, the name of a hypernym file, followed by the names of
     * outcast files, and prints out an outcast in each file:
     */
    public static void main(String[] args)  {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
