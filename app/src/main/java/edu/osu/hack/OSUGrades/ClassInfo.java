package edu.osu.hack.OSUGrades;

import java.util.ArrayList;

public class ClassInfo {

    public ArrayList<String> professorName;
    public String CourseID;
    public double GPA;
    public long reported;
    public long rate;

    public ClassInfo() {
        professorName = new ArrayList<>();
        GPA = 0;
        reported = 0;
    }

    public ClassInfo(String className, double GPA, ArrayList<String> list, long rate, long reported) {
        this.GPA = GPA;
        this.CourseID = className;
        this.professorName = list;
        this.rate = rate;
        this.reported = reported;
    }

    public ClassInfo(String className, double GPA, long rate, long reported) {
        this.GPA += GPA;
        this.CourseID = className;
        this.rate = rate;
        this.reported = reported;
    }
    public long getRate() { return rate; }

    public double getGPA() {
        return GPA;
    }

    public double getAverage() { return GPA / reported ; }

    public double getAverageRate() { return rate / reported ; }

    public String getCourseID() { return CourseID; }

    public long getReported() { return reported; }

    public ArrayList<String> getProfessorName() {

        if ( professorName == null ) {
            return null;
        }
        else {
            return professorName;
        }
    }
}
