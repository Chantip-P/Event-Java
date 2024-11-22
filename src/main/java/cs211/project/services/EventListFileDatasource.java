package cs211.project.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cs211.project.models.Event;
import cs211.project.models.collections.EventList;

import java.io.*;

public class EventListFileDatasource extends FileDatasource<EventList> {

    public EventListFileDatasource(String directoryName, String fileName) {
        super(directoryName, fileName);
    }

    @Override
    public EventList readData() {
        EventList eventList = new EventList();

        BufferedReader buffer = createBufferedReader();
        Gson gson = new Gson();

        Event[] eventArray = gson.fromJson(buffer, Event[].class);
        for (Event event : eventArray) {
            eventList.addEvent(event);
        }

        return eventList;
    }

    @Override
    public void writeData(EventList data) {
        BufferedWriter buffer = createBufferedWriter();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            buffer.write(gson.toJson(data.getEvents()));
            buffer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                buffer.flush();
                buffer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
