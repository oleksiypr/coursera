/* *****************************************************************************
 *  Name: Oleksii Prosianko
 *  Date: 2019/10/15
 *  Description: solved
 **************************************************************************** */

import edu.princeton.cs.algs4.Merge;
import edu.princeton.cs.algs4.StdOut;

/**
 * To efficiently implement the key component in the Burrows–Wheeler transform,
 * we will use a fundamental data structure known as the circular suffix array,
 * which describes the abstraction of a sorted array of the n circular suffixes
 * of a string of length n. As an example, consider the string "ABRACADABRA!" of
 * length 12. The table below shows its 12 circular suffixes and the result of
 * sorting them.
 *
 *  i       Original Suffixes           Sorted Suffixes         index[i]
 *  --    -----------------------     -----------------------    --------
 *  0    A B R A C A D A B R A !     ! A B R A C A D A B R A    11
 *  1    B R A C A D A B R A ! A     A ! A B R A C A D A B R    10
 *  2    R A C A D A B R A ! A B     A B R A ! A B R A C A D    7
 *  3    A C A D A B R A ! A B R     A B R A C A D A B R A !    0
 *  4    C A D A B R A ! A B R A     A C A D A B R A ! A B R    3
 *  5    A D A B R A ! A B R A C     A D A B R A ! A B R A C    5
 *  6    D A B R A ! A B R A C A     B R A ! A B R A C A D A    8
 *  7    A B R A ! A B R A C A D     B R A C A D A B R A ! A    1
 *  8    B R A ! A B R A C A D A     C A D A B R A ! A B R A    4
 *  9    R A ! A B R A C A D A B     D A B R A ! A B R A C A    6
 * 10    A ! A B R A C A D A B R     R A ! A B R A C A D A B    9
 * 11    ! A B R A C A D A B R A     R A C A D A B R A ! A B    2
 *
 * We define index[i] to be the index of the original suffix that appears ith in
 * the sorted array. For example, index[11] = 2 means that the 2nd original
 * suffix appears 11th in the sorted order (i.e., last alphabetically).
 */
public class CircularSuffixArray {

    private final String text;
    private final int n;
    private final CircularSuffix[] suffixes;

    /**
     * This nested class represents a circular suffix implicitly (via a
     * reference to the input string and a pointer to the first character in
     * the circular suffix).
     */
    private final class CircularSuffix implements Comparable<CircularSuffix> {

        final int from;

        /**
         * Implicit suffix array represented by an index to a char of
         * original text.
         * @param from index of a char of original text circular suffix starts
         *             from, [0, n - 1]
         */
        CircularSuffix(int from) {
            this.from = from;
        }

        char charAt(int i) {
            return text.charAt((from + i) % n);
        }

        @Override
        public int compareTo(CircularSuffix that) {
            if (this.from == that.from) return 0;
            for (int i = 0; i < n; i++) {
                int cmp = this.charAt(i) - that.charAt(i);
                if (cmp != 0) return cmp;
            }
            return 0;
        }
    }

    /**
     * Circular suffix array of string.
     * @param text a string
     */
    public CircularSuffixArray(String text) {
        if (text == null)
            throw new IllegalArgumentException("argument is null");

        this.text = text;
        this.n = text.length();

        this.suffixes = new CircularSuffix[n];
        for (int i = 0; i < n; i++) suffixes[i] = new CircularSuffix(i);
        Merge.sort(this.suffixes);
    }

    /**
     * @return length of s
     */
    public int length() {
        return n;
    }

    /**
     * @param i  -th sorted suffix
     * @return returns index of ith sorted suffix
     */
    public int index(int i) {
        if (i < 0 || i >= n)
            throw new IllegalArgumentException("Index out of bound");

        return suffixes[i].from;
    }

    private static final class TestCounter {
        private int tests;
        private int passed;
        private int failed;

        void success() {
            tests++;
            passed++;
        }

        void fail() {
            tests++;
            failed++;
        }

        int total() { return tests; }
        int passed() { return passed; }
        int failed() { return failed; }
    }

    /**
     * Unit testing (required). This main() method must call each public method
     * directly and help verify that they work as prescribed (e.g., by printing
     * results to standard output).
     */
    public static void main(String[] args) {
        TestCounter testCounter = new TestCounter();

        try {
            new CircularSuffixArray(null);
            testCounter.fail();
            StdOut.println("Check for constructor null ==> failed: " +
                "constructor should " +
                "throw IllegalArgumentException on null argument");
        } catch (IllegalArgumentException ex) {
            testCounter.success();
            StdOut.println("Check for constructor null ==> passed");
        }
        // Commented because of Coursera grader restrictions
        /*catch (Throwable th) {
            testCounter.fail();
            StdOut.println("Check for constructor null ==> failed: " +
                "constructor should " +
                "throw IllegalArgumentException on null argument");
        }*/

        String s = "ABRACADABRA!";
        int n = s.length();
        CircularSuffixArray csa = new CircularSuffixArray(s);

        if (csa.length() == n) {
            testCounter.success();
            StdOut.println("Check length ==> passed");
        } else {
            testCounter.fail();
            StdOut.printf("Check length ==> failed: %d != %d\n", csa.length(), n);
        }

        try {
            csa.index(-1);
            csa.index(n);
            testCounter.fail();
            StdOut.println("index out of range ==> failed: index should " +
                "throw IllegalArgumentException if out of range");
        } catch (IllegalArgumentException ex) {
            testCounter.success();
            StdOut.println("index out of range ==> passed");
        }
        // Commented because of Coursera grader restrictions
        /*catch (Throwable th) {
            testCounter.fail();
            StdOut.println("index out of range ==> failed: index should " +
                "throw IllegalArgumentException if out of range");
        }*/

        int[] index = new int[] { 11, 10, 7, 0, 3, 5, 8, 1, 4, 6, 9, 2 };
        for (int i = 0; i < index.length; i++) {
            int actual = csa.index(i);
            int expected = index[i];
            if (actual == expected) {
                testCounter.success();
                StdOut.printf("index(%d) ==> passed\n", i);
            } else {
                testCounter.fail();
                StdOut.printf("index(%d) ==> failed: %d != %d\n",
                    i, actual, expected);
            }
        }

        StdOut.printf("Testing finished. Total: %d, passed: %d, failed: %d",
            testCounter.total(), testCounter.passed(), testCounter.failed());
    }
}
