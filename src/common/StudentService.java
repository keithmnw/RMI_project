// Name: John Keith Muleshe  Student number: 220655

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface StudentService extends Remote {

    // Q1 - Returns greeting with name
    String greet(String name) throws RemoteException;

    // Q3 - Echoes back user input with prefix
    String echo(String userInput) throws RemoteException;

    // Q4 - Returns list of students from database
    List<String[]> getStudents() throws RemoteException;

}
