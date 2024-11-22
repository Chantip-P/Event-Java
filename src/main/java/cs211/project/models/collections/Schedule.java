package cs211.project.models.collections;

import cs211.project.models.Activity;
import cs211.project.models.Trackable;
import cs211.project.services.CalculationUtility;
import cs211.project.services.ScheduleListFileDatasource;
import cs211.project.services.comparators.ActivityStartDateTimeComparator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Comparator;

public class Schedule implements Trackable ,Iterable<Activity>{
    public static boolean readingData = false;

    private final int scheduleID;
    private ArrayList<Activity> schedule;

    public Schedule() {
        scheduleID = readingData
                ? -1
                : CalculationUtility.generateIDFromDataList(
                new ScheduleListFileDatasource("data", "schedule-list.json")
                        .readData().getScheduleList());
        schedule = new ArrayList<>();
    }

    public int getID() { return scheduleID; }

    public boolean addActivity(Activity activity) {
        if (activity.getStartDateTime() != null && activity.getEndDateTime() != null) {

            if (!hasOverlappingActivities(activity.getStartDateTime(), activity.getEndDateTime())){
                schedule.add(activity);
                sort(new ActivityStartDateTimeComparator());

                return true;
            }
        }
        return false;
    }

    public void removeActivity(Activity activity, boolean deleteCommentFile) {
        if (activity.getStartDateTime() != null && activity.getEndDateTime() != null) {
            Activity exist = activityExist(activity.getStartDateTime(), activity.getEndDateTime());

            if (exist == null) { return; }

            if (deleteCommentFile) { exist.close(); }
            schedule.remove(exist);
        }
    }

    private boolean hasOverlappingActivities(Calendar newStartTime, Calendar newEndTime) {
        for (Activity activity : schedule) {
            Calendar existingStartTime = activity.getStartDateTime();
            Calendar existingEndTime = activity.getEndDateTime();

            if ((newStartTime.after(existingStartTime) && newStartTime.before(existingEndTime)) ||
                    (newEndTime.after(existingStartTime) && newEndTime.before(existingEndTime)) ||
                    (existingStartTime.after(newStartTime) && existingStartTime.before(newEndTime)) ||
                    (existingEndTime.after(newStartTime) && existingEndTime.before(newEndTime))
            ) {
                return true;
            }
        }
        return false;
    }

    public Activity activityExist(Calendar startDateTime, Calendar endDateTime){
        for(Activity activity : schedule){
            if(activity.isActivity(startDateTime,endDateTime)){
                return activity;
            }
        }
        return null;
    }

    public ArrayList<Activity> getSchedule() {
        return schedule;
    }

    public boolean isScheduleID(int scheduleID) {
        return this.scheduleID == scheduleID;
    }

    public void sort(Comparator<Activity> comparator) {
        schedule.sort(comparator);
    }

    @Override
    public Iterator<Activity> iterator() {
        return schedule.iterator();
    }
}
