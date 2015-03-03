/**
 * The data type that uses a 2d-tree to implement set of points in the unit square. 
 * A 2d-tree is a generalization of a BST to two-dimensional keys.
 * 
 * @author Oleksiy Prosyanko
 */
public class KdTree {
    private static final boolean X = true;
    private static final boolean Y = false;
    
    static abstract class Node implements Comparable<Point2D> {
        final Point2D point;
        final  RectHV rectangle;
        Node left = null;
        Node right = null;
        int count = 1;
        
        public Node(Point2D point, RectHV rectangle) { 
            this.point = point;
            this.rectangle = rectangle;
        }        
        abstract boolean dimention();
        abstract RectHV rectRight();
        abstract RectHV rectLeft();
        boolean nextDimention() { return !dimention(); }        
    }
    
    static class Xnode extends Node {       
        public Xnode(Point2D point, RectHV rectangle) { super(point, rectangle); }
        @Override boolean dimention() { return X; }      

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
        @Override
        RectHV rectRight() {
            double xmin = point.x();
            double ymin = rectangle.ymin();
            double xmax = rectangle.xmax();
            double ymax = rectangle.ymax();
            return new RectHV(xmin, ymin, xmax, ymax);
        }
        
        @Override
        RectHV rectLeft() {
            double xmin = rectangle.xmin();
            double ymin = rectangle.ymin();
            double xmax = point.x();
            double ymax = rectangle.ymax();
            return new RectHV(xmin, ymin, xmax, ymax);
        }    
    }
    
    static class Ynode extends Node {        
        public Ynode(Point2D point, RectHV rectangle) { super(point, rectangle); }
        @Override boolean dimention() { return Y; }      

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
        @Override
        RectHV rectRight() {
            double xmin = rectangle.xmin();
            double ymin = point.y();
            double xmax = rectangle.xmax();
            double ymax = rectangle.ymax();
            return new RectHV(xmin, ymin, xmax, ymax);
        }
        @Override
        RectHV rectLeft() {
            double xmin = rectangle.xmin();
            double ymin = rectangle.ymin();
            double xmax = rectangle.xmax();
            double ymax = point.y();
            return new RectHV(xmin, ymin, xmax, ymax);
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
		root = insert(root, p, new RectHV(0.0, 0.0, 1.0, 1.0), X);
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
        for (Node node: nodes()) {
	        double x = node.point.x();
	        double y = node.point.y();
	        
	        StdDraw.setPenColor(StdDraw.BLACK);
	        StdDraw.setPenRadius(.01);
	        StdDraw.point(x, y);

            StdDraw.setPenRadius();
            double x0 = 0.0, x1 = 0.0;
            double y0 = 0.0, y1 = 0.0;
	        if (node.dimention() == X) {
	            StdDraw.setPenColor(StdDraw.RED);
	            x0 = x; y0 = node.rectangle.ymin();
	            x1 = x; y1 = node.rectangle.ymax();
	        }    
	        
            if (node.dimention() == Y) {
                StdDraw.setPenColor(StdDraw.BLUE);
                x0 = node.rectangle.xmin(); y0 = y;
                x1 = node.rectangle.xmax(); y1 = y;
            } 
            
            StdDraw.line(x0, y0, x1, y1);
	    }
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
	
    public Node insert(Node node, Point2D p, RectHV rectangle, boolean dimention) {
        if (node == null) {
            if (dimention == X) return new Xnode(p, rectangle);
            if (dimention == Y) return new Ynode(p, rectangle);    
        }
        
        int cmp = node.compareTo(p);
        if (cmp < 0) node.right = insert(node.right, p, node.rectRight(), node.nextDimention());
        if (cmp > 0) node.left = insert(node.left, p, node.rectLeft(), node.nextDimention());
        
        node.count = 1 + size(node.left) + size(node.right);        
        return node;
    }
    
    private Iterable<Node> nodes() {
        Queue<Node> queue = new Queue<Node>();
        inorder(root, queue);
        return queue;        
    }
	
	private int size(Node node) {
        if (node == null) return 0;
        return node.count;
    }
    
	private void inorder(Node node, Queue<Node> queue) {
	    if (node == null) return;
	    inorder(node.left, queue);
	    queue.enqueue(node);
	    inorder(node.right, queue);
	}
	
    // unit testing of the methods (optional)
	public static void main(String[] args) {} 
}