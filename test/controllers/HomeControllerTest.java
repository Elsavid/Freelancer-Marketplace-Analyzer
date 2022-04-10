package controllers;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.WebSocket;
import play.mvc.Http.Request;
import play.mvc.Http.RequestBuilder;

import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;

import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import services.ApiServiceInterface;
import services.ApiServiceMock;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static play.inject.Bindings.bind;

import static org.junit.Assert.assertEquals;

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
     * Tests the Websocket connection for searchSocket
     *
     * @author Yan Ren
     */
    @Test
    public final void testSearchSocket() {
        RequestBuilder requestBuilder = Helpers.fakeRequest();
        WebSocket result = controller.searchSocket();

        assertEquals("play.mvc.WebSocket$1", result.getClass().getName());
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
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Tests the searchStats controller action
     *
     * @author Vincent Marechal
     */
    // @Test
    // public final void testSearchStats() {

    // // If the search gives no result, the statistics page is not accessible
    // (handled
    // // by javascript)
    // // so an empty result is not tested here
    // CompletionStage<Result> statsResult = controller.searchStats("PHP");
    // try {
    // Result result = statsResult.toCompletableFuture().get();
    // String parsedResult = contentAsString(result);

    // assertThat("Optional[text/html]", is(result.contentType().toString()));
    // // Title and table are displayed
    // assertTrue(parsedResult
    // .contains("<h1>Global words statistics for the search result using the
    // keywords \"PHP\"</h1>"));
    // assertTrue(parsedResult.contains("Number of appearances"));
    // // Statistics are displayed
    // assertTrue(parsedResult.contains("<tr><td>centers</td><td>2</td></tr>"));
    // assertTrue(parsedResult.contains("<tr><td>room</td><td>1</td></tr>"));
    // assertTrue(parsedResult.contains("<tr><td>fix</td><td>1</td></tr>"));
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    /**
     * Tests the stats controller action
     *
     * @author Vincent Marechal
     */
    // @Test
    // public final void testStats() {

    // // If the search gives no result, the statistics page is not accessible
    // (handled
    // // by javascript)
    // // so an empty result is not tested here
    // CompletionStage<Result> statsResult = controller.stats(33239791);
    // try {
    // Result result = statsResult.toCompletableFuture().get();
    // String parsedResult = contentAsString(result);

    // assertThat("Optional[text/html]", is(result.contentType().toString()));
    // // Title and table are displayed
    // assertTrue(parsedResult.contains("<h1>Words statistics for project ID
    // 33239791</h1>"));
    // assertTrue(parsedResult.contains("Number of appearances"));
    // // Statistics are displayed
    // assertTrue(parsedResult.contains("<tr><td>with</td><td>1</td></tr>"));
    // assertTrue(parsedResult.contains("<tr><td>in</td><td>1</td></tr>"));
    // assertTrue(parsedResult.contains("<tr><td>platform</td><td>1</td></tr>"));
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    /**
     * Tests the readability controller
     *
     * @author Wenshu Li
     */
    //TODO..
    //@Test

//    public final void testReadability() {
//        CompletionStage<Result> csResult = controller.readability("How are your bro? I'm fine thank you, and you?");
//        csResult.whenComplete((r, e) -> {
//            String parsedResult = Helpers.contentAsString(r);
//            assertThat("Optional[text/html]", is(r.contentType().toString()));
//            assertThat(parsedResult, containsString("Readability"));
//        }).exceptionally(e -> {
//            System.out.println(e);
//            return null;
//        });
//    }

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
}
