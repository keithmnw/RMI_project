/*
Name: Joe Migwi
Index: 220835
Date: 22/05/2026
 */
package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("=== RMI Console Client Diagnostic Boot ===");
            System.out.print("Connecting to local RMI Registry namespace...");

            // Connection looks locally for now; easily switched to Radmin IP later
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            MyInterface stub = (MyInterface) registry.lookup("Obj1");

            System.out.println(" [CONNECTED]\n");

            // Test execution 1: Base greeting interface test (Questions 1 & 2)
            System.out.print("Enter your name to test basic server connection: ");
            String nameInput = scanner.nextLine();
            String greetingResponse = stub.greetMe(nameInput);
            System.out.println("Server verification response: " + greetingResponse + "\n");

            // Test execution 2: String processing loop (Question 3)
            System.out.println("=== Starting Continuous Remote String Processor ===");
            System.out.println("Type text lines to send to the server. (Type 'exit' to quit)");

            while (true) {
                System.out.print("\nInput Data Stream > ");
                String textInput = scanner.nextLine().trim();

                if (textInput.equalsIgnoreCase("exit")) {
                    System.out.println("Closing client diagnostic loop. Goodbye.");
                    break;
                }

                if (textInput.isEmpty()) {
                    System.out.println("Skipping transaction: Empty data lines are invalid.");
                    continue;
                }

                System.out.println("Piping stream payload to remote execution server...");
                String processedResult = stub.processInput(textInput);
                System.out.println("Processed Data Result: " + processedResult);
            }

        } catch (Exception e) {
            System.err.println("\n[FATAL ERROR] Remote communication routine failed!");
            System.err.println("Root Cause Analysis details below:");
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}