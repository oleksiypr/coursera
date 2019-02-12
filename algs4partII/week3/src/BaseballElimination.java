/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

/**
 *
 */
public class BaseballElimination {

    /**
     * Create a baseball division from given filename in format specified below
     * @param filename name of the file
     */
    public BaseballElimination(String filename) {

    }

    /**
     * @return number of teams
     */
    public int numberOfTeams() {
        return -1;
    }

    /**
     * @return all teams
     */
    public Iterable<String> teams() {
        return null;
    }

    /**
     * @param team a team
     * @return number of wins for given team
     */
    public int wins(String team) {
        return -1;
    }

    /**
     * @param team a team
     * @return number of losses for given team
     */
    public int losses(String team) {
        return -1;
    }

    /**
     * @param team a team
     * @return number of remaining games for given team
     */
    public int remaining(String team) {
        return -1;
    }

    /**
     * @param team1 a team
     * @param team2 other team
     * @return number of remaining games between team1 and team2
     */
    public int against(String team1, String team2) {
        return -1;
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

}
