/*
Name: Jayden Kinoti
Student Number: 220692
Date: 22/05/2026
*/

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class StudentServiceImpl
        extends UnicastRemoteObject
        implements StudentService {

    public StudentServiceImpl()
            throws RemoteException {

        super();
    }

    @Override
    public ArrayList<Student> getStudents()
            throws RemoteException {

        ArrayList<Student> students =
                new ArrayList<>();

        try {

            Connection con =
                    DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/school",
                            "root",
                            "123456789");

            Statement stmt =
                    con.createStatement();

            ResultSet rs =
                    stmt.executeQuery(
                            "SELECT * FROM student_data");

            while (rs.next()) {

                Student s = new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("course"),
                        rs.getInt("score"),
                        rs.getString("email")
                );

                students.add(s);
            }

            rs.close();
            stmt.close();
            con.close();

        }
        catch (Exception e) {

            e.printStackTrace();
        }

        return students;
    }
}