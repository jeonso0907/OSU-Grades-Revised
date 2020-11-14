package edu.osu.hack.OSUGrades;

public class User {


    public String userID;
    public String userPassword;
    public String userEmail;

    public User() {
        userID = "Unknown";
        userPassword = "";
        userEmail = "";
    }

    public User(String ID, String Password, String Email) {
        userID = ID;
        userPassword = Password;
        userEmail = Email;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserPassword() {
        return userPassword;
    }


    public String getUserEmail() {
        return userEmail;
    }

}
