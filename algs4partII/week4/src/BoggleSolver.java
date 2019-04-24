/* *****************************************************************************
 *  Name: Oleksii Prosianko
 *  Date: 2019/04/24
 *  Description: boggle solver
 ******************************************************************************/

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.TST;

/**
 * Boggle solver is immutable data that finds all valid words in a given
 * Boggle board, using a given dictionary.
 */
public class BoggleSolver {

    private final TST<Boolean> dictioanry;

    private static class Dice {
        public final int i;
        public final int j;

        public Dice(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    /**
     *  Algorythm implementaion based on depth-first search to enumerate
     *  all strings that can be composed by following sequences of adjacent
     *  dice.
     *  Algorythm enumerates all simple paths in the Boggle graph
     *  (there is no explicit graph creation).
     */
    private class DfsDices {

        private final BoggleBoard board;
        private boolean[][] marked;
        private SET<String> pathes;

        /**
         * @param board game board
         * @param si from row    0 .. board.rows() - 1
         * @param sj from column 0 .. board.cols() - 1
         */
        public DfsDices(BoggleBoard board, int si, int sj) {
            this.board = board;
            this.marked = new boolean[board.rows()][board.cols()];
            this.pathes = new SET<>();
            dfs(si, sj, "");
        }

        public SET<String> pathes() {
            return this.pathes;
        }

        private void dfs(int i, int j, String path) {
            marked[i][j] = true;
            path = path + getLetter(i, j);
            if (path.length() > 1 && !isPrefix(path)) {
                marked[i][j] = false;
                return;
            }
            pathes.add(path);
            for (Dice dice: adj(i, j)) {
                if (!marked[dice.i][dice.j]) {
                    dfs(dice.i, dice.j, path);
                    marked[dice.i][dice.j] = false;
                }
            }
        }

        private String getLetter(int i, int j) {
            char ch = board.getLetter(i, j);
            if (ch == 'Q') return "QU";
            else return Character.toString(ch);
        }

        private Bag<Dice> adj(int i, int j) {
            Bag<Dice> all = new Bag<>();
            int n = board.rows();
            int m = board.cols();

            all.add(new Dice(i - 1, j - 1));
            all.add(new Dice(i + 1, j + 1));
            all.add(new Dice(i - 1, j));
            all.add(new Dice(i + 1, j));
            all.add(new Dice(i, j - 1));
            all.add(new Dice(i, j + 1));
            all.add(new Dice(i - 1, j + 1));
            all.add(new Dice(i + 1, j - 1));

            Bag<Dice> res = new Bag<>();
            for (Dice d: all) {
                if (d.i < 0)        continue;
                if (d.i > n - 1)    continue;
                if (d.j < 0)        continue;
                if (d.j > m - 1)    continue;
                res.add(d);
            }
            return res;
        }
    }

    /**
     * Initializes the data structure using the given array of strings as the
     * dictionary.
     * We can assume each word in the dictionary contains only the uppercase
     * letters A through Z.
     * @param dictionary array of strings as the dictionary
     */
    public BoggleSolver(String[] dictionary) {
        this.dictioanry = new TST<>();
        for (String s: dictionary) this.dictioanry.put(s, Boolean.TRUE);
    }

    /**
     * @param board a bord
     * @return the set of all valid words in the given Boggle board
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        SET<String> words = new SET<>();
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                DfsDices dfs = new DfsDices(board, i, j);
                for (String path: dfs.pathes()) {
                    if (path.length() > 2 && isInDictionary(path)) {
                        words.add(path);
                    }
                }
            }
        }
        return words;
    }

    /**
     * Words are scored according to their length, using this table:
     * 0–2 ->  0
     * 3–4 ->  1
     * 5   ->  2
     * 6   ->  3
     * 7   ->  5
     * 8+  -> 11
     * We can assume the word contains only the uppercase letters A through Z.
     * @param word a word
     * @return the score of the given word if it is in the dictionary,
     * zero otherwise.
     */
    public int scoreOf(String word) {
        if (word.length() < 3 || !isInDictionary(word)) return 0;
        else if (word.length()  < 5) return 1;
        else if (word.length() == 5) return 2;
        else if (word.length() == 6) return 3;
        else if (word.length() == 7) return 5;
        else return 11;
    }

    private boolean isInDictionary(String word) {
        return dictioanry.contains(word);
    }

    private boolean isPrefix(String str) {
        return dictioanry.keysWithPrefix(str).iterator().hasNext();
    }


    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        int n = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
            n++;
        }
        StdOut.println("Score = " + score + " n = " + n);
    }
}

