package cs211.project.services;

import cs211.project.models.Event;
import cs211.project.models.Team;

import java.util.ArrayList;

public class AuthenticationUtility {
    public static String userAuthorizeAs(Event event){
        ArrayList<Team> allEventTeam = event.getOrganizerTeamsAsObject();

        int count = 0;
        for (Team t : allEventTeam) {
            if (t.getMemberIDList().contains(ProgramUtility.getLoggedInUserID())) {
                count++;
            }
        }

        if (count == 1) {
            return "Organizer Team";
        }
        else if (count > 1) {
            return "Multiple Teams";
        }

        if (event.getParticipants().contains(ProgramUtility.getLoggedInUserID())) {
            return "Participant";
        }

        if (event.getEventCreatorUserID() == ProgramUtility.getLoggedInUserID()) {
            return "Event Organizer";
        }
        return "User";

    }
}
