package cs211.project.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cs211.project.models.User;
import cs211.project.models.collections.UserList;
import java.io.*;

public class UserListFileDatasource extends FileDatasource<UserList> {

    public UserListFileDatasource(String directoryName, String fileName) {
        super(directoryName, fileName);
    }

    @Override
    public UserList readData() {
        UserList users = new UserList();

        BufferedReader buffer = createBufferedReader();
        Gson gson = new Gson();

        User[] userArray = gson.fromJson(buffer, User[].class);
        for (User user : userArray) {
            users.addUser(user);
        }

        return users;
    }

    @Override
    public void writeData(UserList data) {
        BufferedWriter buffer = createBufferedWriter();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            buffer.write(gson.toJson(data.getUsers()));
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
