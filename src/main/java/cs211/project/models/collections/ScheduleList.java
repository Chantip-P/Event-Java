package cs211.project.models.collections;

import java.util.ArrayList;
import java.util.Iterator;

public class ScheduleList implements Iterable<Schedule> {
    private ArrayList<Schedule> schedules;

    public ScheduleList() {schedules = new ArrayList<>(); }

    public void addSchedule(Schedule schedule) {schedules.add(schedule);}

    public Schedule findScheduleByID(int targetScheduleID) {
        for (Schedule schedule : schedules) {
            if (schedule.isScheduleID(targetScheduleID)) {
                return schedule;
            }
        }
        return null;
    }

    public ArrayList<Schedule> getScheduleList() {return schedules;}

    @Override
    public Iterator<Schedule> iterator() {
        return schedules.iterator();
    }
}
