/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private Picture picture;

    /**
     * Create a seam carver object based on the given picture.
     * @param picture an input
     */
    public SeamCarver(Picture picture) {
        this.picture = picture;
    }

    /**
     * @return current picture
     */
    public Picture picture() {
        return this.picture;
    }

    /**
     * @return  width of current picture
     */
    public int width() {
        return picture.width();
    }

    /**
     * @return height of current picture
     */
    public int height() {
        return picture.height();
    }

    /**
     * @param x horizintal coordinate 0 .. width - 1
     * @param y vertical coordinate 0 .. height - 1
     * @return energy of pixel at column x and row y
     */
    public  double energy(int x, int y) {
        return Double.NaN;
    }

    /**
     * @return sequence of indices for horizontal seam
     */
    public int[] findHorizontalSeam() {
        return null;
    }

    /**
     * @return sequence of indices for vertical seam
     */
    public int[] findVerticalSeam() {
        return null;
    }

    /**
     * Remove horizontal seam from current picture.
     * @param seam to be removed
     */
    public void removeHorizontalSeam(int[] seam) {

    }

    /**
     * Remove vertical seam from current picture.
     * @param seam to be removed
     */
    public void removeVerticalSeam(int[] seam) {

    }
}
