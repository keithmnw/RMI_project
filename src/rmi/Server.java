/*
Name: Jayden Kinoti
Student Number: 220692
Date: 22/05/2026
*/

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {

    public static void main(String[] args) {

        try {

            LocateRegistry.createRegistry(1099);

            StudentServiceImpl service =
                    new StudentServiceImpl();

            Naming.rebind(
                    "rmi://localhost/StudentService",
                    service);

            System.out.println(
                    "Student RMI Server Running...");
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}