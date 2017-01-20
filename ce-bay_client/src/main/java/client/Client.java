package client;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.UntypedActor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import akka.stream.impl.fusing.Scan;
import at.jku.ce.bay.api.*;
import at.jku.ce.bay.app.App;
import at.jku.ce.bay.utils.CEBayHelper;

/*
* CE ÜBUNGSPROJEKT --> CLIENTACTOR
* Nadine Brandstetter & Michael-Christian Ortner
*/

public class Client extends UntypedActor {

    public static Props props () {
        return Props.create(Client.class);
    }

    public static class InitPublish {}

    public static class InitFindFile {
        private static String filename;
        public InitFindFile(String name) {
            this.filename = name;
        }
    }

    //get the reference of cebayActor
    ActorSelection cebayActor = context().actorSelection(CEBayHelper.GetRegistryActorRef());
    public void onReceive(Object message) throws Throwable {
        if(message instanceof InitPublish) {
            //get the available files
            cebayActor.tell(new GetFileNames(), getSelf());
            //the bay found files and returned the name of them
        } else if(message instanceof FilesFound) {
            FilesFound filesFound = (FilesFound) message;
            List<String> fileList = filesFound.fileNames();
            System.out.println("*****************");
            System.out.println(" Available files");
            System.out.println("*****************");
            //show the files that were found by the bay
            int index = 1;
            for (String filename : fileList) {
                System.out.println("File " + index + ":" + filename);
                index++;
            }
            //print menu to get another input
            App.printMenu();
            //clientActor wants to find a specific file
        } else if(message instanceof InitFindFile)  {
            System.out.println("Enter desired filename: " + InitFindFile.filename);
            cebayActor.tell(new FindFile(InitFindFile.filename), getSelf());
            //the bay returned the seeders that provide a specific file
        } else if(message instanceof SeederFound) {
            SeederFound seeder = (SeederFound) message;
            List<String> seederList = seeder.seeder();
            System.out.println();
            System.out.println("************************************");
            System.out.println("List of seeders with specific file! ");
            System.out.println("************************************");
            //if list is empty, print the menu again
            if(seederList.isEmpty()) {
                System.out.println("There is not a single seeder with specific file.");
                App.printMenu();
            } else {
                int index = 0;
                for (String seederActor : seederList) {
                    System.out.println("Index *" + index + "*: " + seederActor);
                    index++;
                }
                System.out.print("Enter index of desired Seeder: ");
                String input = App.in.readLine();
                System.out.println("Your choice: " + seederList.get(Integer.parseInt(input)));
                //get the selection of desired seederActor
                ActorSelection sourceSeeder = context().actorSelection(seederList.get(Integer.parseInt(input)));
                //sends the seeder with the file a getFile-Message
                sourceSeeder.tell(new GetFile(InitFindFile.filename), getSelf());
            }
            //message that signalizes that file was not found
        } else if(message instanceof FileNotFound) {
            System.out.println("Sorry! The file has not been sended!");
            App.printMenu();
            //message that signalizes that the seeder has sended the file
        } else if(message instanceof FileRetrieved) {
            //file comes in a byte array
            byte[] file = ((FileRetrieved) message).data();
            System.out.print("Enter desired filename: ");
            String desiredFilename = App.in.readLine();
            FileOutputStream out = new FileOutputStream(desiredFilename);
            out.write(file);
            out.close();
            System.out.println("Download terminated successfully!");
            System.out.println("*********************************");
            System.out.println("*********************************");
            App.printMenu();
        } else {
            System.out.println(message.toString());
            App.printMenu();
        }
    }
}