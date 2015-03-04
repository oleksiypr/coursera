import org.junit.Assert;
import org.junit.Test;

public class KdTreeTest {
    @Test
    public void newTreeIsEmptyTest() {
        KdTree tree = new KdTree();        
        Assert.assertTrue(tree.isEmpty());
    }
    
    @Test
    public void isEmptyTest() {
        KdTree tree = new KdTree();
        tree.insert(new Point2D(0.0, 0.0));
        Assert.assertFalse(tree.isEmpty());
    }
    
    @Test
    public void sizeTest() {
        KdTree tree = new KdTree();
        tree.insert(new Point2D(0.5, 0.6));
        tree.insert(new Point2D(0.7, 0.3));
        tree.insert(new Point2D(0.2, 0.4));
        tree.insert(new Point2D(0.1, 0.2));
        tree.insert(new Point2D(0.3, 0.7));
        Assert.assertEquals(5, tree.size());
    }
    
    @Test
    public void insertSinglePointTest() {
        KdTree tree = new KdTree();
        Point2D p0 = new Point2D(0.0, 0.0);
        tree.insert(p0);
        
        Assert.assertNotNull(tree.root());
        Assert.assertEquals(p0, tree.root().point);
        Assert.assertEquals(1, tree.size());
    }
    
    @Test
    public void insertTest() {
        KdTree tree = new KdTree();
        Point2D p1 = new Point2D(0.5, 0.6);
        Point2D p2 = new Point2D(0.7, 0.3);
        Point2D p3 = new Point2D(0.2, 0.4);
        Point2D p4 = new Point2D(0.1, 0.2);
        Point2D p5 = new Point2D(0.3, 0.7);
        
        tree.insert(p1);
        tree.insert(p2);
        tree.insert(p3);
        tree.insert(p4);
        tree.insert(p5);
        
        Assert.assertEquals(5, tree.size());
        Assert.assertEquals(p1, tree.root().point);
        Assert.assertEquals(p2, tree.root().right.point);
        Assert.assertEquals(p3, tree.root().left.point);
        Assert.assertEquals(p4, tree.root().left.left.point);
        Assert.assertEquals(p5, tree.root().left.right.point);
        
        Assert.assertTrue(tree.root() instanceof KdTree.Xnode);
        Assert.assertTrue(tree.root().right instanceof KdTree.Ynode);
        Assert.assertTrue(tree.root().left instanceof KdTree.Ynode);
        Assert.assertTrue(tree.root().left.left instanceof KdTree.Xnode);
        Assert.assertTrue(tree.root().left.right instanceof KdTree.Xnode);
    }
    
    @Test
    public void emptyTreeContainsNothing() {
        KdTree tree = new KdTree();
        Assert.assertFalse(tree.contains(new Point2D(0.1, 0.5)));
    }
    
    @Test
    public void containsTest() {
        KdTree tree = new KdTree();
        Point2D p1 = new Point2D(0.5, 0.6);
        Point2D p2 = new Point2D(0.7, 0.3);
        Point2D p3 = new Point2D(0.2, 0.4);
        Point2D p4 = new Point2D(0.1, 0.2);
        Point2D p5 = new Point2D(0.3, 0.7);
        
        tree.insert(p1);
        tree.insert(p2);
        tree.insert(p3);
        tree.insert(p4);
                        
        Assert.assertTrue(tree.contains(p1));
        Assert.assertTrue(tree.contains(p2));
        Assert.assertTrue(tree.contains(p3));
        Assert.assertTrue(tree.contains(p4));
        Assert.assertTrue(tree.contains(p5));
        
        Assert.assertFalse(tree.contains(new Point2D(0.0, 0.0)));
        Assert.assertFalse(tree.contains(new Point2D(0.6, 0.7)));
        Assert.assertFalse(tree.contains(new Point2D(1.5, 1.6)));
        Assert.assertFalse(tree.contains(new Point2D(0.4, 0.6)));
        Assert.assertFalse(tree.contains(new Point2D(-0.5, 0.6)));
    }
    
    @Test
    public void containsAfterInsertTest() {
        KdTree tree = new KdTree();
        Point2D p1 = new Point2D(0.5, 0.6);
        Point2D p2 = new Point2D(0.7, 0.3);
        Point2D p3 = new Point2D(0.2, 0.4);
        Point2D p4 = new Point2D(0.1, 0.2);
        Point2D p5 = new Point2D(0.3, 0.7);
        
        tree.insert(p1);
        tree.insert(p2);
        tree.insert(p3);
        tree.insert(p4);
        
        Assert.assertFalse(tree.contains(p5));
        tree.insert(p5);
        Assert.assertTrue(tree.contains(p5));   
    }
    
    @Test
    public void emptyTreeRangeNothing() {
        KdTree tree = new KdTree();
        RectHV query = new RectHV(0.0, 0.0, 1.0, 1.0);
        Assert.assertFalse(tree.range(query).iterator().hasNext());
    }
    
    @Test
    public void rooRectangleContainsEverything() {
        KdTree tree = new KdTree();
        Point2D p1 = new Point2D(0.5, 0.6);
        Point2D p2 = new Point2D(0.7, 0.3);
        Point2D p3 = new Point2D(0.2, 0.4);
        Point2D p4 = new Point2D(0.1, 0.2);
        Point2D p5 = new Point2D(0.3, 0.7);
        
        tree.insert(p1);
        tree.insert(p2);
        tree.insert(p3);
        tree.insert(p4);
        tree.insert(p5);
        
        RectHV query = tree.root().rectangle;
        Stack<Point2D> result = (Stack<Point2D>) tree.range(query);
        Assert.assertEquals(tree.size(), result.size());
    }
    
    @Test
    public void rangeTest() {
        KdTree tree = new KdTree();
        Point2D p1 = new Point2D(0.5, 0.6);
        Point2D p2 = new Point2D(0.7, 0.3);
        Point2D p3 = new Point2D(0.2, 0.4);
        Point2D p4 = new Point2D(0.1, 0.2);
        Point2D p5 = new Point2D(0.3, 0.7);
        
        tree.insert(p1);
        tree.insert(p2);
        tree.insert(p3);
        tree.insert(p4);
        tree.insert(p5);
        
        RectHV query = new RectHV(0.4, 0.3, 0.8, 0.7);
        Stack<Point2D> result = (Stack<Point2D>) tree.range(query);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(p2, result.pop());
        Assert.assertEquals(p1, result.pop());
        
        RectHV emptyResultquery = new RectHV(0.6, 0.4, 0.9, 0.8);
        Stack<Point2D> emptyResult = (Stack<Point2D>) tree.range(emptyResultquery);
        Assert.assertTrue(emptyResult.isEmpty());
    }
}
