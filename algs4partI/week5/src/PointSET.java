/**
 * The data type that represents a set of points in the unit square. 
 * Implemented by using a red-black BST.
 * 
 * @author Oleksiy Prosyanko
 */
public class PointSET {
	/**
	 *  Construct an empty set of points.
	 */
	public PointSET() {
		//TODO
	}     
	
	/**
	 * Is the set empty? 
	 * @return true iff set is empty
	 */
	public boolean isEmpty() {
		//TODO
		return true;
	}   
	
    /**
     * Number of points in the set.
     * @return number of points in the set
     */
	public int size() {
		//TODO
		return -1;
	} 
	
    /**
     * Add the point to the set (if it is not already in the set).
     * @param p point to add
     */
	public void insert(Point2D p) {
		//TODO
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

    // unit testing of the methods (optional)
	public static void main(String[] args) {} 
}
