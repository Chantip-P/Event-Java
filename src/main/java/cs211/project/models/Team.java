package cs211.project.models;

import cs211.project.models.collections.Schedule;
import cs211.project.models.collections.ScheduleList;
import cs211.project.models.collections.UserList;
import cs211.project.services.*;
import java.util.ArrayList;
import java.util.Calendar;

public class Team implements Trackable{
    private final int teamID;
    private String teamName;

    private Calendar startDateTime;
    private Calendar endDateTime;
    private TeamAvailability availability;
    private ArrayList<Integer> memberIDList;
    private int maximumMember;
    private int scheduleID;


    public Team(String teamName, int maximumMember, Calendar startDateTime, Calendar endDateTime){
        this.teamName = teamName.trim();
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.maximumMember = maximumMember;
        memberIDList = new ArrayList<>();

        Datasource<ScheduleList> scheduleDatasource = new ScheduleListFileDatasource("data", "schedule-list.json");
        ScheduleList scheduleList = scheduleDatasource.readData();

        Schedule teamSchedule = new Schedule();
        scheduleID = teamSchedule.getID();

        scheduleList.addSchedule(teamSchedule);
        scheduleDatasource.writeData(scheduleList);

        availability = TeamAvailability.AVAILABLE;
        teamID = CalculationUtility.generateIDFromDataList(
                new TeamListFileDatasource("data", "team-list.json")
                        .readData().getTeams()
        );
    }

    public String getTeamName() {
        return teamName;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public Schedule getScheduleAsObject() {
        return new ScheduleListFileDatasource("data", "schedule-list.json").readData().findScheduleByID(scheduleID);
    }

    public Calendar getStartDateTime() { return startDateTime;  }
    public Calendar getEndDateTime() { return endDateTime; }

    public TeamAvailability getAvailability() {
        checkTeamAvailability();
        return availability;
    }

    public void checkTeamAvailability(){
         if (!isFull() && (Calendar.getInstance().after(startDateTime) && Calendar.getInstance().before(endDateTime))){
             availability = TeamAvailability.AVAILABLE;
         }
         else availability = TeamAvailability.UNAVAILABLE;
    }

    public boolean isFull() {
        return getCurrentMember() >= maximumMember;
    }

    public ArrayList<Integer> getMemberIDList() {
        return memberIDList;
    }
    public ArrayList<User> getMemberAsUser() {
        UserList userList = new UserListFileDatasource("data", "user-list.json").readData();
        ArrayList<User> members = new ArrayList<>();
        for (int userID : memberIDList) {
            User u = userList.findUserByID(userID);
            if (u != null) { members.add(u); }
        }
        return members;
    }

    public int getMaximumMember() { return maximumMember; }

    public int getCurrentMember(){
        return memberIDList.size();
    }

    public void addMemberIDList(int userID) {
        memberIDList.add(userID);
    }
    public void removeMemberIDList(int userID){ memberIDList.remove((Integer)userID); }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setMaximumMember(int maximumMember) {this.maximumMember = maximumMember;    }

    public void setStartDateTime(Calendar startDateTime) {this.startDateTime = startDateTime; }

    public void setEndDateTime(Calendar endDateTime) { this.endDateTime = endDateTime;   }

    public boolean isTeam(int teamID) {
        return teamID == this.teamID;
    }

    @Override
    public String toString() {
        return "Team{" +
                "teamID=" + teamID +
                ", teamName='" + teamName + '\'' +
                ", availability=" + availability +
                ", memberIDList=" + memberIDList +
                ", maximumMember=" + maximumMember +
                ", scheduleIDList=" + scheduleID +
                '}';
    }

    @Override
    public int getID() {
        return teamID;
    }
}
