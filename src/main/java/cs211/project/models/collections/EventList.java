package cs211.project.models.collections;

import cs211.project.models.Event;

import java.util.Comparator;
import java.util.Iterator;
import java.util.ArrayList;

public class EventList implements Iterable<Event>{
    private ArrayList<Event> events;

    public EventList() {
        events = new ArrayList<>();
    }

    public void addEvent(Event e) {
        events.add(e);
    }

    public Event findEventByID( int targetEventID) {
        for (Event event : events) {
            if (event.isEvent(targetEventID)) {
                return event;
            }
        }
        return null;
    }

    public void sort(Comparator<Event> comparator){
        events.sort(comparator);
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    @Override
    public Iterator<Event> iterator() {
        return events.iterator();
    }
}
