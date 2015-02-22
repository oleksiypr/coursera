/*************************************************************************
 * Name:
 * Email:
 *
 * Compilation:  javac Point.java
 * Execution:
 * Dependencies: StdDraw.java
 *
 * Description: An immutable data type for points in the plane.
 * @author Oleksiy_Prosyanko
 *************************************************************************/

import java.util.Arrays;
import java.util.Comparator;

public class Point implements Comparable<Point> {
    public final Comparator<Point> SLOPE_ORDER = new SloperOrder();       

    private final int x;    
    private final int y;    

    /**
     * Create the point (x, y)
     * @param x 
     * @param y 
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     *  Plot this point to standard drawing
     */
    public void draw() {
        StdDraw.point(x, y);
    }

    /**
     * Draw line between this point and that point to standard drawing
     * @param that point
     */
    public void drawTo(Point that) {
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    /**
     * Slope between this point and that point
     * @param that point
     * @return slope to that point
     */
    public double slopeTo(Point that) {
        int dx = that.x - this.x;
        int dy = that.y - this.y;
        
        if (dx == 0 && dy == 0) return Double.NEGATIVE_INFINITY;
        if (dy == 0) return +0.0;
        if (dx == 0) return Double.POSITIVE_INFINITY;
        return ((double) dy)/ dx;
    }

    /**
     * Is this point lexicographically smaller than that one?
     * comparing y-coordinates and breaking ties by x-coordinates
     * @return { -1, 0, +1 }, 0 if pints are equals, 1 if this grater then that, -1 if this less then that 
     */
    public int compareTo(Point that) {
        if (this.y == that.y && this.x == that.x) return 0;
        if (this.y < that.y) return -1;
        if (this.y == that.y) {
            if (this.x > that.x) return +1;
            else return -1;
        }
        return +1;
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    
    private class SloperOrder implements Comparator<Point> {
        @Override
        public int compare(Point x1, Point x2) {
            Double slope1 = slopeTo(x1);
            Double slope2 = slopeTo(x2);
            return slope1.compareTo(slope2);
        }
    }

    // unit test
    public static void main(String[] args) {
        cornerLeft(); 
        corenerRight();
        cross();
        crossOrigin();  
        starOrigin();
    }
    
    /**
     *     4
     *     |
     *     3 
     *     |
     * 0-1-2
     */
    private static void cornerLeft() {
        StdOut.println("Corner left");
        Point[] ps = new Point[5];
        ps[0] = new Point(0, 0);
        ps[1] = new Point(1, 0);
        ps[2] = new Point(2, 0);
        ps[3] = new Point(2, 1);
        ps[4] = new Point(2, 2);
        StdRandom.shuffle(ps);        
        Arrays.sort(ps);
        out(ps);
    }
    
    /**
     * 2-3-4
     * |    
     * 1     
     * |    
     * 0
     */
    private static void corenerRight() {
        StdOut.println("Corner right");
        Point[] ps = new Point[5];
        ps[0] = new Point(0, 0);
        ps[1] = new Point(0, 1);
        ps[2] = new Point(0, 2);
        ps[3] = new Point(1, 2);
        ps[4] = new Point(2, 2);
        StdRandom.shuffle(ps);        
        Arrays.sort(ps);
        out(ps);
        
    }
    
    /**
     *   4
     *   |  
     * 1-2-4     
     *   |    
     *   0
     */
    private static void cross() {
        StdOut.println("Cross");
        Point[] ps = new Point[5];
        ps[0] = new Point(1, 0);
        ps[1] = new Point(0, 1);
        ps[2] = new Point(1, 1);
        ps[3] = new Point(2, 1);
        ps[4] = new Point(1, 2);
        StdRandom.shuffle(ps);        
        Arrays.sort(ps);
        out(ps);        
    }
    
    /**
     *      3(4)
     *      |  
     * 1(2)-0-2(1)     
     *      |    
     *      4(3)
     */
    private static void crossOrigin() {
        StdOut.println("Cross orgin");
        Point[] ps = new Point[5];
        Point origin = new Point(1, 1);
        ps[0] = origin;
        ps[1] = new Point(0, 1);
        ps[2] = new Point(2, 1);
        ps[3] = new Point(1, 2);
        ps[4] = new Point(1, 0);
        StdRandom.shuffle(ps);         
        Arrays.sort(ps, origin.SLOPE_ORDER);
        out(ps);   
        
    }
    
    /**
     *    2 8 6
     *     \|/  
     *   3--0--4     
     *     /|\    
     *    5 7 1
     */
    private static void starOrigin() {
        StdOut.println("Star orgin");
        Point[] ps = new Point[9];
        Point origin = new Point(1, 1);
        ps[0] = origin;
        ps[1] = new Point(0, 2);
        ps[2] = new Point(2, 0);
        ps[3] = new Point(0, 1);
        ps[4] = new Point(2, 1);
        ps[5] = new Point(0, 0);
        ps[6] = new Point(2, 2);
        ps[7] = new Point(1, 0);
        ps[8] = new Point(1, 2);
        
        StdRandom.shuffle(ps);  
        Arrays.sort(ps);
        Arrays.sort(ps, origin.SLOPE_ORDER);
        out(ps);  
        
    }

    
    private static void out(Point[] ps) {
        for (int i = 0; i < ps.length; i++) {
            StdOut.println(i + ": " + ps[i]);
        }
        StdOut.println();
    }
}