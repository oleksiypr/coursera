/**
 * The data type that uses a 2d-tree to implement set of points in the unit square. 
 * A 2d-tree is a generalization of a BST to two-dimensional keys.
 * 
 * @author Oleksiy Prosyanko
 */
public class KdTree {
    private static final boolean HORISONTAL = true;
    private static final boolean VERTICAL = false;
    
    static abstract class Node implements Comparable<Point2D> {
        final Point2D point;
        Node left = null;
        Node right = null;
        int count = 1;
        
        public Node(Point2D point) { this.point = point; }        
        abstract boolean nextDimention();
    }
    
    static class Xnode extends Node {
        public Xnode(Point2D point) { super(point); }
        @Override boolean nextDimention() { return HORISONTAL; }      

        @Override
        public int compareTo(Point2D q) {
            double dx = this.point.x() - q.x();
            if (dx > 0.0) return +1;
            if (dx < 0.0) return -1;
            if (dx == 0.0) {
                double dy = this.point.y() - q.y();
                if (dy > 0.0) return +1;
                if (dy < 0.0) return -1;
            }
            return 0;
        }    
    }
    
    static class Ynode extends Node {
        public Ynode(Point2D point) { super(point); }
        @Override boolean nextDimention() { return VERTICAL; }      

        @Override
        public int compareTo(Point2D q) {
            double dy = this.point.y() - q.y();
            if (dy > 0.0) return +1;
            if (dy < 0.0) return -1;
            if (dy == 0.0) {
                double dx = this.point.x() - q.x();
                if (dx > 0.0) return +1;
                if (dx < 0.0) return -1;
            }
            return 0;
        }    
    }
    
    private Node root;
    
	/**
	 *  Construct an empty set of points.
	 */
	public KdTree() {
		root = null;
	}     
	
	/**
	 * Is the set empty? 
	 * @return true iff set is empty
	 */
	public boolean isEmpty() {
		return size() == 0;
	}   
	
    /**
     * Number of points in the set.
     * @return number of points in the set
     */
	public int size() {
		return size(root);
	} 
	
    /**
     * Add the point to the set (if it is not already in the set).
     * @param p point to add
     */
	public void insert(Point2D p) {
		root = insert(root, p, VERTICAL);
	}
	
    /**
     * Does the set contain point p?
     * @param p point to be checked
     * @return true iff the set contains point
     */
	public boolean contains(Point2D p) {
		//TODO
		return false;
	} 
	
    /**
     * Draw all points to standard draw.
     */
	public void draw() {
		//TODO
	} 
	
    /**
     * All points that are inside the rectangle
     * @param rect rectangle
     * @return points
     */
	public Iterable<Point2D> range(RectHV rect) {
		//TODO
		return null;
	} 
	
    /**
     * F nearest neighbor in the set to point p; null if the set is empty
     * @param p
     * @return
     */
	public Point2D nearest(Point2D p) {
		//TODO 
		return null;
	} 
	
	/**
	 * To be used for testing  purpose.
	 * @return root of this tree
	 */
	Node root() {
	    return this.root;
	}
	
    public Node insert(Node node, Point2D p, boolean dimention) {
        if (node == null) {
            if (dimention == VERTICAL) return new Xnode(p);
            if (dimention == HORISONTAL) return new Ynode(p);    
        }
        
        int cmp = node.compareTo(p);
        if (cmp < 0) node.right = insert(node.right, p, node.nextDimention());
        if (cmp > 0) node.left = insert(node.left, p, node.nextDimention());
        
        node.count = 1 + size(node.left) + size(node.right);        
        return node;
    }
	
	private int size(Node x) {
        if (x == null) return 0;
        return x.count;
    }
    
    // unit testing of the methods (optional)
	public static void main(String[] args) {} 
}
