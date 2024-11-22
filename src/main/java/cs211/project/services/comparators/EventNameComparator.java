package cs211.project.services.comparators;

import cs211.project.models.Event;

import java.util.Comparator;

public class EventNameComparator implements Comparator<Event> {
    @Override
    public int compare(Event event1, Event event2) {
        return event1.getEventName().toLowerCase().compareTo(event2.getEventName().toLowerCase());
    }

    @Override
    public String toString() {
        return "Event Name";
    }

}
