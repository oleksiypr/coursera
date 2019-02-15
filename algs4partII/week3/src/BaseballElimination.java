/* *****************************************************************************
 *  Name: Oleksii Prosianko
 *  Date:
 *  Description: baseball elimination problem
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;

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

        System.out.println("n = " + n);

        teams   = new ST<>();
        wins    = new int[n];
        loses   = new int[n];
        g       = new int[n][n];
        remaining = new int[n];

        int i = 0;
        while (!in.isEmpty()) {
            String[] line = in.readLine().split("\\s+");
            teams.put(line[0], i);
            wins[i]      = Integer.parseInt(line[1]);
            loses[i]     = Integer.parseInt(line[2]);
            remaining[i] = Integer.parseInt(line[3]);
            for (int j = 4; j < n; j++) {
                g[i][j] = Integer.parseInt(line[j]);
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
     * @return is given team eliminated?
     */
    public boolean isEliminated(String team) {
        return false;
    }

    /**
     * @param team a team
     * @return subset R of teams that eliminates given team; null if not
     * eliminated
     */
    public Iterable<String> certificateOfElimination(String team) {
        return null;
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
