package actors;

import static org.junit.Assert.assertTrue;

import java.time.Duration;

import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import akka.testkit.javadsl.TestKit;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.Materializer;
import akka.stream.impl.io.InputStreamSinkStage.Data;
import play.Application;
import play.cache.AsyncCacheApi;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import services.ApiService;
import services.ApiServiceInterface;
import services.ApiServiceMock;
import services.ReadabilityService;

import static play.inject.Bindings.bind;

public class SearchActorTest {
    public static ActorSystem system;
    public static Materializer materializer;
    public ApiService apiService;
    public ReadabilityService readabilityService;

    Application application;

    @Before
    public void setup() {
        system = ActorSystem.create();
        application = new GuiceApplicationBuilder().overrides(bind(ApiServiceInterface.class).to(ApiServiceMock.class))
                .build();
        apiService = application.injector().instanceOf(ApiService.class);
        readabilityService = new ReadabilityService();
    }

    @Test
    public void testSearchActor() {

        new TestKit(system) {
            {
                final TestKit testProbe = new TestKit(system);
                ActorRef probeRef = testProbe.getRef();
                final Props props = SearchActor.props(probeRef, apiService, readabilityService);
                final ActorRef subject = system.actorOf(props);

                ObjectNode testData = Json.newObject();
                testData.put("keywords", "test");
                subject.tell(testData, probeRef);

                ObjectNode response = testProbe.expectMsgClass(ObjectNode.class);
                assertTrue(response.get("projects").size() > 0 ? true : false);
            }

        };

    }

    @After
    public void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }
}