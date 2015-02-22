import java.util.Arrays;

/**
 * A program examines 4 points at a time and checks whether they all lie 
 * on the same line segment, printing out any such line segments to 
 * standard output and drawing them using standard drawing. 
 * o check whether the 4 points p, q, r, and s are collinear, check whether 
 * the slopes between p and q, between p and r, and between p and s are all equal.   
 * 
 * @author Oleksiy_Prosyanko
 */
public class Brute {
    public static void main(String[] args) {
        prepare();
        Point[] points = readInput(args[0]);
        plotLinePatterns(points);
    }

    private static void prepare() {
        StdDraw.setXscale(0, 32768);    // rescale coordinates and turn on animation mode
        StdDraw.setYscale(0, 32768);
        StdDraw.setPenRadius(0.01);     // make the points a bit larger 
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
            p.draw();
            points[i] = p;
        }
        return points;
    }
    
    private static void plotLinePatterns(Point[] points) {
        StdDraw.setPenRadius();
        for (int i = 0; i < points.length - 3; i++) 
        for (int j = i + 1; j < points.length - 2; j++) {
            Point p = points[i];
            Point q = points[j];
            double slopeQ = p.slopeTo(q);            
            for (int k = j + 1; k < points.length - 1; k++) {
                Point r = points[k];
                double slopeR = p.slopeTo(r);
                if (slopeQ != slopeR) continue;
                for (int m = k + 1; m < points.length; m++) {
                    Point s = points[m];  
                    double slopeS = p.slopeTo(s);
                    if (slopeQ == slopeR && slopeR == slopeS) {
                        Point[] colliniars = new Point[4];
                        colliniars[0] = p;
                        colliniars[1] = q;
                        colliniars[2] = r;
                        colliniars[3] = s;
                        
                        Arrays.sort(colliniars);                        
                        out(colliniars);
                    }
                }
            }
        }
    }

    private static void out(Point[] colliniars) {
        int begin = 0;
        int end = colliniars.length - 1;
        colliniars[begin].drawTo(colliniars[end]);
        StdDraw.show(1);

        StdOut.print(colliniars[begin] + " -> ");
        for (int i = begin + 1; i < end; i++) {
            StdOut.print(colliniars[i] + " -> ");
        }
        StdOut.print(colliniars[end]);
        StdOut.println();        
    }
}
