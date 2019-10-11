/* *****************************************************************************
 *  Name: Oleksii Prosianko
 *  Date: 2019/10/11
 *  Description: `encode/decode` solved
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * Move-to-front encoding is to maintain an ordered sequence of the 256 extended
 * ASCII characters. Initialize the sequence by making the ith character in the
 * sequence equal to the ith extended ASCII character. Now, read each 8-bit
 * character c from standard input one at a time, output the 8-bit index in the
 * sequence where c appears, and move c to the front.
 */
public class MoveToFront {

    /**
     * Extended ASCII radix.
     */
    private static final int R = 256;

    /**
     * Apply move-to-front encoding, reading from standard input and writing to
     * standard output.
     */
    public static void encode() {
        char[] alphabet = alphabet();
        while (!BinaryStdIn.isEmpty()) {
            char index;
            char in, ch = in = BinaryStdIn.readChar();
            for (index = 0; index < R; index ++) {
                char current = alphabet[index];
                alphabet[index] = ch;
                ch = current;
                if (in == current) break;
            }
            BinaryStdOut.write(index);
        }
        BinaryStdOut.close();
    }

    /**
     * Apply move-to-front decoding, reading from standard input and writing to
     * standard output.
     */
    public static void decode() {
        char[] alphabet = alphabet();
        while (!BinaryStdIn.isEmpty()) {
            char index = BinaryStdIn.readChar();
            char ch = alphabet[index];
            BinaryStdOut.write(ch);
            for (int k = 0; k <= index; k++) {
                char buff = alphabet[k];
                alphabet[k] = ch;
                ch = buff;
            }
        }
        BinaryStdOut.close();
    }

    private static char[] alphabet() {
        char[] alphabet = new char[R];
        for (char i = 0; i < R; i++) alphabet[i] = i;
        return alphabet;
    }

    /**
     * Entry point. Useage example:
     * <pre>
     * {@code MoveToFront - < abra.txt }
     * </pre>
     * @param args if args[0] is '-', apply move-to-front encoding
     *             if args[0] is '+', apply move-to-front decoding
     */
    public static void main(String[] args) {
        if      (args[0].equals("-")) encode();
        else if (args[0].equals("+")) decode();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
