/* *****************************************************************************
 *  Name: Oleksii Prosianko
 *  Date: 2019/05/05
 *  Description: Initial commit
 **************************************************************************** */

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

    /**
     * Circular suffix array of string.
     * @param s a string
     */
    public CircularSuffixArray(String s) {
        // TODO
    }

    /**
     * @return length of s
     */
    public int length() {
        // TODO
        return -1;
    }

    /**
     * @param i  -th sorted suffix
     * @return returns index of ith sorted suffix
     */
    public int index(int i) {
        // TODO
        return -1;
    }

    /**
     * Unit testing (required). This main() method must call each public method
     * directly and help verify that they work as prescribed (e.g., by printing
     * results to standard output).
     */
    public static void main(String[] args) {

    }
}
