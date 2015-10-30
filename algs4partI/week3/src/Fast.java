import java.util.Arrays;
import edu.princeton.cs.algs4.*;


/** A faster, sorting-based solution. Remarkably, it is possible to solve the problem much faster 
 * than the brute-force solution described above. Given a point p, the following method 
 * determines whether p participates in a set of 4 or more collinear points.
 *  - Think of p as the origin.
 *  - For each other point q, determine the slope it makes with p.
 *  - Sort the points according to the slopes they makes with p.
 *  - Check if any 3 (or more) adjacent points in the sorted order have equal slopes with respect to p. If so, 
 *     these points, together with p, are collinear.    
 * 
 * @author Oleksiy_Prosyanko
 */
public class Fast {
    private final static int MINIMUM_APPENED_POINTS = 2;
    
    public static void main(String[] args) {
        prepare();
        Point[] points = readInput(args[0]);
        plotLinePatterns(points);
    }

    private static void plotLinePatterns(Point[] points) {
        if (points.length < 4) return;
        StdDraw.setPenRadius();
        
        Arrays.sort(points);  
        for (int k = 0; k < points.length; k++) { 
            Point origin = points[k];             
            Point[] rest = Arrays.copyOfRange(points, k, points.length);
            Arrays.sort(rest, origin.slopeOrder());
            
            if (rest.length < MINIMUM_APPENED_POINTS + 2) continue;
            double slope = origin.slopeTo(rest[1]); 
            for (int i = 2, count = 0; i < rest.length; i++) {  
                //check for line segment end
                if (origin.slopeTo(rest[i]) == slope) { 
                    count++; 
                    
                    // check edge condition for line segment
                    if (i == rest.length - 1 && count >= MINIMUM_APPENED_POINTS && 
                        !sloped(points, k, slope)) {                          
                        out(count, i, rest); 
                    }
                } else {    
                    //check usual condition for line segment
                    if (count >= MINIMUM_APPENED_POINTS && !sloped(points, k, slope)) {                       
                        out(count, i - 1, rest);
                    }
                    
                    slope = origin.slopeTo(rest[i]);
                    count = 0;                    
                }
            }            
        }
    }

    private static void prepare() {
        StdDraw.setXscale(0, 32768);    
        StdDraw.setYscale(0, 32768);
        StdDraw.setPenRadius(0.01);    
        StdDraw.show(0);               
    }
    
    private static Point[] readInput(String filename) {
        In in = new In(filename);
        int N = in.readInt();
        Point[] points = new Point[N];
        for (int i = 0; i < N; i++) {
            int x = in.readInt();
            int y = in.readInt();
            Point p = new Point(x, y);
            //p.draw();
            points[i] = p;
        }
        return points;
    }
    
    private static boolean sloped(Point[] origins, int k, double slope) {
        Point candicate = origins[k];
        for (int i = 0; i < k; i++) {
            if (origins[i].slopeTo(candicate) == slope) return true;
        }
        return false;
    }

    private static void out(int count, int end, Point[] ps) {
        ps[0].drawTo(ps[end]);
        StdDraw.show(100);

        Point origin = ps[0];
        int size = count + MINIMUM_APPENED_POINTS;
        int begin = end - size + 1;
        StdOut.print(origin + " -> ");
        for (int i = begin + 1; i < end; i++) {
            StdOut.print(ps[i] + " -> ");
        }
        StdOut.print(ps[end]);
        StdOut.println();
    }
}