package edu.osu.hack.OSUGrades;

public class Grade {

    public String professorName;
    public String profile;
    public int GPA;

    public Grade() {
        professorName = "Unknown";
        GPA = 0;
        profile = "";
    }

    public Grade(String name, int GPA, String profile ) {
        professorName = name;
        this.GPA = GPA;
        this.profile = profile;
    }


    public int getGPA() {
        return GPA;
    }

    public String getProfile() {
        return profile;
    }

    public String getProfessorName() {
        return professorName;
    }
}
