/* *****************************************************************************
 *  Name: Oleksii Prosianko
 *  Date: 2019/10/16
 *  Description: Initial commit
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * Given a typical English text file, transform it into a text file in which
 * sequences of the same character occur near each other many times.
 */
public class BurrowsWheeler {

    /**
     * Apply Burrows-Wheeler transform, reading from standard input and writing
     * to standard output.
     */
    public static void transform() {
        String text = BinaryStdIn.readString();
        CircularSuffixArray sca = new CircularSuffixArray(text);

        for (int first = 0; first < sca.length(); first++)
            if (sca.index(first) == 0) {
                BinaryStdOut.write(first);
                break;
            }

        for (int i = 0; i < sca.length(); i++) {
            int index = sca.index(i);
            int k = (index == 0) ? sca.length() - 1 : index - 1;
            BinaryStdOut.write(text.charAt(k));
        }

        BinaryStdOut.close();
    }

    /**
     * Apply Burrows-Wheeler inverse transform, reading from standard input and
     * writing to standard output
     */
    public static void inverseTransform() {
        // TODO
    }

    /**
     * Entry point. Useage example:
     * <pre>
     * {@code BurrowsWheeler - < abra.txt }
     * </pre>
     * @param args if args[0] is '-', apply Burrows-Wheeler transform
     *             if args[0] is '+', apply Burrows-Wheeler inverse transform
     */
    public static void main(String[] args) {
        if      (args[0].equals("-")) transform();
        else if (args[0].equals("+")) inverseTransform();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
