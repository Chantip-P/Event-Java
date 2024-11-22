package cs211.project.services.comparators;

import cs211.project.models.Event;

import java.util.Comparator;

public class EventParticipantComparator implements Comparator<Event> {
    @Override
    public int compare(Event event1, Event event2) {
        return Integer.compare(event1.getJoinedParticipants(),event2.getJoinedParticipants()) * -1;
    }

    @Override
    public String toString() {
        return "Number of Participant";
    }

}
