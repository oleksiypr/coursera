import java.util.TreeSet;

/**
 * The data type that represents a set of points in the unit square. 
 * Implemented by using a red-black BST.
 * 
 * @author Oleksiy Prosyanko
 */
public class PointSET {
    private TreeSet<Point2D> points;
	
	/**
	 *  Construct an empty set of points.
	 */
	public PointSET() {
		points = new TreeSet<Point2D>();
	}     
	
	/**
	 * Is the set empty? 
	 * @return true iff set is empty
	 */
	public boolean isEmpty() {
		return points.isEmpty();
	}   
	
    /**
     * Number of points in the set.
     * @return number of points in the set
     */
	public int size() {
		return points.size();
	} 
	
    /**
     * Add the point to the set (if it is not already in the set).
     * @param p point to add
     */
	public void insert(Point2D p) {
		points.add(p);
	}
	
    /**
     * Does the set contain point p?
     * @param p point to be checked
     * @return true iff the set contains point
     */
	public boolean contains(Point2D p) {
		return points.contains(p);
	} 
	
    /**
     * Draw all points to standard draw.
     */
	public void draw() {
		for (Point2D p: points) StdDraw.point(p.x(), p.y());
	} 
	
    /**
     * All points that are inside the rectangle
     * @param rect rectangle
     * @return points
     */
	public Iterable<Point2D> range(RectHV rect) {
		Stack<Point2D> ps = new Stack<Point2D>();
		for (Point2D p: points) {
			if (!rect.contains(p)) continue;
			ps.push(p);
		}
		return ps;
	} 
	
    /**
     * A nearest neighbor in the set to point p; null if the set is empty.
     * @param p point
     * @return nearest neighbor
     */
	public Point2D nearest(Point2D q) {	
		Point2D nearest = null;
		double dSqrMin = Double.POSITIVE_INFINITY;
		for (Point2D p: points) {
			double dSqr = p.distanceSquaredTo(q);
			if (dSqr < dSqrMin) {
				dSqrMin = dSqr;
				nearest = p;
			}
		}
		return nearest;
	} 
}
