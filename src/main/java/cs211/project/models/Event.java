package cs211.project.models;

import cs211.project.models.collections.Schedule;
import cs211.project.models.collections.ScheduleList;
import cs211.project.models.collections.TeamList;
import cs211.project.models.collections.UserList;
import cs211.project.services.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class Event implements Trackable {
    // Data Keeping
    private final int eventID;
    private final int eventCreatorUserID;
    private EventVisibility eventVisibility = EventVisibility.DRAFT;
    private ArrayList<Integer> bannedUsersIDList;

    // General Info
    private String eventName, eventDescription;
    private Calendar startDateTime, endDateTime;

    // Participants Info
    private ArrayList<Integer> participantsIDList;
    private Calendar participantAcceptStartDateTime, participantAcceptEndDateTime;
    private int maxParticipants;

    // Organizer Teams Info
    private ArrayList<Integer> organizerTeamsIDList;

    // Schedules Info
    private int eventScheduleID;

    public Event(String eventName, String eventDescription, Calendar startDateTime, Calendar endDateTime) {
        this.eventName = eventName.trim();
        this.eventDescription = eventDescription.trim();
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;

        this.maxParticipants = 1;

        bannedUsersIDList = new ArrayList<>();
        participantsIDList = new ArrayList<>();
        organizerTeamsIDList = new ArrayList<>();

        Datasource<ScheduleList> scheduleDatasource = new ScheduleListFileDatasource("data", "schedule-list.json");
        ScheduleList scheduleList = scheduleDatasource.readData();

        Schedule eventSchedule = new Schedule();
        eventScheduleID = eventSchedule.getID();

        scheduleList.addSchedule(eventSchedule);
        scheduleDatasource.writeData(scheduleList);

        eventID = CalculationUtility.generateIDFromDataList(
                new EventListFileDatasource("data", "event-list.json")
                        .readData().getEvents()
        );
        eventCreatorUserID = ProgramUtility.getLoggedInUserID();
    }

    public int getID() { return eventID; }
    public int getEventCreatorUserID() { return eventCreatorUserID; }
    public EventVisibility getEventVisibility() { return eventVisibility; }
    public String getEventName() { return eventName; }
    public String getEventDescription() { return eventDescription; }
    public String getEventBannerPath() {
        String directoryPath = "data/images/event-banners";
        File directory = new File(directoryPath);
        if (!directory.exists()) { directory.mkdirs(); }

        File[] fileList = directory.listFiles();
        if (fileList != null) {
            for (File f : fileList) {
                String fn = f.getName();
                String fileName = fn.substring(0, fn.lastIndexOf("."));
                String fileExtension = fn.substring(fn.lastIndexOf("."));
                if (((Integer) eventID).toString().equals(fileName)) {
                    return "file:" + directoryPath + "/" + eventID + fileExtension;
                }
            }
        }

        return getClass().getResource("/images/banner-default.png").toExternalForm();
    }
    public Calendar getStartDateTime() { return startDateTime; }
    public Calendar getEndDateTime() { return endDateTime; }
    public ArrayList<Integer> getParticipants() { return participantsIDList; }
    public ArrayList<User> getParticipantsAsUser() {
        UserList userList = new UserListFileDatasource("data", "user-list.json").readData();
        ArrayList<User> participants = new ArrayList<>();
        for (int userID : participantsIDList) {
            User u = userList.findUserByID(userID);
            if (u != null) { participants.add(u); }
        }
        return participants;
    }
    public int getJoinedParticipants() { return participantsIDList.size(); }
    public Calendar getParticipantAcceptStartDateTime() { return participantAcceptStartDateTime; }
    public Calendar getParticipantAcceptEndDateTime() { return participantAcceptEndDateTime;}
    public int getMaxParticipants() { return maxParticipants; }
    public ArrayList<Integer> getOrganizerTeams() { return organizerTeamsIDList; }
    public ArrayList<Team> getOrganizerTeamsAsObject() {
        TeamList teamList = new TeamListFileDatasource("data", "team-list.json").readData();
        ArrayList<Team> teams = new ArrayList<>();
        for (int teamID : organizerTeamsIDList) {
            Team t = teamList.findTeamByID(teamID);
            if (t != null) { teams.add(t); }
        }

        return teams;
    }
    public ArrayList<Integer> getBannedUsersIDList() {return bannedUsersIDList;}
    public int getEventScheduleID() { return eventScheduleID; }
    public Schedule getEventScheduleAsObject() {
        return new ScheduleListFileDatasource("data", "schedule-list.json").readData().findScheduleByID(eventScheduleID);
    }

    public void setEventName(String eventName) { this.eventName = eventName.trim(); }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription.trim(); }
    public void setStartDateTime(Calendar startDateTime) { this.startDateTime = startDateTime; }
    public void setEndDateTime(Calendar endDateTime) { this.endDateTime = endDateTime; }
    public void setParticipantAcceptStartDateTime(Calendar participantAcceptStartDateTime) { this.participantAcceptStartDateTime = participantAcceptStartDateTime; }
    public void setParticipantAcceptEndDateTime(Calendar participantAcceptEndDateTime) { this.participantAcceptEndDateTime = participantAcceptEndDateTime; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public boolean isEvent(int eventID){
        return this.eventID == eventID;
    }

    public void publishEvent() {
        eventVisibility = EventVisibility.PUBLIC;
    }

    public boolean seatAvailable() {
        return isBelowMaxParticipants() && isWithinParticipantAcceptanceTime();
    }

    public boolean isBelowMaxParticipants() { return getJoinedParticipants() < maxParticipants; }

    public boolean isWithinParticipantAcceptanceTime() {
        Calendar now = Calendar.getInstance();
        return now.after(participantAcceptStartDateTime) && now.before(participantAcceptEndDateTime);
    }

    public boolean teamSeatAvailable() {
        if (organizerTeamsIDList.isEmpty()) { return false; }

        Datasource<TeamList> teamListFileDatasource = new TeamListFileDatasource("data","team-list.json");
        TeamList teams = teamListFileDatasource.readData();

        for (int teamID : organizerTeamsIDList) {
            Team t = teams.findTeamByID(teamID);
            if (t.getAvailability() == TeamAvailability.AVAILABLE) {
                return true;
            }
        }

        return false;
    }

    public void joinEvent(int userId) {
        if (!seatAvailable()) { return; }
        participantsIDList.add(userId);
    }

    public void ban(int userID) {
        if (bannedUsersIDList.contains(userID)) { return; }
        bannedUsersIDList.add(userID);

        participantsIDList.remove((Integer)userID);

        Datasource<TeamList> teamListFileDatasource = new TeamListFileDatasource("data","team-list.json");
        TeamList teams = teamListFileDatasource.readData();

        for (int teamId : organizerTeamsIDList) {
            Team t = teams.findTeamByID(teamId);
            if (t.getMemberIDList().contains(userID)) {
                t.removeMemberIDList(userID);
                teamListFileDatasource.writeData(teams);
            }
        }
    }

    public void addTeamByID(int teamID) {
        organizerTeamsIDList.add(teamID);
    }

    public int checkEventStatus() {
        if (Calendar.getInstance().before(startDateTime)) { return -1; } // Soon
        else if (Calendar.getInstance().after(endDateTime)) { return 1; } // Ended
        else { return 0; } // On Going
    }

}
