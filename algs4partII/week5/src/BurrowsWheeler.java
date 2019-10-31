/* *****************************************************************************
 *  Name: Oleksii Prosianko
 *  Date: 2019/10/31
 *  Description: solved
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * Given a typical English text file, transform it into a text file in which
 * sequences of the same character occur near each other many times.
 */
public class BurrowsWheeler {

    /**
     * Extended ASCII radix.
     */
    private static final int R = 256;

    /**
     * Apply Burrows-Wheeler transform, reading from standard input and writing
     * to standard output.
     */
    public static void transform() {
        String text = BinaryStdIn.readString();
        CircularSuffixArray sca = new CircularSuffixArray(text);
        final int n = sca.length();

        for (int first = 0; first < n; first++)
            if (sca.index(first) == 0) {
                BinaryStdOut.write(first);
                break;
            }

        for (int i = 0; i < n; i++) {
            int index = sca.index(i);
            int k = (index == 0) ? n - 1 : index - 1;
            BinaryStdOut.write(text.charAt(k));
        }

        BinaryStdOut.close();
    }

    /**
     * Apply Burrows-Wheeler inverse transform, reading from standard input and
     * writing to standard output
     */
    public static void inverseTransform() {
        final int first = BinaryStdIn.readInt();
        final String text = BinaryStdIn.readString();
        final int n = text.length();

        // compute frequency counts
        int[] count = new int[R + 1];
        for (int i = 0; i < n; i++) {
            int shifted = text.charAt(i) + 1;
            count[shifted]++;
        }
        // compute cumulates
        for (int r = 0; r < R; r++) count[r + 1] += count[r];

        // move data
        char[] sorted = new char[n];
        int[] nexts = new int[n];
        for (int next = 0; next < n; next++) {
            char ch = text.charAt(next);
            int index = count[ch]++;
            nexts[index] = next;
            sorted[index] = text.charAt(next);
        }

        // deduce original text
        for (int i = 0, next = first; i < n; i++) {
            BinaryStdOut.write(sorted[next]);
            next = nexts[next];
        }
        BinaryStdOut.close();
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
