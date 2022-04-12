package actors;

import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import akka.testkit.javadsl.TestKit;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.Materializer;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import services.ApiServiceInterface;
import services.ApiServiceMock;
import services.ReadabilityService;

import static play.inject.Bindings.bind;

public class SkillActorTest {
    public static ActorSystem system;
    public static Materializer materializer;
    public ApiServiceInterface apiService;

    Application application;

    /**
     * Sets up test fixture
     *
     * @author Yan Ren
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
     * @author Yan Ren
     */
    @After
    public void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    /**
     * Tests the search actor
     * 
     * @author Yan Ren
     */
    @Test
    public void testSkillActor() {

        new TestKit(system) {
            {
                final TestKit testProbe = new TestKit(system);
                ActorRef probeRef = testProbe.getRef();
                final Props props = SkillActor.props(probeRef, apiService);
                final ActorRef subject = system.actorOf(props);

                ObjectNode testData = Json.newObject();
                testData.put("skill", "1");
                subject.tell(testData, probeRef);

                ObjectNode response = testProbe.expectMsgClass(ObjectNode.class);
                assertTrue(response.get("projects").size() > 0);
            }
        };
    }
}
