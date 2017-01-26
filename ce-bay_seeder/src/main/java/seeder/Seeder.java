package seeder;

import akka.actor.ActorSelection;
import akka.actor.Props;
import akka.actor.UntypedActor;
import at.jku.ce.bay.api.*;
import at.jku.ce.bay.utils.CEBayHelper;
import java.io.File;
import java.io.FileInputStream;

/*
* CE ÜBUNGSPROJEKT --> SEEDERACTOR
* Nadine Brandstetter & Michael-Christian Ortner
*/
public class Seeder extends UntypedActor {

    public static Props props () {
        return Props.create(Seeder.class);
    }

    //start-message
    public static class InitPublish {}

    private String address() {
        return CEBayHelper.GetRemoteActorRef(getSelf());
    }

    private String path = "test28.txt";
    private String name = "test28.txt";
    //get selection of cebayActor
    private ActorSelection cebayActor = context().actorSelection(CEBayHelper.GetRegistryActorRef());
    //declaration of file that should be provided by seeder
    private File seederFile = new File(path);
    public void onReceive(Object message) throws Throwable {
        //start message sended by App
        if(message instanceof InitPublish) {
            //sends message for publishing file on cebay
            System.out.println(path + " - " + hashedFileName() + " - " + address());
            cebayActor.tell(new Publish(path, hashedFileName(), address()), getSelf());
        //if GetFile message arrives, send file to client that sended the request
        } else if(message instanceof GetFile) {
            System.out.println("Anfrage von" + getSender().toString());
            GetFile wantedFile = (GetFile) message;
            //if filename of seeder = filename that was requested by a client
            if(wantedFile.name().equals(this.name)) {
                byte[] data = new byte[(int)seederFile.length()];
                FileInputStream in = new FileInputStream(seederFile);
                if(in.read(data) != -1) {
                    //send file in FileRetrieved message to client
                    getSender().tell(new FileRetrieved(data), getSelf());
                }
                in.close();
            //filename of seeder != filename that was requested by a client
            } else {
                //send FileNotFound message
                getSender().tell(new FileNotFound(wantedFile.name()), getSelf());
            }
        //bay wants to know if seeder is still available
        } else if (message instanceof GetStatus) {
            //therefore a StatusRetrieved is returned
            System.out.println("Statusanfrage von" + getSender().toString());
            getSender().tell(new StatusRetrieved(), getSelf());
        } else {
            //if unknown message
            getSender().tell("Sorry, message is unknown!", getSelf());
        }
    }

    private String hashedFileName() {
        String hash = null;
        try {
            hash = CEBayHelper.GetHash(seederFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hash;
    }
}
