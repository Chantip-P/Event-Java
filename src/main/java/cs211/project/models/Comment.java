package cs211.project.models;

import cs211.project.models.collections.UserList;
import cs211.project.services.Datasource;
import cs211.project.services.UserListFileDatasource;

public class Comment {
    private int userID;
    private String comment;

    public Comment(int userID,String comment){
        this.userID = userID;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public String getName(){
        Datasource<UserList> datasource = new UserListFileDatasource("data","user-list.json");
        UserList userList = datasource.readData();
        User user = userList.findUserByID(this.userID);
        return user.getFormattedFullName();
    }

    public String getProfileImage(){
        Datasource<UserList> datasource = new UserListFileDatasource("data","user-list.json");
        UserList userList = datasource.readData();
        User user = userList.findUserByID(this.userID);

        return user.getProfileImagePath(true);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "userID=" + userID +
                ", comment='" + comment + '\'' +
                '}';
    }
}
