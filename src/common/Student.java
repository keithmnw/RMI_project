package server;/*
Name: Jayden Kinoti
server.Student Number: 220692
Date: 22/05/2026
*/

import java.io.Serializable;

public class Student implements Serializable {

    private int id;
    private String name;
    private String course;
    private int score;
    private String email;

    public Student(int id, String name, String course,
                   int score, String email) {

        this.id = id;
        this.name = name;
        this.course = course;
        this.score = score;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCourse() {
        return course;
    }

    public int getScore() {
        return score;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {

        return id + " - " +
                name + " - " +
                course + " - " +
                score + " - " +
                email;
    }
}