import edu.princeton.cs.algs4.*;

/**
 * A client program Subset.java that takes a command-line integer k; 
 * reads in a sequence of N strings from standard input using StdIn.readString(); 
 * and prints out exactly k of them, uniformly at random. Each item from the sequence 
 * can be printed out at most once. You may assume that 0 ≤ k ≤ N, where N is the 
 * number of string on standard input.  
 * 
 * @author Oleksiy_Prosyanko
 */
public class Subset {
    public static void main(String[] args) {
        RandomizedQueue<String> rq = new RandomizedQueue<String>();
        while (!StdIn.isEmpty()) {
            String s = StdIn.readString();
            rq.enqueue(s);
        }
        
        int k = Integer.parseInt(args[0]);
        for (int i = 0; i < k; i++) {
            StdOut.println(rq.dequeue());
        }
    }
}
