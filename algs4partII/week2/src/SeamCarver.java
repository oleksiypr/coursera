/* *****************************************************************************
 *  Name: Oleksii Prosianko
 *  Date: 2019/02/06
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
         * @param i 0 .. H - 1
         * @param j 0 .. W - 1
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
        return new Picture(this.picture);
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
        return energy(this.picture, x, y);
    }

    /**
     * @return sequence of indices for horizontal seam
     */
    public int[] findHorizontalSeam() {
        return findVerticalSeam(transpose(this.picture));
    }

    /**
     * @return sequence of indices for vertical seam
     */
    public int[] findVerticalSeam() {
        return findVerticalSeam(this.picture);
    }

    /**
     * Remove horizontal seam to current picture.
     * @param seam to be removed
     */
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) throw new IllegalArgumentException("Seam cannot ne null");
        Picture transposed = transpose(this.picture);
        this.picture = transpose(removeVerticalSeam(transposed, seam));
    }

    /**
     * Remove vertical seam to current picture.
     * @param seam to be removed
     */
    public void removeVerticalSeam(int[] seam) {
        this.picture = removeVerticalSeam(this.picture, seam);
    }

    /**
     * @param p target picture
     * @return sequence of indices for vertical seam
     */
    private int[] findVerticalSeam(Picture p) {
        int H = p.height();
        int W = p.width();
        double[][] energy = new double[H][W];
        for (int y = 0; y < H; y++)
            for (int x = 0; x < W; x++)
                energy[y][x] = energy(p, x, y);

        TolologicalSP sp = new TolologicalSP(energy, H, W);
        return sp.path();
    }

    /**
     * Remove vertical seam to current picture.
     * @param p original picture
     * @param seam to be removed
     * @return new picture with seam removed
     */
    private Picture removeVerticalSeam(Picture p, int[] seam) {
        if (seam == null) throw new IllegalArgumentException("Seam cannot ne null");

        int height = p.height();
        int width  = p.width();

        if (seam.length != height)
            throw new IllegalArgumentException(
                "Seam length should be equsl to " + height());

        Picture newPicture = new Picture(width - 1, height);
        for (int y = 0; y < height; y++) {
            if (y > 0 && Math.abs(seam[y] - seam[y - 1]) > 1) {
                throw new IllegalArgumentException(
                    "Distance beetwen 2 adjacent seam points more then 1");
            }
            for (int x = 0, newX = 0; x < width; x++) {
                validateX(p, seam[y]);
                if (seam[y] != x) {
                    newPicture.setRGB(newX, y, p.getRGB(x, y));
                    newX++;
                }
            }
        }
        return newPicture;
    }

    private Picture transpose(Picture p) {
        int H = p.height();
        int W = p.width();
        Picture transposed = new Picture(H, W);
        for (int y = 0; y < H; y++)
            for (int x = 0; x < W; x++) {
                int rgb = p.getRGB(x, y);
                transposed.setRGB(y, x, rgb);
            }
        return transposed;
    }

    /**
     * @param p target picture
     * @param x horizintal coordinate 0 .. width - 1
     * @param y vertical coordinate 0 .. height - 1
     * @return energy of pixel at column x and row y
     */
    private double energy(Picture p, int x, int y) {
        validateX(p, x);
        validateY(p, y);
        if (x == 0 || x == p.width() - 1) return BORDER_ENERGY;
        if (y == 0 || y == p.height() - 1) return BORDER_ENERGY;
        return Math.sqrt(d2x(p, x, y) + d2y(p, x, y));
    }

    /**
     * Square of X gradient.
     * @param x horizintal coordinate 0 .. width - 1
     * @param y vertical coordinate 0 .. height - 1
     * @return square of X gradient
     */
    private double d2x(Picture p, int x, int y) {
        int right = p.getRGB(x + 1, y);
        int left  = p.getRGB(x - 1, y);
        
        double rx =   red(right) -   red(left);
        double gx = green(right) - green(left);
        double bx =  blue(right) -  blue(left);
        
        return rx*rx + gx*gx + bx*bx;
    }

    /**
     * Square of Y gradient.
     * @param x horizintal coordinate 0 .. width - 1
     * @param y vertical coordinate 0 .. height - 1
     * @return square of Y gradient
     */
    private double d2y(Picture p, int x, int y) {
        int below = p.getRGB(x, y + 1);
        int above = p.getRGB(x, y - 1);

        double ry =   red(below) -   red(above);
        double gy = green(below) - green(above);
        double by =  blue(below) -  blue(above);
        
        return ry*ry + gy*gy + by*by;
    }
    
    private int red(int rgb) {
        return (rgb >> 16) & 0xFF; 
    }
    
    private int green(int rgb) {
        return (rgb >> 8) & 0xFF;
    }
    
    private int blue(int rgb) {
        return rgb & 0xFF;
    }
    
    private void validateX(Picture p, int x) {
        if (x < 0 || x >= p.width())
            throw new IllegalArgumentException(
                "Coordinate x must be between 0 and " + (width() - 1) + ": " + x);
    }

    private void validateY(Picture p, int y) {
        if (y < 0 || y >= p.height())
            throw new IllegalArgumentException(
                "Coordinate y must be between 0 and " + (height() - 1) + ": " + y);
    }
}
