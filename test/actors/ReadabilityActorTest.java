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
import services.ReadabilityService;

import static org.junit.Assert.assertTrue;
import static play.inject.Bindings.bind;

public class ReadabilityActorTest {
    private static ActorSystem system;
    private static Materializer materializer;
    private ApiServiceInterface apiService;
    private ReadabilityService readabilityService;
    Application application;

    @Before
    public void setup() {
        system = ActorSystem.create();
        application = new GuiceApplicationBuilder()
                .overrides(bind(ApiServiceInterface.class).to(ApiServiceMock.class))
                .build();
        apiService = application.injector().instanceOf(ApiServiceInterface.class);
        readabilityService = application.injector().instanceOf(ReadabilityService.class);
    }

    @After
    public void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testProjectReadability(){
        new TestKit(system) {
            {
                final TestKit testProbe = new TestKit(system);
                ActorRef probeRef = testProbe.getRef();
                final Props readabilityActorProp = ReadabilityActor.props(probeRef, apiService, readabilityService);
                final ActorRef subject = system.actorOf(readabilityActorProp);

                ObjectNode testData = Json.newObject()
                        .put("projectId", 12345);
                subject.tell(testData, probeRef);

                ObjectNode responseForReadability = testProbe.expectMsgClass(ObjectNode.class);
                String stringResult = responseForReadability.get("result").asText();

                // Make sure the response contains the correct statistics
                assertTrue(stringResult.startsWith("<li>Flesh Reading Ease Index: " + 68.0 + "</li>"));
                assertTrue(stringResult.contains("<li>Education level: "+ "8th grade" + "</li>"));

            }
        };
    }

    @Test
    public void testIncorrectJsonRequest() {

        new TestKit(system) {
            {
                final TestKit testProbe = new TestKit(system);
                ActorRef probeRef = testProbe.getRef();
                final Props readabilityActorProp = ReadabilityActor.props(probeRef, apiService, readabilityService);
                final ActorRef subject = system.actorOf(readabilityActorProp);

                ObjectNode testData = Json.newObject()
                        .put("anything", "wrong");
                subject.tell(testData, probeRef);

                testProbe.expectNoMessage();
            }
        };
    }
}
