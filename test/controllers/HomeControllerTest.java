package controllers;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.SupervisorStrategy.Restart;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.WebSocket;
import play.mvc.Http.Request;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;
import services.ApiServiceInterface;
import services.ApiServiceMock;

import java.util.concurrent.CompletionStage;

import static play.inject.Bindings.bind;

import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class HomeControllerTest extends WithApplication {
    private static Application testApp;

    @BeforeClass
    public static void initTestApp() {
        testApp = new GuiceApplicationBuilder()
                .overrides(bind(ApiServiceInterface.class).to(ApiServiceMock.class))
                .build();

        Helpers.start(testApp);
    }

    @AfterClass
    public static void stopTestApp() {
        Helpers.stop(testApp);
    }

    /**
     * Test the index controller action
     *
     * @author Yan Ren
     */
    @Test
    public final void testIndex() {
        final HomeController controller = testApp.injector().instanceOf(HomeController.class);
        RequestBuilder requestBuilder = Helpers.fakeRequest();
        Request request = requestBuilder.build();
        Result csResult = controller.index(request);
        try{
            String parsedResult = Helpers.contentAsString(csResult);
            assertThat("Optional[text/html]", is(csResult.contentType().toString()));
            assertThat(parsedResult, containsString("FreeLancelot"));
        } catch (Exception e){
            System.out.println(e);
        }
    }

    /**
     * Test the Websocket connection for searchSocket
     *
     * @author Yan Ren
     */
    @Test
    public final void testSearchSocket() {
        final HomeController controller = testApp.injector().instanceOf(HomeController.class);
        RequestBuilder requestBuilder = Helpers.fakeRequest();
        WebSocket result = controller.searchSocket();

        assertEquals("play.mvc.WebSocket$1", result.getClass().getName());
    }

    /**
     * Test the skill controller action
     *
     * @author Yan Ren
     */
    @Test
    public final void testSkill() {
        final HomeController controller = testApp.injector().instanceOf(HomeController.class);

        CompletionStage<Result> skillResult = controller.skill("1");
        try{
            Result result = skillResult.toCompletableFuture().get();
            String parsedResult = Helpers.contentAsString(result);
            assertThat("Optional[text/html]", is(result.contentType().toString()));
            assertThat(parsedResult, containsString("<h2>Skill</h2>"));
            assertThat(parsedResult, containsString("<td>25385873</td>"));
            assertThat(parsedResult, containsString("Mobile App Testing"));
        } catch (Exception e){
            System.out.println(e);
        }
    }

    @Test
    public final void testReadability(){
        final HomeController controller = testApp.injector().instanceOf(HomeController.class);
        CompletionStage<Result> csResult = controller.readability("How are your bro? I'm fine thank you, and you?");
        csResult.whenComplete((r,e)->{
            String parsedResult = Helpers.contentAsString(r);
            assertThat("Optional[text/html]", is(r.contentType().toString()));
            assertThat(parsedResult, containsString("Readability"));
        }).exceptionally(e-> {
            System.out.println(e);
            return null;
        });
    }
}
