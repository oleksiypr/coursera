/* *****************************************************************************
 *  Name: Oleksii Prosianko
 *  Date: 2019/03/04
 *  Description: baseball elimination problem
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;
import java.util.Collections;

/**
 * In the baseball elimination problem, there is a division consisting of n
 * teams. At some point during the season, team i has w[i] wins, l[i] losses,
 * r[i] remaining games, and g[i][j] games left to play against team j. A team
 * is mathematically eliminated if it cannot possibly finish the season in (or
 * tied for) first place. The goal is to determine exactly which teams are
 * mathematically eliminated. For simplicity, we assume that no games end in a
 * tie (as is the case in Major League Baseball) and that there are no rainouts
 * (i.e., every scheduled game is played).
 */
public class BaseballElimination {

    private final ST<String, Integer> teams;
    private final int[] wins;
    private final int[] loses;
    private final int[] remaining;
    private final int[][] g; // games left between i and j

    /**
     * Create a baseball division from given filename in format specified below
     * @param filename name of the file
     */
    public BaseballElimination(String filename) {
        In in = new In(filename);
        int n =  Integer.parseInt(in.readLine());

        teams   = new ST<>();
        wins    = new int[n];
        loses   = new int[n];
        g       = new int[n][n];
        remaining = new int[n];

        int i = 0;
        while (!in.isEmpty()) {
            String row = in.readLine();
            if (row == null) continue;

            String[] line = row.trim().split("\\s+");
            teams.put(line[0], i);
            wins[i]      = Integer.parseInt(line[1]);
            loses[i]     = Integer.parseInt(line[2]);
            remaining[i] = Integer.parseInt(line[3]);
            for (int k = 4; k < n + 4; k++) {
                int j = k - 4;
                g[i][j] = Integer.parseInt(line[k]);
            }
            i++;
        }
        in.close();
    }

    /**
     * @return number of teams
     */
    public int numberOfTeams() {
        return teams.size();
    }

    /**
     * @return all teams
     */
    public Iterable<String> teams() {
        return teams.keys();
    }

    /**
     * @param team a team
     * @return number of wins for given team
     */
    public int wins(String team) {
        verify(team);
        return wins[teams.get(team)];
    }

    /**
     * @param team a team
     * @return number of losses for given team
     */
    public int losses(String team) {
        verify(team);
        return loses[teams.get(team)];
    }

    /**
     * @param team a team
     * @return number of remaining games for given team
     */
    public int remaining(String team) {
        verify(team);
        return remaining[teams.get(team)];
    }

    /**
     * @param team1 one team
     * @param team2 other team
     * @return number of remaining games between team1 and team2
     */
    public int against(String team1, String team2) {
        verify(team1);
        verify(team2);
        int i = teams.get(team1);
        int j = teams.get(team2);
        return g[i][j];
    }

    /**
     * @param team a team
     * @return true if given team eliminated
     */
    public boolean isEliminated(String team) {
        verify(team);
        int k = teams.get(team);
        return isTrivialEliminated(k) || isNonTriviallyEliminted(k);
    }

    /**
     * @param team a team
     * @return subset of teams that eliminates given team; null if not
     * eliminated
     */
    public Iterable<String> certificateOfElimination(String team) {
        verify(team);
        int k = teams.get(team);
        for (String tm: teams.keys()) {
            int i = teams.get(tm);
            if (i == k) continue;
            if (wins[k] + remaining[k] < wins[i]) return Collections.singletonList(tm);
        }

        Bag<String> eliminates = new Bag<>();
        FordFulkerson ff = ff(k);
        for (String tm: teams.keys()) {
            int i = teams.get(tm);
            if (i == k) continue;
            if (ff.inCut(i)) eliminates.add(tm);
        }
        return eliminates.isEmpty() ? null : eliminates;
    }

    private boolean isTrivialEliminated(int k) {
        for (int i = 0; i < teams.size(); i++) {
            if (i == k) continue;
            if (wins[k] + remaining[k] < wins[i]) return true;
        }
        return false;
    }

    private boolean isNonTriviallyEliminted(int k) {
        FordFulkerson ff = ff(k);
        for (int i = 0; i < teams.size(); i++) {
            if (i == k) continue;
            if (ff.inCut(i)) return true;
        }
        return false;
    }

    /**
     * The {@link FordFulkerson} instanse to check whether team is eliminated.
     * @param k a team to be chacked
     * @return FordFulkerson insatnce
     */
    private FordFulkerson ff(int k) {
        final int n = teams.size();
        final int s = n;
        final int t = n + 1;
        final int games = (n - 2)*(n - 1)/2;
        final int V = games + n + 2;
        FlowNetwork G = new FlowNetwork(V);

        int game = n + 2;
        for (int i = 0; i < n - 1; i++) {
            if (i == k) continue;
            for (int j = i + 1; j < n; j++) {
                if (j == k) continue;
                G.addEdge(new FlowEdge(s, game, g[i][j]));
                G.addEdge(new FlowEdge(game, i, Double.POSITIVE_INFINITY));
                G.addEdge(new FlowEdge(game, j, Double.POSITIVE_INFINITY));
                game++;
            }
        }

        for (int i = 0; i < n; i++) {
            if (i == k) continue;
            double capacity = wins[k] + remaining[k] - wins[i];
            G.addEdge(new FlowEdge(i, t, capacity));
        }

        return new FordFulkerson(G, s, t);
    }

    private void verify(String team) {
        if (!teams.contains(team))
            throw new IllegalArgumentException("No such team");
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
