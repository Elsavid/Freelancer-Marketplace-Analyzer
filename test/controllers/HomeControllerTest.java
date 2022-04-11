package controllers;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.WebSocket;
import play.mvc.Http.Request;
import play.mvc.Http.RequestBuilder;

import static org.junit.Assert.*;
import static play.mvc.Http.Status.OK;

import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import services.ApiServiceInterface;
import services.ApiServiceMock;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.inject.Bindings.bind;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.test.Helpers.contentAsString;

public class HomeControllerTest extends WithApplication {
    private static Application testApp;
    final HomeController controller = testApp.injector().instanceOf(HomeController.class);

    /**
     * Initializes the test fixture (needed only once)
     *
     * @author Whole group
     */
    @BeforeClass
    public static void initTestApp() {
        testApp = new GuiceApplicationBuilder()
                .overrides(bind(ApiServiceInterface.class).to(ApiServiceMock.class))
                .build();

        Helpers.start(testApp);
    }

    /**
     * Tears down the test fixture
     *
     * @author Whole group
     */
    @AfterClass
    public static void stopTestApp() {
        Helpers.stop(testApp);
    }

    /**
     * Tests the index controller action
     *
     * @author Yan Ren
     */
    @Test
    public final void testIndex() {
        RequestBuilder requestBuilder = Helpers.fakeRequest();
        Request request = requestBuilder.build();
        Result resp = controller.index(request);
        try {
            assertEquals(OK, resp.status());
            String parsedResult = contentAsString(resp);
            assertThat("Optional[text/html]", is(resp.contentType().toString()));
            // Make sure that the search bar and button are displayed
            assertTrue(parsedResult.contains("placeholder=\"Enter keywords here...\" required"));
            assertTrue(
                    parsedResult.contains("<input class=\"searchButton\" type=\"submit\" value=\"Search\"></input>"));
            assertThat(parsedResult, containsString("FreeLancelot"));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Tests the Websocket connection for skillSocket
     *
     * @author Yan Ren
     */
    @Test
    public final void testSkillSocket() {
        RequestBuilder requestBuilder = Helpers.fakeRequest();
        WebSocket result = controller.skillSocket();

        assertEquals("play.mvc.WebSocket$1", result.getClass().getName());
    }

    /**
     * Tests the skill controller action
     *
     * @author Yan Ren
     */
    @Test
    public final void testSkill() {
        Result skillResult = controller.skill("1");
        try {
            assertEquals(OK, skillResult.status());
            String parsedResult = contentAsString(skillResult);

            assertThat("Optional[text/html]", is(skillResult.contentType().toString()));
            assertTrue(parsedResult.contains("<title>Skill</title>"));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Tests the searchStats controller action
     *
     * @author Vincent Marechal
     */
    @Test
    public final void testSearchStats() {

        // If the search gives no result, the statistics page is not accessible (handled
        // by javascript)
        // so an empty result is not tested here
        String parsedResult = contentAsString(controller.searchStats("PHP"));

        // Title and table are displayed
        assertTrue(parsedResult
                .contains("<h1>Global words statistics for the search result using the keywords \"PHP\"</h1>"));

        // Javascript in views isn't executed during tests so we can't test the data inside the template here
        // but this is done through the tests of the StatsActor iteself
        // assertTrue(parsedResult.contains("Number of appearances"));
        // // Statistics are displayed
        // assertTrue(parsedResult.contains("<tr><td>centers</td><td>2</td></tr>"));
        // assertTrue(parsedResult.contains("<tr><td>room</td><td>1</td></tr>"));
        // assertTrue(parsedResult.contains("<tr><td>fix</td><td>1</td></tr>"));
    }

    /**
     * Tests the stats controller action
     *
     * @author Vincent Marechal
     */
    @Test
    public final void testStats() {
        // If the search gives no result, the statistics page is not accessible (handled
        // by javascript)
        // so an empty result is not tested here
        String parsedResult = contentAsString(controller.stats(33239791));

        // Title and table are displayed
        assertTrue(parsedResult.contains("<h1>Words statistics for project ID 33239791</h1>"));

        // Javascript in views isn't executed in tests so we can't test the data inside the template here
        // but this is done through the tests of the StatsActor iteself
        // assertTrue(parsedResult.contains("Number of appearances"));
        // // Statistics are displayed
        // assertTrue(parsedResult.contains("<tr><td>with</td><td>1</td></tr>"));
        // assertTrue(parsedResult.contains("<tr><td>in</td><td>1</td></tr>"));
        // assertTrue(parsedResult.contains("<tr><td>platform</td><td>1</td></tr>"));
    }

    /**
     * Tests the readability controller
     *
     * @author Wenshu Li
     */
    @Test
    public final void testReadability() {
        String result = contentAsString(controller.readability(33239791));
        assertTrue(result.contains("<title>Readability</title>"));
    }
    /**
     * Tests the Websocket connection for readabilitySocket
     *
     * @author Wenshu Li
     */
    @Test
    public final void testReadabilitySocket(){
        WebSocket result = controller.readabilitySocket();
        assertEquals("play.mvc.WebSocket$1", result.getClass().getName());
    }


    /**
     * Tests the employer controller
     *
     * @author Haoyue Zhang
     */
    @Test
    public final void employerTest() {
        CompletionStage<Result> employerResult = controller.employer("61317541");
        employerResult.whenComplete((r, e) -> {
            String parsedResult = Helpers.contentAsString(r);
            assertThat("Optional[text/html]", is(r.contentType().toString()));
            assertTrue(parsedResult.contains("<h1>Information of employer 61317541</h1>"));
            assertTrue(parsedResult.contains("<tr><td>username</td><td>KashishD4761</td></tr>"));

        }).exceptionally(e -> {
            System.out.println(e);
            return null;
        });

    }

    /**
     * Tests the different WebSockets that are opened during the functioning
     *
     * @author Vincent Marechal
     */
    @Test
    public final void testWebSockets() {
        WebSocket statsSocket = controller.statsSocket();
        WebSocket searchSocket = controller.searchSocket();
        WebSocket readabilitySocket = controller.readabilitySocket();

        assertTrue("play.mvc.WebSocket$1".equals(statsSocket.getClass().getName()));
        assertTrue("play.mvc.WebSocket$1".equals(searchSocket.getClass().getName()));
        assertTrue("play.mvc.WebSocket$1".equals(readabilitySocket.getClass().getName()));

        assertNotEquals(statsSocket, searchSocket);
        assertNotEquals(statsSocket, readabilitySocket);
        assertNotEquals(searchSocket, readabilitySocket);
    }
}
