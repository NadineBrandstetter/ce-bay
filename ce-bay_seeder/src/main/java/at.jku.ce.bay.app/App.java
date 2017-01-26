package at.jku.ce.bay.app;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;
import seeder.Seeder;
import at.jku.ce.bay.utils.CEBayHelper;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/*
* CE ÜBUNGSPROJEKT --> APP FOR SEEDERACTOR
* Nadine Brandstetter & Michael-Christian Ortner
*/

public class App {

    public static void main(String[] args) {
        /*
        * SYSTEM AND ACTOR NEED A NEW NAME EVERY TIME THE SEEDER IS STARTED
        */
        //creation of actorsystem and actor
        ActorSystem actorSystem = ActorSystem.create(CEBayHelper.SYS_NAME,  ConfigFactory.load("application"));
        ActorRef actor = actorSystem.actorOf(Seeder.props(), CEBayHelper.SYS_NAME);
        //start message for SeederActor
        actor.tell(new Seeder.InitPublish(), ActorRef.noSender());
    }

}
