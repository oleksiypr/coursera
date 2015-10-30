import edu.princeton.cs.algs4.*;

/**
 * Monte Carlo simulation to estimate the percolation threshold. Use to perform
 * a series of computational experiments.
 * 
 * @author Oleksiy_Prosyanko
 */
public class PercolationStats {
    private int[] s;    // number of open sites in each experiment, 0..(T - 1)
    private int M;      // total number of sites
    private int N;
    private int T;

    /**
     * Perform T independent experiments on an N-by-N grid.
     * @param N grid size
     * @param T number of experiments
     */
    public PercolationStats(int N, int T) {
        if (N <= 0 || T <= 0) throw new IllegalArgumentException("Given N <= 0 || T <= 0");

        this.N = N;
        this.T = T;
        this.M = N * N;
        this.s = new int[T];
        perform();
    }

    /**
     * Sample mean of percolation threshold. 
     * @return sample mean of percolation threshold
     */
    public double mean() {
        return meanS()/M;
    }

    /**
     * Sample standard deviation of percolation threshold. 
     * @return sample standard deviation of percolation threshold
     */
    public double stddev() {
        return Math.sqrt(dispS())/M;
    }

    /**
     * Low end-point of 95% confidence interval. 
     * @return low end-point of 95% confidence interval
     */
    public double confidenceLo() {
        return mean() - 1.96*stddev()/Math.sqrt(T);
    }

    /**
     * High end-point of 95% confidence interval. 
     * @return high endpoint of 95% confidence interval
     */
    public double confidenceHi() {
        return mean() + 1.96*stddev()/Math.sqrt(T);
    }

    // mean value of open sites when the system percalates
    private double meanS() {
        int sum = 0;
        for (int t = 0; t < T; t++) {
            sum += s[t];
        }
        return ((double) sum)/T;
    }

    // dispersion of values in array s
    private double dispS() {
        double m = meanS();
        double sumDev = 0.0;
        for (int t = 0; t < T; t++) {
            double dev = s[t] - m;
            sumDev += dev*dev;
        }
        return sumDev/(T - 1);
    }

    private void perform() {
        for (int t = 0; t < T; t++) {
            Percolation percolation = new Percolation(N);
            int openSites = 0;
            while (!percolation.percolates()) {
                int i = StdRandom.uniform(1, N + 1);
                int j = StdRandom.uniform(1, N + 1);
                if (!percolation.isOpen(i, j)) {
                    percolation.open(i, j);
                    openSites++;
                }
            }
            s[t] = openSites;
        }
    }

    public static void main(String[] args) {
        int N = Integer.valueOf(args[0]);
        int T = Integer.valueOf(args[1]);
        Stopwatch stopwatch = new Stopwatch();
        PercolationStats stats = new PercolationStats(N, T);
        double time = stopwatch.elapsedTime();
        StdOut.println("mean                    = " + stats.mean());
        StdOut.println("stddev                  = " + stats.stddev());
        StdOut.println("95% confidence interval = " + stats.confidenceLo() + " " + stats.confidenceHi());
        StdOut.println("elapsed time            = " + time);
    }
}
