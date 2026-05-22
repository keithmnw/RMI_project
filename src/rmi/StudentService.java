/*
Name: Jayden Kinoti
Student Number: 220692
Date: 22/05/2026
*/

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface StudentService extends Remote {

    ArrayList<Student> getStudents()
            throws RemoteException;
}