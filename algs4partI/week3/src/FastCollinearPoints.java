import java.util.Arrays;

import edu.princeton.cs.algs4.ResizingArrayQueue;

/** 
 * Fast solution to find all line segments containing 4 points.
 * @author Oleksiy_Prosyanko
 */
public class FastCollinearPoints {
    private final static int MINIMUM_APPENED_POINTS = 2;
	
	private Point[] points;
	private LineSegment[] segments;
	
	/**	
	 * Construct fast find collinear points solution object.
	 * @param input points to be examined 
	 */
	public FastCollinearPoints(Point[] input)  {
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
        if (points.length < 4) {
        	segments = new LineSegment[0];
        	return;
        }
       
        ResizingArrayQueue<LineSegment> segs = new ResizingArrayQueue<>();
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
                    if (i == rest.length - 1 && 
                    	count >= MINIMUM_APPENED_POINTS && 
                        !sloped(points, k, slope)) 
                    {                          
                        segs.enqueue(new LineSegment(rest[0], rest[i]));    
                    }
                } else {    
                    //check usual condition for line segment
                    if (count >= MINIMUM_APPENED_POINTS && !sloped(points, k, slope)) {                       
                    	segs.enqueue(new LineSegment(rest[0], rest[i - 1])); 
                    }
                    
                    slope = origin.slopeTo(rest[i]);
                    count = 0;                    
                }
            }            
        }
        
        int segCount = segs.size();
        segments = new LineSegment[segCount]; 
        for (int i = 0; i < segCount; i++) {
        	segments[i] = segs.dequeue();   
        }
	}
	
    private boolean sloped(Point[] origins, int k, double slope) {
        Point candicate = origins[k];
        for (int i = 0; i < k; i++) {
            if (origins[i].slopeTo(candicate) == slope) return true;
        }
        return false;
    }   
}
