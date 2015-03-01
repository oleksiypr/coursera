import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

public class PointSETTest {
	@Test
	public void isEmptyTest() {
		PointSET ps = new PointSET();
		Assert.assertTrue(ps.isEmpty());
		
		ps.insert(new Point2D(0.0, 0.0));
		Assert.assertFalse(ps.isEmpty());
	}
	
	@Test
	public void testSize() {
		PointSET ps = new PointSET();
		Assert.assertEquals(0, ps.size());
		
		ps.insert(new Point2D(0.0, 0.0));
		Assert.assertEquals(1, ps.size());
		
		ps.insert(new Point2D(0.0, 0.0));
		Assert.assertEquals(1, ps.size());
		
		ps.insert(new Point2D(1.0, 2.0));
		Assert.assertEquals(2, ps.size());
	}
	
	@Test
	public void testContains() {
		PointSET ps = new PointSET();
		Point2D p0 = new Point2D(0.0, 0.0);
		Assert.assertFalse(ps.contains(p0));
		
		ps.insert(new Point2D(0.1, 2.5));
		Assert.assertFalse(ps.contains(p0));
		Assert.assertTrue(ps.contains(new Point2D(0.1, 2.5)));
	}
	
	@Test
	public void rangeEmptySetTest() {
		PointSET ps = new PointSET();
		RectHV rect = new RectHV(0.0, 0.0, 0.2, 0.2);		
		Assert.assertFalse(ps.range(rect).iterator().hasNext());
	}
	
	@Test
	public void rangeDegenerateRectTest() {
		PointSET ps = new PointSET();
		ps.insert(new Point2D(0.0, 0.0));
		
		RectHV rect = new RectHV(0.5, 0.5, 0.5, 0.5);			
		Assert.assertFalse(ps.range(rect).iterator().hasNext());
		
		ps.insert(new Point2D(0.5, 0.5));
		Assert.assertTrue(ps.range(rect).iterator().hasNext());
	}
	
	@Test
	public void rangeRectTest() {
		PointSET ps = new PointSET();
		ps.insert(new Point2D(0.0, 0.0));
		ps.insert(new Point2D(0.1, 0.2));
		ps.insert(new Point2D(0.5, 0.6));
		
		RectHV rect = new RectHV(0.0, 0.0, 0.5, 0.5);
		Iterator<Point2D> rangeInerator = ps.range(rect).iterator();		
		Assert.assertTrue(rangeInerator.hasNext());
		
		rangeInerator.next();
		Assert.assertTrue(rangeInerator.hasNext());
		
		rangeInerator.next();
		Assert.assertFalse(rangeInerator.hasNext());
	}
	
	@Test
	public void nearestNullTest() {
		PointSET ps = new PointSET();
		Assert.assertNull(ps.nearest(new Point2D(0.0, 0.0)));
	} 
	
	@Test
	public void nearestSamePointTest() {
		PointSET ps = new PointSET();
		ps.insert(new Point2D(0.0, 0.0));
		ps.insert(new Point2D(0.1, 0.2));
		Assert.assertNotNull(ps.nearest(new Point2D(0.0, 0.0)));
		Assert.assertEquals(new Point2D(0.0, 0.0), ps.nearest(new Point2D(0.0, 0.0)));
	} 
	
	@Test
	public void nearestTest() {
		PointSET ps = new PointSET();
		ps.insert(new Point2D(0.0, 0.0));
		ps.insert(new Point2D(0.1, 0.2));
		ps.insert(new Point2D(0.5, 0.6));
		ps.insert(new Point2D(0.3, 0.1));
		ps.insert(new Point2D(0.3, 0.5));
		
		Point2D q = new Point2D(0.6, 0.4);
		Assert.assertNotNull(ps.nearest(q));
		Assert.assertEquals(new Point2D(0.5, 0.6), ps.nearest(q));
	}
}
