package actors;

import java.time.Duration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;

/**
 * This is the Test Class to perform Supervisor Actor testing
 * 
 * @author Yan Ren
 *
 */
public class SupervisorActorTest {

    public static ActorSystem system;

    @Before
    public void setup() {
        system = ActorSystem.create();
    }

    @Test
    public void testCommitSupervisorActor() {

        new TestKit(system) {
            {
                final Props props = SupervisorActor.getProps();
                final ActorRef subject = system.actorOf(props);
                final TestKit probe = new TestKit(system);

                subject.tell(new SupervisorActor.RegisterMsg(), getRef());
                expectNoMsg();

                subject.tell(new SupervisorActor.DeRegister(), getRef());
                expectNoMsg();
            }
        };
    }

    @After
    public void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }
}
