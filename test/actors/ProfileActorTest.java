package actors;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.Materializer;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import models.Project;
import models.WordStatsProcessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Profile;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import services.ApiServiceInterface;
import services.ApiServiceMock;
import services.ReadabilityService;

import static org.junit.Assert.assertTrue;
import static play.inject.Bindings.bind;
import javax.inject.Inject;
/**
 * test profile actor
 *
 * @author Haoyue Zhang
 */

public class ProfileActorTest {
    public static ActorSystem system;
    public static Materializer materializer;
    public ApiServiceInterface apiService;

    Application application;
    /**
     * Sets up test fixture
     *
     */
    @Before
    public void setup() {
        system = ActorSystem.create();
        application = new GuiceApplicationBuilder().overrides(bind(ApiServiceInterface.class).to(ApiServiceMock.class))
                .build();
        apiService = application.injector().instanceOf(ApiServiceInterface.class);
    }

    /**
     * Tears down test fixture
     *
     */
    @After
    public void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }
    /**
     * Tests the case where an incorrect json response is received
     *
     * */
    @Test
    public void testIncorrectJsonRequest() {

        new TestKit(system) {
            {
                final TestKit testProbe = new TestKit(system);
                ActorRef probeRef = testProbe.getRef();
                final Props statsActorProp = ProfileActor.props(probeRef, apiService);
                final ActorRef subject = system.actorOf(statsActorProp);

                ObjectNode testData = Json.newObject()
                        .put("wrong", "wrong");
                subject.tell(testData, probeRef);

                testProbe.expectNoMessage();
            }
        };
    }
    /**
     * Tests the profile actor
     *
     */
    @Test
    public void testProfileActor() {

        new TestKit(system) {
            {
                final TestKit testProbe = new TestKit(system);
                ActorRef probeRef = testProbe.getRef();
                final Props props = ProfileActor.props(probeRef, apiService);
                final ActorRef subject = system.actorOf(props);

                ObjectNode testData = Json.newObject();
                testData.put("ownerId", "2");
                subject.tell(testData, probeRef);
                ObjectNode response = testProbe.expectMsgClass(ObjectNode.class);
                assertTrue(response.get("projects").size()> 0);
                assertTrue(response.get("userInfo").size()>0);
            }
        };

    }

}
