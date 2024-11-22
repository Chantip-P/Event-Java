package cs211.project.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cs211.project.models.Comment;
import cs211.project.models.collections.CommentList;

import java.io.*;

public class CommentListFileDatasource extends FileDatasource<CommentList> {

    public CommentListFileDatasource(String directoryName, String fileName) {
        super(directoryName, fileName);
    }

    @Override
    public CommentList readData() {
        CommentList commentList = new CommentList();

        BufferedReader buffer = createBufferedReader();
        Gson gson = new Gson();

        Comment[] eventArray = gson.fromJson(buffer, Comment[].class);
        for (Comment event : eventArray) {
            commentList.addComment(event);
        }

        return commentList;
    }


    @Override
    public void writeData(CommentList data) {
        BufferedWriter buffer = createBufferedWriter();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            buffer.write(gson.toJson(data.getComments()));
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
