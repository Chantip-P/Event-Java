package cs211.project.models.collections;
import cs211.project.models.Team;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
public class TeamList implements Iterable<Team> {
    private ArrayList <Team> teams;

    public TeamList() { teams = new ArrayList<>(); }

    public void addTeam(Team team){ teams.add(team); }

    public Team findTeamByID(int teamID){
        for (Team team : teams){
            if (team.isTeam(teamID)){
                return team;
            }
        }
        return null;
    }

    @Override
    public Iterator<Team> iterator() { return teams.iterator();
    }

    public void sort(Comparator<Team> comparator) {
        teams.sort(comparator);
    }

    public ArrayList<Team> getTeams() { return teams; }
}
