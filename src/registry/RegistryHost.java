package registry;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RegistryHost {
    public static void main(String[] args) {
        try {
            System.out.println("=== Starting Programmatic RMI Registry ===");

            // Creates and starts an RMI registry instance in-memory on port 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            System.out.println("Status: RMI Registry successfully running on port 1099.");
            System.out.println("Press Ctrl+C in this terminal to shut down the registry.");

            // Keep the thread alive indefinitely to listen for server bindings
            Object lock = new Object();
            synchronized (lock) {
                lock.wait();
            }

        } catch (Exception e) {
            System.err.println("[FATAL ERROR] Failed to initialize RMI Registry instance:");
            e.printStackTrace();
        }
    }
}