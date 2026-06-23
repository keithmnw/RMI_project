package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import common.StudentService; // Importing from the new common package

public class Server {
    public static void main(String[] args) {
        try {
            System.out.println("=== Launching RMI Production Server ===");
            System.out.print("Instantiating Remote Service Implementation layer...");

            // 1. Instantiate the remote object implementation
            StudentServiceImpl serviceImpl = new StudentServiceImpl();
            System.out.println(" [OK]");

            System.out.print("Locating central RMI Registry on localhost:1099...");
            // 2. Locate the registry running locally on port 1099
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            System.out.println(" [FOUND]");

            System.out.print("Binding service instance stub to namespace identifier 'Obj1'...");
            // 3. Bind the remote object stub in the registry
            registry.rebind("Obj1", serviceImpl);
            System.out.println(" [BOUND]");

            System.out.println("\nStatus: RMI Server is operational and listening for remote method calls.");

        } catch (Exception e) {
            System.err.println("\n[FATAL ERROR] Server subsystem initialization aborted:");
            e.printStackTrace();
        }
    }
}