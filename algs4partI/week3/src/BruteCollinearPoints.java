import java.util.Arrays;
import edu.princeton.cs.algs4.*;

/** 
 * Brute force solution to find all line segments containing 4 points.
 * @author Oleksiy_Prosyanko
 */
public class BruteCollinearPoints {
	private Point[] points;
	private LineSegment[] segments;
	
	/**	
	 * Construct brute force find collinear points solution object.
	 * @param input points to be examined 
	 */
	public BruteCollinearPoints(Point[] input)  {
		for (Point p: input) if (p == null) { 
			throw new NullPointerException("Neither the argument to the constructor or any point in the array is null");
		}
		
		points = Arrays.copyOf(input, input.length);
		Arrays.sort(points);
		for (int i = 0; i < points.length - 1; i++) {
			int cmp = points[i].compareTo(points[i + 1]);
			if (cmp == 0) throw new IllegalArgumentException("An input should not contain a repeated points.");
		}
	}  
	
	/**
	 * The number of line segments.
	 * @return number of line segments
	 */
	public int numberOfSegments() {
		if (segments == null) findCollinearPoints();
		return segments.length;		
	}   
	
	/**
	 * The line segments.
	 * @return line segments
	 */
	public LineSegment[] segments() {
		if (segments == null) findCollinearPoints();
		return Arrays.copyOf(segments, segments.length);
	}
		
	private void findCollinearPoints() {
		Queue<LineSegment> segs = new Queue<>();
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
                        out(colliniars, segs);
                    }
                }
            }
        }
        
        int segCount = segs.size();
        segments = new LineSegment[segCount]; 
        for (int i = 0; i < segCount; i++) {
        	segments[i] = segs.dequeue();   
        }
	}

	private void out(Point[] colliniars, Queue<LineSegment> segs) {		
        int begin = 0;
        int end = colliniars.length - 1;
        Arrays.sort(colliniars);
        segs.enqueue(new LineSegment(colliniars[begin], colliniars[end]));
	}
}
