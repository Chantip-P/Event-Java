package cs211.project.services.comparators;

import cs211.project.models.Team;

import java.util.Comparator;

public class TeamComparator implements Comparator<Team> {

    @Override
    public int compare(Team team1, Team team2) {
        if (team1.getAvailability() != team2.getAvailability()) {
            return team1.getAvailability().compareTo(team2.getAvailability());
        } else if (team1.getStartDateTime() != team2.getStartDateTime()) {
            return team1.getStartDateTime().compareTo(team2.getStartDateTime());
        } else {
            return team1.getTeamName().compareTo(team2.getTeamName());
        }
    }
}
