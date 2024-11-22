package cs211.project.models;

import at.favre.lib.crypto.bcrypt.BCrypt;
import cs211.project.services.CalculationUtility;
import cs211.project.services.Theme;
import cs211.project.services.UserListFileDatasource;
import java.util.Calendar;

public class User implements Trackable {
    private UserType type;
    private final int userID;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String profileImagePath;
    private Calendar loggedInTime;
    private Theme userDefaultTheme;

    public User(String username, String firstName, String lastName) {
        this.type = UserType.USER;
        this.userID = CalculationUtility.generateIDFromDataList(
                new UserListFileDatasource("data", "user-list.json")
                        .readData().getUsers()
        );
        this.username = username;
        this.password = null;
        setFirstName(firstName);
        setLastName(lastName);
        this.profileImagePath = "/images/user-default.png";
        this.loggedInTime = Calendar.getInstance();
        this.userDefaultTheme = Theme.LIGHT;
    }

    public User(String username, String firstName, String lastName, String imagePath) {
        this(username, firstName, lastName);
        this.profileImagePath = imagePath;
    }

    public boolean isUsername(String username) {
        return this.username.equals(username);
    }

    public boolean isID(int userID) { return this.userID == userID; }

    public void setPassword(String password) {
        this.password = BCrypt.withDefaults().hashToString(9, password.toCharArray());
    }

    public boolean verifyPassword(String password) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), this.password);
        return result.verified;
    }

    public void setUsername(String username) {
        if (!username.equals(this.username) &&
                !this.profileImagePath.equals("/images/user-default.png")) {
            String destDirPath = "data/images/profiles";
            CalculationUtility.renameFile(destDirPath, this.username, username);
            setProfileImagePath(destDirPath + "/" + CalculationUtility.findFile(destDirPath, username).getName());
        } this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName.substring(0, 1).toUpperCase() +
                        firstName.substring(1).toLowerCase();
    }

    public void setLastName(String lastName) {
        this.lastName = lastName.substring(0, 1).toUpperCase() +
                lastName.substring(1).toLowerCase();
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }

    public void setLoggedInTime(Calendar loggedInTime) { this.loggedInTime = loggedInTime; }

    public void setUserDefaultTheme(Theme theme) { this.userDefaultTheme = theme; }

    public UserType getType() { return type; }

    public int getID() { return userID; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getFormattedFullName() {
        return getFirstName() + " " + getLastName().substring(0, 1) + ".";
    }

    public String getProfileImagePath(boolean useRelativePath) {
        if (!useRelativePath) { return profileImagePath; }

        if (this.profileImagePath.equals("/images/user-default.png")) {
            return getClass().getResource(this.profileImagePath).toExternalForm();
        } else {
            return "file:" + this.profileImagePath;
        }
    }

    public Calendar getLoggedInTime() { return loggedInTime; }

    public Theme getUserDefaultTheme() { return userDefaultTheme; }

    @Override
    public String toString() {
        return "Last Login: " +
                CalculationUtility.formatDateTime(loggedInTime, true)
                + " , " +
                "User: " + username;
    }

}