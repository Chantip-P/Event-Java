package cs211.project.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cs211.project.models.Team;
import cs211.project.models.collections.TeamList;

import java.io.*;

public class TeamListFileDatasource extends FileDatasource<TeamList> {

    public TeamListFileDatasource(String directoryName, String fileName) {
        super(directoryName, fileName);
    }

    @Override
    public TeamList readData() {
        TeamList teams = new TeamList();

        BufferedReader buffer = createBufferedReader();
        Gson gson = new Gson();

        Team[] teamArray = gson.fromJson(buffer, Team[].class);
        for (Team team : teamArray) {
            teams.addTeam(team);
        }

        return teams;
    }

    @Override
    public void writeData(TeamList data) {
        BufferedWriter buffer = createBufferedWriter();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            buffer.write(gson.toJson(data.getTeams()));
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
