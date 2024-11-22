package cs211.project.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cs211.project.models.collections.Schedule;
import cs211.project.models.collections.ScheduleList;

import java.io.*;

public class ScheduleListFileDatasource extends FileDatasource<ScheduleList> {

    public ScheduleListFileDatasource(String directoryName, String fileName) {
        super(directoryName, fileName);
    }

    @Override
    public ScheduleList readData() {
        Schedule.readingData = true;

        ScheduleList schedules = new ScheduleList();

        BufferedReader buffer = createBufferedReader();
        Gson gson = new Gson();

        Schedule[] scheduleArray = gson.fromJson(buffer, Schedule[].class);
        for (Schedule schedule : scheduleArray) {
            schedules.addSchedule(schedule);
        }

        Schedule.readingData = false;
        return schedules;
    }

    @Override
    public void writeData(ScheduleList data) {
        BufferedWriter buffer = createBufferedWriter();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            buffer.write(gson.toJson(data.getScheduleList()));
            buffer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                buffer.flush();
                buffer.close();
            }
            catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }
}
