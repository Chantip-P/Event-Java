package cs211.project.services.comparators;

import cs211.project.models.User;

import java.util.Comparator;

public class UserLoggedInTimeComparator implements Comparator<User> {
    @Override
    public int compare(User user1, User user2) {
        return user2.getLoggedInTime().compareTo(user1.getLoggedInTime());
    }

}
