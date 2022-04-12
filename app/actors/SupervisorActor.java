package actors;

import java.util.HashSet;
import java.util.Set;

import javax.swing.LookAndFeel;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * Main Supervisor actor of the application
 *
 * @author Yan Ren
 */
public class SupervisorActor extends AbstractActor {
    private LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);
    private Set<ActorRef> userActors;

    /**
     * The class being used by other actor to
     * register with Supervisor actor
     *
     */
    static public class RegisterMsg {
    }

    /**
     * The class is used by other actor to
     * deregister from Supervisor Actor
     *
     */
    static public class DeRegister {
    }

    /**
     * The constructor for Supervisor Actor
     */
    private SupervisorActor() {
        this.userActors = new HashSet<>();
    }

    /**
     * The method is called to create Supervisor Actor
     * 
     * @return Props
     */
    static public Props getProps() {
        return Props.create(SupervisorActor.class, () -> new SupervisorActor());
    }

    @Override
    public void preStart() {
        logger.info("Server supervisor started");
    }

    /**
     * The method called when received message from other actors
     * also add the registered actor into set of actors
     * 
     * @return Receive
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RegisterMsg.class, msg -> register(sender()))
                .match(DeRegister.class, msg -> deregister(sender()))
                .build();
    }

    /**
     * Removes an Actor from the list of Actors supervised by this Actor
     * 
     * @param sender The ActorRef of the Actor to remove
     * @return True if the Actor was removed
     */
    private boolean deregister(ActorRef sender) {
        logger.info("Server supervisor actor unregister: {}", sender);
        return userActors.remove(sender);
    }

    /**
     * Adds an Actor to the list of Actors supervised by this Actor
     * 
     * @param actorRef The ActorRef of the Actor to add
     */
    private void register(ActorRef actorRef) {
        logger.info("Server supervisor actor register: {}", actorRef);
        userActors.add(actorRef);
    }
}
