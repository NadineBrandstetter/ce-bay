package at.jku.ce.bay.app;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import at.jku.ce.bay.utils.CEBayHelper;
import client.Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class App {
    public static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    //Added random number to system and actor name
    private static Random rnd = new Random();
    private static ActorSystem actorSystem = ActorSystem.create("ClientActor"+rnd.nextInt());
    private static ActorRef actor = actorSystem.actorOf(Client.props(), "ClientActor"+rnd.nextInt());

    public static void main(String[] args) {
        System.out.println("********************************");
        System.out.println("Der ClientActor wurde gestartet!");
        System.out.println("********************************\n");
        printMenu();
    }

    public static void printMenu() {
        String entry = "";
        try {
            System.out.println("*************************************************");
            System.out.println("Enter 'A' to list all files that are available!");
            System.out.println("Enter 'B' to get a specific file!");
            System.out.println("Enter 'C' to terminate the actor system.");
            System.out.println("*************************************************");
            System.out.print("Enter your choice: ");
            entry = in.readLine();
            if(entry.equalsIgnoreCase("A")) {
                actor.tell(new Client.InitPublish(), null);
            } else if(entry.equalsIgnoreCase("B")) {
                System.out.print("Enter name of desired file: ");
                String filename = in.readLine();
                actor.tell(new Client.InitFindFile(filename), null);
            } else if(entry.equalsIgnoreCase("C")) {
                actorSystem.shutdown();
            } else {
                printMenu();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
