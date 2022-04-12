package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.Materializer;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import services.ApiServiceInterface;
import services.ApiServiceMock;

import static org.junit.Assert.assertTrue;
import static play.inject.Bindings.bind;

public class StatsActorTest {

    private static ActorSystem system;
    private static Materializer materializer;
    private ApiServiceInterface apiService;
    Application application;


    /**
     * Sets up test fixture
     *
     * @author Vincent Marechal
     */
    @Before
    public void setup() {
        system = ActorSystem.create();
        application = new GuiceApplicationBuilder()
                .overrides(bind(ApiServiceInterface.class).to(ApiServiceMock.class))
                .build();
        apiService = application.injector().instanceOf(ApiServiceInterface.class);
    }

    /**
     * Tears down test fixture
     *
     * @author Vincent Marechal
     */
    @After
    public void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    /**
     * Tests the single project words stats computation
     *
     * @author Vincent Marechal
     */
    @Test
    public void testProjectWordsStatsRequest() {

        new TestKit(system) {
            {
                final TestKit testProbe = new TestKit(system);
                ActorRef probeRef = testProbe.getRef();
                final Props statsActorProp = StatsActor.props(probeRef, apiService);
                final ActorRef subject = system.actorOf(statsActorProp);

                ObjectNode testData = Json.newObject()
                        .put("global", "false")
                        .put("projectId", 12345);
                subject.tell(testData, probeRef);

                ObjectNode responseForSingleProjectStats = testProbe.expectMsgClass(ObjectNode.class);
                String stringResult = responseForSingleProjectStats.get("result").asText();

                // Make sure the response contains the correct statistics
                assertTrue(stringResult.startsWith("<table class=\"wordStatsTable\"><thead><tr><th>Word</th><th>Number of appearances</th></tr></thead>"));
                assertTrue(stringResult.contains("<tr><td>development</td><td>1</td></tr>"));
                assertTrue(stringResult.contains("<tr><td>android</td><td>1</td></tr>"));
                assertTrue(stringResult.contains("<tr><td>java</td><td>1</td></tr>"));
            }
        };
    }


    /**
     * Tests the global projects words stats computation
     *
     * @author Vincent Marechal
     */
    @Test
    public void testGlobalWordsStatsRequest() {

        new TestKit(system) {
            {
                final TestKit testProbe = new TestKit(system);
                ActorRef probeRef = testProbe.getRef();
                final Props statsActorProp = StatsActor.props(probeRef, apiService);
                final ActorRef subject = system.actorOf(statsActorProp);

                ObjectNode testData = Json.newObject()
                        .put("global", "true")
                        .put("keywords", "whatever");
                subject.tell(testData, probeRef);

                ObjectNode responseForGlobalStats = testProbe.expectMsgClass(ObjectNode.class);
                String stringResult = responseForGlobalStats.get("result").asText();

                // Make sure the response contains the correct statistics
                assertTrue(stringResult.startsWith("<table class=\"wordStatsTable\"><thead><tr><th>Word</th><th>Number of appearances</th></tr></thead>"));
                assertTrue(stringResult.contains("<tr><td>in</td><td>2</td></tr>"));
                assertTrue(stringResult.contains("<tr><td>attractive</td><td>1</td></tr>"));
                assertTrue(stringResult.contains("<tr><td>commission</td><td>1</td></tr>"));
                assertTrue(stringResult.contains("<tr><td>letters</td><td>1</td></tr>"));
            }
        };
    }

    /**
     * Tests the case where an incorrect json response is received
     *
     * @author Vincent Marechal
     */
    @Test
    public void testIncorrectJsonRequest() {

        new TestKit(system) {
            {
                final TestKit testProbe = new TestKit(system);
                ActorRef probeRef = testProbe.getRef();
                final Props statsActorProp = StatsActor.props(probeRef, apiService);
                final ActorRef subject = system.actorOf(statsActorProp);

                ObjectNode testData = Json.newObject()
                        .put("anything", "wrong");
                subject.tell(testData, probeRef);

                testProbe.expectNoMessage();
            }
        };
    }
}
