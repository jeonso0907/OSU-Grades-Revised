package edu.osu.hack.OSUGrades;

import java.util.ArrayList;

public class ClassInfo {

    public ArrayList<String> professorName;
    public String CourseID;
    public double GPA;
    public int reported;
    public double rate;

    public ClassInfo() {
        professorName = new ArrayList<>();
        GPA = 0.0;
        reported = 0;
    }

    public ClassInfo(String className, double GPA, String professorName, double rate, int reported) {
        this.GPA = GPA;
        this.CourseID = className;
        this.professorName.add(professorName);
        this.rate = rate;
        this.reported = reported + 1;
    }

    public ClassInfo(String className, double GPA, double rate, int reported) {
        this.GPA += GPA;
        this.CourseID = className;
        this.rate = rate;
        this.reported = reported + 1;
    }
    public double getRate() { return rate; }

    public double getGPA() {
        return GPA;
    }

    public String getCourseID() { return CourseID; }

    public int getReported() { return reported; }

    public ArrayList<String> getProfessorName() {

        if ( professorName == null ) {
            return null;
        }
        else {
            return professorName;
        }
    }
}
