import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Deque is a generalization of a stack and a queue that supports inserting and
 * removing items from either the front or the back of the data structure.
 * 
 * @author Oleksiy_Prosyanko
 * @param <Item> items type
 */
public class Deque<Item> implements Iterable<Item> {
    private Node first;
    private Node last;
    private int N;
    
    private class Node {
        private final Item item;
        private Node next;
        private Node prev;
        
        public Node(Item item, Node next, Node prev) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }        
    }
    
    /**
     * Construct an empty deque.
     */
    public Deque() {
    }

    /**
     * Is the deque empty?
     * @return true if empty
     */
    public boolean isEmpty() {
        return first == null;
    }

    /**
     * Number of items on the deque.
     * @return number of items on the deque
     */
    public int size() {
        return N;
    }

    /**
     * Insert the item at the front.
     * @param item an item
     */
    public void addFirst(Item item) {
        if (item == null) throw new NullPointerException();        
        Node oldFirst = first;
        Node newFirst = new Node(item, oldFirst, null);    
        if (isEmpty()) last = newFirst;
        first = newFirst;
        N++;
    }

    /**
     * Insert the item at the end.
     * @param item an item
     */
    public void addLast(Item item) {
        if (item == null) throw new NullPointerException();         
        Node oldLast = last;
        last = new Node(item, null, oldLast);
        if (isEmpty()) first = last;
        else oldLast.next = last;  
        N++;
    }

    /**
     * Delete and return the item at the front.
     * @return first an item
     */
    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException();        
        Item item = first.item;
        first = first.next;
        if (!isEmpty()) first.prev = null;
        N--;
        return item;
    }

    /**
     * Delete and return the item at the end.
     * @return last an item
     */
    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException();
        
        Item item = last.item;
        last = last.prev;
        if (last == null) first = null;
        if (!isEmpty()) last.next = null;
        N--;
        return item;
    }

    /**
     * An iterator over items in order from front to end.
     * @return an iterator over items in order from front to end
     */
    public Iterator<Item> iterator() {
        return new ListIterator();
    }
    
    private class ListIterator implements Iterator<Item> {
        private Node current = first;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Item next() {
            if (!hasNext()) throw new NoSuchElementException();            
            Item item = current.item;
            current = current.next;
            return item;
        }
        
        public void remove() {
            throw new UnsupportedOperationException(); 
        }
    }

    public static void main(String[] args) {
        Deque<String> dq;
        
        dq = new Deque<String>();
        StdOut.println("New deque is emtpy: " + dq.isEmpty());
        StdOut.println("New deque size is zero, size: " + dq.size());
        StdOut.println();
        
        dq = new Deque<String>();
        dq.addFirst("First");
        StdOut.println("Add first, is epmty: " + dq.isEmpty() + ", size: " + dq.size());
        StdOut.println("Remove first: " + dq.removeFirst() + ", size: " + dq.size() + ", is empty: " + dq.isEmpty());
        StdOut.println();
        
        dq = new Deque<String>();
        dq.addLast("Last");
        StdOut.println("Add last, is epmty: " + dq.isEmpty() + ", size: " + dq.size());
        StdOut.println("Remove last: " + dq.removeLast() + ", size: " + dq.size() + ", is empty: " + dq.isEmpty());
        StdOut.println();
        
        dq = new Deque<String>();
        dq.addFirst("Last");
        StdOut.println("Add first, is epmty: " + dq.isEmpty() + ", size: " + dq.size());
        StdOut.println("Remove last: " + dq.removeLast() + ", size: " + dq.size() + ", is empty: " + dq.isEmpty());
        StdOut.println();
        
        dq = new Deque<String>();
        dq.addLast("First");
        StdOut.println("Add last, is epmty: " + dq.isEmpty() + ", size: " + dq.size());
        StdOut.println("Remove first: " + dq.removeFirst() + ", size: " + dq.size() + ", is empty: " + dq.isEmpty());
        StdOut.println();
        
        dq = new Deque<String>();
        dq.addFirst("Middle");
        dq.addFirst("First");
        dq.addLast("Last");
        StdOut.println("Items added, is epmty: " + dq.isEmpty() + ", size: " + dq.size());
        StdOut.println("Remove first: " + dq.removeFirst() + ", size: " + dq.size() + ", is empty: " + dq.isEmpty());
        StdOut.println("Remove last: " + dq.removeLast() + ", size: " + dq.size() + ", is empty: " + dq.isEmpty());
        StdOut.println("Remove first: " + dq.removeFirst() + ", size: " + dq.size() + ", is empty: " + dq.isEmpty());
        StdOut.println();
        
        
        dq = new Deque<String>();
        StdOut.println("Epmty deque contains nothing");
        for (String s: dq) {
            StdOut.println("Empty: " + s);
        }
        StdOut.println();
        
        
        dq = new Deque<String>();
        dq.addFirst("1");
        dq.addLast("2");
        dq.addLast("3");
        dq.addLast("4");
        dq.addFirst("0");
        StdOut.println("Items added, is epmty: " + dq.isEmpty() + ", size: " + dq.size());
        for (String s: dq) {
            StdOut.println("item: " +  s);
        }
    }
}