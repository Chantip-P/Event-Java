package cs211.project.services.comparators;

import cs211.project.models.Activity;

import java.util.Comparator;

public class ActivityStartDateTimeComparator implements Comparator<Activity> {
    @Override
    public int compare(Activity activity1, Activity activity2) {
        return activity1.getStartDateTime().compareTo(activity2.getStartDateTime());
    }

}
