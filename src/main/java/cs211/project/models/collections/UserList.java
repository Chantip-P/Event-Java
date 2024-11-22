package cs211.project.models.collections;

import cs211.project.models.User;
import cs211.project.services.Theme;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;

public class UserList {
    private ArrayList<User> users;

    public UserList() {
        users = new ArrayList<>();
    }

    public User findUserByUsername(String username) {
        for (User user : users) {
            if (user.isUsername(username)) {
                return user;
            }
        }
        return null;
    }

    public User findUserByID(int userID) {
        int firstIndex = 0, lastIndex = users.size() - 1;
        while (firstIndex <= lastIndex){
            int middleIndex = (firstIndex + lastIndex) / 2;

            User user = users.get(middleIndex);
            if (user.getID() < userID) {
                firstIndex = middleIndex + 1;
            } else if (user.isID(userID)) {
                return user;
            } else {
                lastIndex = middleIndex - 1;
            }
        }
        return null;
    }

    public void addUser(User user) {
        User exist = findUserByUsername(user.getUsername());
        if (exist == null) {
            users.add(user);
        }
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void changePassword(int userID, String oldPassword, String newPassword) {
        User exist = findUserByID(userID);
        if (exist != null && exist.verifyPassword(oldPassword)) {
            exist.setPassword(newPassword);
        }
    }

    public User login(String username, String password) {
        User exist = findUserByUsername(username);
        if (exist != null && exist.verifyPassword(password)) {
            exist.setLoggedInTime(Calendar.getInstance());
            return exist;
        }
        return null;
    }

    public void changeFirstName(int userID, String newFirstName) {
        User exist = findUserByID(userID);
        if (exist != null) {
            exist.setFirstName(newFirstName);
        }
    }

    public void changeLastName(int userID, String newLastName) {
        User exist = findUserByID(userID);
        if (exist != null) {
            exist.setLastName(newLastName);
        }
    }

    public void changeProfileImage(String username, String newImagePath) {
        User exist = findUserByUsername(username);
        if (exist != null) {
            exist.setProfileImagePath(newImagePath);
        }
    }

    public void changeUserDefaultTheme(int userID, Theme theme) {
        User exist = findUserByID(userID);
        if (exist != null) {
            exist.setUserDefaultTheme(theme);
        }
    }

    public void sort(Comparator<User> comparator) {
        users.sort(comparator);
    }

}