/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

public class SeamCarver {

    private Picture picture;

    private static class TolologicalSP {

        private final double[][] energy;

        private TolologicalSP(double[][] energy) {
            this.energy = energy;
        }

        public int[] path() {
            return null;
        }


        private int from(int v) {
            int imin = -1;
            double min = Double.POSITIVE_INFINITY;
            int[] ins = ins(v);
            for (int i = 0; i < ins.length; i++) {
                if (ins[i] < min) {
                    imin = i;
                    min = ins[i];
                }
            }
            return imin;
        }

        private int[] ins(int v) {
            return null;
        }

        /**
         * @param v vertex 1 .. W*H
         * @return i 0 .. H - 1
         */
        private int i(int v) {
            return -1;
        }

        /**
         * @param v vertex 1 .. W*H
         * @return j 0 .. W - 1
         */
        private int j(int v) {
            return -1;
        }

        /**
         * @param i 0..H - 1
         * @param j 0..W - 1
         * @return vertex
         */
        private int v(int i, int j) {
            return -1;
        }
    }

    /**
     * Create a seam carver object based on the given picture.
     * @param picture an input
     */
    public SeamCarver(Picture picture) {
        this.picture = new Picture(picture);
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
        if (x == 0 || x == width() - 1) return 1000.0;
        if (y == 0 || y == height() - 1) return 1000.0;
        return Math.sqrt(d2x(x, y) + d2y(x, y));
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
        double[][] energy = new double[height()][width()];
        for (int y = 0; y < height(); y++)
            for (int x = 0; x < width(); x++)
                energy[y][x] = energy(x, y);

        TolologicalSP sp = new TolologicalSP(energy);
        return sp.path();
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

    /**
     * Square of X gradient.
     * @param x horizintal coordinate 0 .. width - 1
     * @param y vertical coordinate 0 .. height - 1
     * @return square of X gradient
     */
    private double d2x(int x, int y) {
        double rx = picture.get(x + 1, y).getRed()   - picture.get(x - 1, y).getRed();
        double gx = picture.get(x + 1, y).getGreen() - picture.get(x - 1, y).getGreen();
        double bx = picture.get(x + 1, y).getBlue()  - picture.get(x - 1, y).getBlue();
        return rx*rx + gx*gx + bx*bx;
    }

    /**
     * Square of Y gradient.
     * @param x horizintal coordinate 0 .. width - 1
     * @param y vertical coordinate 0 .. height - 1
     * @return square of Y gradient
     */
    private double d2y(int x, int y) {
        double ry = picture.get(x, y + 1).getRed()   - picture.get(x, y - 1).getRed();
        double gy = picture.get(x, y + 1).getGreen() - picture.get(x, y - 1).getGreen();
        double by = picture.get(x, y + 1).getBlue()  - picture.get(x, y - 1).getBlue();
        return ry*ry + gy*gy + by*by;
    }
}
