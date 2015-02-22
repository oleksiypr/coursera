import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A randomized queue is similar to a stack or queue, except that the item
 * removed is chosen uniformly at random from items in the data structure.
 * 
 * @author Oleksiy_Prosyanko
 * @param <Item> items type
 */
public class RandomizedQueue<Item> implements Iterable<Item> {
    private static final int DEFAULT_SIZE = 2;

    private Item[] q;
    private int N;

    /**
     * Construct an empty randomized queue.
     */
    @SuppressWarnings("unchecked")
    public RandomizedQueue() {
        this.N = 0;
        this.q = (Item[]) new Object[DEFAULT_SIZE];
    }

    /**
     * Is the queue empty?
     * @return true if empty
     */
    public boolean isEmpty() {
        return N == 0;
    }

    /**
     * Return the number of items on the queue.
     * @return the number of items on the queue
     */
    public int size() {
        return N;
    }

    /**
     * Add the item.
     * @param item an item
     */
    public void enqueue(Item item) {
        if (item == null) throw new NullPointerException();
        if (N == q.length) resize(2*q.length);   
        q[N++] = item;     
    }

    /**
     * Delete and return a random item.
     * @return random item
     */
    public Item dequeue() {
        if (isEmpty()) throw new NoSuchElementException();        
        int r = StdRandom.uniform(0, N);
        Item item = q[r];
        swap(r, N - 1);        
        removeLast();
        return item;
    }

    /**
     * Return (but do not delete) a random item.
     * @return random item
     */
    public Item sample() {
        if (isEmpty()) throw new NoSuchElementException();     
        return q[StdRandom.uniform(0, N)];
    }

    /**
     * Independent iterator over items in random order.
     * @return independent iterator over items in random order
     */
    public Iterator<Item> iterator() {
        return new RandomArrayIterator();
    }
    
    private void resize(int capacity) {
        @SuppressWarnings("unchecked")
        Item[] temp = (Item[]) new Object[capacity];
        for (int i = 0; i < N; i++) {
            temp[i] = q[i];
        }
        q = temp;
    }
    
    private void swap(int i, int j) {
        Item tmp = q[i];
        q[i] = q[j];
        q[j] = tmp;        
    }
    
    private void removeLast() {
        q[N - 1] = null;                           
        N--;
        if (N > 0 && N == q.length/4) resize(q.length/2);        
    }
    
    private class RandomArrayIterator implements Iterator<Item> {
        private int current;
        private int[] indexes;

        public RandomArrayIterator() {
            current = 0;
            indexes = new int[N];
            for (int i = 0; i < N; i++) indexes[i] = i;
            StdRandom.shuffle(indexes);
        }

        @Override
        public boolean hasNext() {
            return current < N;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();
            int index = indexes[current++];
            return q[index];
        }
    }

    public static void main(String[] args) {
        RandomizedQueue<Integer> rq;
        
        rq = new RandomizedQueue<Integer>();
        StdOut.println("New random queue is emtpy: " + rq.isEmpty());
        StdOut.println("New random queue size is zero, size: " + rq.size());
        StdOut.println();
        
        rq = new RandomizedQueue<Integer>();
        rq.enqueue(1);
        StdOut.println("New item added. Is emtpy: " + rq.isEmpty() + ", size: " + rq.size());
        StdOut.println("Sample: " + rq.sample() + ", is emtpy: " + rq.isEmpty() + ", size: " + rq.size());
        StdOut.println("Deque: " + rq.dequeue());
        StdOut.println("Dequed. Is emtpy: " + rq.isEmpty() + ", size: " + rq.size());
        StdOut.println();
        
        rq = new RandomizedQueue<Integer>();
        StdOut.println("Epmty deque contains nothing");
        for (Integer i: rq) {
            StdOut.println("Empty: " + i);
        }
        StdOut.println();
        
        rq = new RandomizedQueue<Integer>();
        rq.enqueue(0);
        rq.enqueue(1);
        rq.enqueue(2);
        rq.enqueue(3);
        rq.enqueue(4);
        rq.enqueue(5);
        rq.enqueue(6);
        rq.enqueue(7);        
        StdOut.println("Items added, is epmty: " + rq.isEmpty() + ", size: " + rq.size());
        for (Integer i: rq) {
            StdOut.println("item: " +  i);
        }
        StdOut.println();
        
        rq = new RandomizedQueue<Integer>();
        rq.enqueue(0);
        rq.enqueue(1);
        rq.enqueue(2);
        rq.enqueue(3);
        rq.enqueue(4);
        rq.enqueue(5);
        rq.enqueue(6);
        rq.enqueue(7);
        StdOut.println("Items added, is epmty: " + rq.isEmpty() + ", size: " + rq.size());
        int n = rq.size();
        for (int i = 0; i < n; i++) {
            StdOut.println("item: " +  rq.dequeue());
        }
        StdOut.println("Dequed. Is emtpy: " + rq.isEmpty() + ", size: " + rq.size());
    }
}