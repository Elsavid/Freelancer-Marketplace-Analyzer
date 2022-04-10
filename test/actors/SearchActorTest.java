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
import scala.Int;
import services.ApiServiceInterface;
import services.ApiServiceMock;
import services.ReadabilityService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static play.inject.Bindings.bind;

public class SearchActorTest {
    public static ActorSystem system;
    public static Materializer materializer;
    public ApiServiceInterface apiService;
    public ReadabilityService readabilityService;

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
        readabilityService = new ReadabilityService();
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
    public void testSearchActor() {

        new TestKit(system) {
            {
                final TestKit testProbe = new TestKit(system);
                ActorRef probeRef = testProbe.getRef();
                final Props props = SearchActor.props(probeRef, apiService, readabilityService);
                final ActorRef subject = system.actorOf(props);


                // Try to update when there is still no request
                subject.tell(new SearchActor.Tick(), probeRef);
                testProbe.expectNoMessage();

                ObjectNode testData = Json.newObject();
                testData.put("keywords", "testSearchActor");
                subject.tell(testData, probeRef);

                ObjectNode response = testProbe.expectMsgClass(ObjectNode.class);

                assertTrue(response.size() == 1);
                assertTrue(response.get("testSearchActor").get("projects").size() == 10);

                Set<Integer> expectedIdSet = Set.of(33206644, 33206636, 33206716, 33206633, 33206628, 33206712, 33206692, 33206709, 33206706, 33206705);
                Set<Integer> responseIdSet = new HashSet<>();
                response.get("testSearchActor").get("projects").fieldNames().forEachRemaining(id -> responseIdSet.add(Integer.parseInt(id)));
                assertTrue(expectedIdSet.containsAll(responseIdSet) && responseIdSet.containsAll(expectedIdSet));


                // Tests the duplicate request case (update), should send the same data
                subject.tell(new SearchActor.Tick(), probeRef);
                response = testProbe.expectMsgClass(ObjectNode.class);

                assertTrue(response.size() == 1);
                assertTrue(response.get("testSearchActor").get("projects").size() == 10);

                Set<Integer> secondResponseIdSet = new HashSet<>();
                response.get("testSearchActor").get("projects").fieldNames().forEachRemaining(id -> secondResponseIdSet.add(Integer.parseInt(id)));
                assertTrue(expectedIdSet.containsAll(secondResponseIdSet) && secondResponseIdSet.containsAll(expectedIdSet));
            }
        };

    }
}
