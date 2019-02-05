/* *****************************************************************************
 *  Name: Oleksii Prosianko
 *  Date: 2019/02/04
 *  Description:  Seam Carving
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

/**
 * Seam-carving is a content-aware image resizing technique where the image is reduced in size by
 * one pixel of height (or width) at a time. A vertical seam in an image is a path of pixels
 * connected from the top to the bottom with one pixel in each row. (A horizontal seam is a path of
 * pixels connected from the left to the right with one pixel in each column.) Below left is the
 * original 505-by-287 pixel image; below right is the result after removing 150 vertical seams,
 * resulting in a 30% narrower image. Unlike standard content-agnostic resizing techniques
 * (e.g. cropping and scaling), the most interesting features (aspect ratio, set of objects present,
 * etc.) of the image are preserved.
 */
public class SeamCarver {

    private static final double BORDER_ENERGY = 1000.0;

    private Picture picture;
    private boolean isTransposed;

    private static class TolologicalSP {

        private final double[][] energy;
        private final int[] edgeTo;
        private final int V;
        private final int H;
        private final int W;
        private double outEenergy;

        private TolologicalSP(double[][] energy, int H, int W) {
            this.outEenergy = 0.0;
            this.energy = energy;
            this.H = H;
            this.W = W;
            this.V = H*W + 2;
            this.edgeTo = new int[V];
            for (int v = 0; v < V; v++) relax(v);
        }

        public int[] path() {
            int[] p = new int[H];
            int w = V - 1;
            while (edgeTo[w] != 0) {
                int v = edgeTo[w];
                int i = i(v);
                int j = j(v);
                p[i] = j;
                w = v;
            }
            return p;
        }

        /**
         * @param v vertex 0 .. V - 1
         */
        private void relax(int v) {
            if (v == 0) return;

            int from = to(v);
            edgeTo[v] = from;
            if (v == V - 1) {
                outEenergy = energy[i(from)][j(from)];
                return;
            }

            int i = i(v);
            int j = j(v);
            double e = energy(from);
            energy[i][j] += e;

        }

        /**
         * @param v vertex 1 .. V - 1
         * @return number of the vertex with lowest energy we came from
         */
        private int to(int v) {
            int kmin = -1;
            double min = Double.POSITIVE_INFINITY;
            int[] ins = ins(v);
            if (ins == null) throw new IllegalArgumentException("ins is null");
            for (int k = 0; k < ins.length; k++) {
                double e = energy(ins[k]);
                if (e < min) {
                    kmin = k;
                    min = e;
                }
            }
            return ins[kmin];
        }

        /**
         * @param v vertex 0 .. V - 1
         * @return energy
         */
        private double energy(int v) {
            if (v == 0) return 0.0;
            if (v == V - 1) return outEenergy;
            return energy[i(v)][j(v)];
        }

        /**
         * Here by `in` we mean a vertex we came from.
         * @param v retex 1 .. V - 1
         * @return array of all `in` verexies
         */
        private int[] ins(int v) {
            if (v > 0 && v <= W) return new int[]{0};
            if (v == V - 1) {
                int[] res = new int[W];
                int bottomBegin = V - 1 - W;
                int bottomEnd = V - 1;
                for (int k = bottomBegin; k < bottomEnd; k++) res[j(k)] = k;
                return res;
            }

            int i = i(v);
            int j = j(v);

            if (j == 0)
                if (W == 1) return new int[] {  v(i - 1, j) };
                else return new int[] {
                    v(i - 1, j),
                    v(i - 1, j + 1) };

            if (j == W - 1)
                return new int[] {
                    v(i - 1, j),
                    v(i - 1, j - 1) };

            return new int[] {
                v(i - 1, j - 1),
                v(i - 1, j),
                v(i - 1, j + 1)
            };
        }

        /**
         * @param v vertex 1 .. V - 2
         * @return i 0 .. H - 1
         */
        private int i(int v) {
            return (v - 1) / W;
        }

        /**
         * @param v vertex 1 .. V - 2
         * @return j 0 .. W - 1
         */
        private int j(int v) {
            return (v - 1) % W;
        }

        /**
         * @param i 0..H - 1
         * @param j 0..W - 1
         * @return vertex 1 .. V - 2
         */
        private int v(int i, int j) {
            return i*W + j + 1;
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
    public double energy(int x, int y) {
        validateX(x);
        validateY(y);
        if (x == 0 || x == width() - 1) return BORDER_ENERGY;
        if (y == 0 || y == height() - 1) return BORDER_ENERGY;
        return Math.sqrt(d2x(x, y) + d2y(x, y));
    }

    /**
     * @return sequence of indices for horizontal seam
     */
    public int[] findHorizontalSeam() {
        transpose();
        int[] horiz = findVerticalSeam();
        transpose();
        return horiz;
    }

    /**
     * @return sequence of indices for vertical seam
     */
    public int[] findVerticalSeam() {
        int H = height();
        int W = width();
        double[][] energy = new double[H][W];
        for (int y = 0; y < H; y++)
            for (int x = 0; x < W; x++)
                energy[y][x] = energy(x, y);

        TolologicalSP sp = new TolologicalSP(energy, H, W);
        return sp.path();
    }

    /**
     * Remove horizontal seam to current picture.
     * @param seam to be removed
     */
    public void removeHorizontalSeam(int[] seam) {
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }

    /**
     * Remove vertical seam to current picture.
     * @param seam to be removed
     */
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException("Seam cannot ne null");
        if (seam.length != height()) throw new IllegalArgumentException("Seam length should be equsl to " + height());

        Picture p = new Picture(width() - 1, height());
        for (int y = 0; y < height(); y++) {
            if (y > 0 && Math.abs(seam[y] - seam[y - 1]) > 1) {
                throw new IllegalArgumentException("Distance beetwen 2 adjacent seam points more then 1");
            }
            for (int x = 0, newX = 0; x < width(); x++) {
                validateX(seam[y]);
                if (seam[y] != x) {
                    p.setRGB(newX, y, picture.getRGB(x, y));
                    newX++;
                }
            }
        }
        this.picture = p;
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

    private void transpose() {
        int H = height();
        int W = width();
        Picture current = picture();
        Picture transposed = new Picture(H, W);

        for (int y = 0; y < H; y++)
            for (int x = 0; x < W; x++) {
                int rgb = current.getRGB(x, y);
                transposed.setRGB(y, x, rgb);
            }
        this.picture = transposed;
        this.isTransposed = !this.isTransposed;
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

    private void validateX(int x) {
        if (x < 0 || x >= width())
            throw new IllegalArgumentException("Coordinate x must be between 0 and " + (width() - 1) + ": " + x);
    }

    private void validateY(int y) {
        if (y < 0 || y >= height())
            throw new IllegalArgumentException("Coordinate y must be between 0 and " + (height() - 1) + ": " + y);
    }
}
