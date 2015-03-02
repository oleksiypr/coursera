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
        
        Assert.assertEquals(p1, tree.root().point);
        Assert.assertEquals(p2, tree.root().right.point);
        Assert.assertEquals(p3, tree.root().left.point);
        Assert.assertEquals(p4, tree.root().left.left.point);
        Assert.assertEquals(p5, tree.root().left.right.point);
        Assert.assertEquals(5, tree.size());
    }
}
