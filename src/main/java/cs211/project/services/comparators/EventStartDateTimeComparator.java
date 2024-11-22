package cs211.project.services.comparators;

import cs211.project.models.Activity;
import cs211.project.models.Event;

import java.util.Comparator;

public class EventStartDateTimeComparator implements Comparator<Event> {
    @Override
    public int compare(Event event1, Event event2) {
        return event1.getStartDateTime().compareTo(event2.getStartDateTime());
    }

    @Override
    public String toString() {
        return "Event Start Date";
    }

}
