package cs211.project.models;

import cs211.project.models.collections.Schedule;
import cs211.project.services.CalculationUtility;
import cs211.project.services.ScheduleListFileDatasource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Activity implements Trackable {

    private Calendar startDateTime;
    private Calendar endDateTime;
    private String activityName;
    private String description;
    private boolean forceEnd;
    private final int activityID;

    public Activity(String activityName, String description, Calendar startDateTime, Calendar endDateTime, int setID) {
        this.activityName = activityName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.description = description;
        this.forceEnd = false;

        if (setID == -1) {
            int maxID = -1;
            for (Schedule s : new ScheduleListFileDatasource("data", "schedule-list.json").readData()) {
                maxID = Math.max(CalculationUtility.generateIDFromDataList(s.getSchedule()), maxID);
            }
            activityID = maxID;
        }
        else {
            activityID = setID;
        }
    }

    public Activity(String activityName, String description, Calendar startDateTime, Calendar endDateTime) {
        this(activityName, description, startDateTime, endDateTime, -1);
    }

    public boolean checkActivityOnGoing(){
        return !(Calendar.getInstance().after(endDateTime) || forceEnd);
    }
    public void setForceEnd(){
        this.forceEnd = true;
    }

    public boolean getForceEnd(){
        return forceEnd;
    }

    public String getActivityName() {return activityName;}

    public Calendar getStartDateTime() {
        return startDateTime;
    }

    public Calendar getEndDateTime() {
        return endDateTime;
    }

    public int getID() {
        return activityID;
    }

    public String getDescription() {
        return description;
    }

    public Boolean isActivity(Calendar startDateTime, Calendar endDateTime){
        return this.startDateTime.equals(startDateTime) &&
                this.endDateTime.equals(endDateTime);
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return "Activity{" +
                "startDateTime=" + dateFormat.format(startDateTime) +
                ", endTime=" + dateFormat.format(endDateTime) +
                ", description='" + description + '\'' +
                '}';
    }

    public void close() {
        File file = CalculationUtility.findFile("data/comments", String.format("comment-list-%d.json", activityID));
        if (file != null) {
            try { Files.deleteIfExists(file.toPath()); }
            catch (IOException e) { throw new RuntimeException(e); }
        }
    }
}