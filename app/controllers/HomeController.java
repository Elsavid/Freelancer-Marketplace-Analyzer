package controllers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.inject.Singleton;

import actors.*;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import models.Project;
import org.springframework.context.annotation.Profile;
import play.libs.streams.ActorFlow;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;
import services.ApiServiceInterface;
import services.ReadabilityService;
import actors.SupervisorActor;

@Singleton
public class HomeController extends Controller {
    private final ActorSystem actorSystem;
    private final Materializer materializer;
    @Inject
    ApiServiceInterface apiService;
    @Inject
    ReadabilityService readabilityService;
    @Inject
    WSClient ws;

    /**
     * The HomeController constructor
     *
     * @param actorSystem  The default ActorSystem (from Play)
     * @param materializer Materializer for AkkaStreams
     * @author Whole group
     */
    @Inject
    public HomeController(ActorSystem actorSystem, Materializer materializer) {
        this.actorSystem = actorSystem;
        this.materializer = materializer;
        actorSystem.actorOf(SupervisorActor.getProps(), "supervisorActor");
    }

    /**
     * Renders the home page of the application (with the search bar)
     *
     * @param request The received HTTP request
     * @return Play response and render of the home page
     * @author Whole group
     */
    public Result index(Http.Request request) {
        return ok(views.html.index.render(request));
    }

    /**
     * Creates the websocket connections for the keywords search request
     *
     * @return websocket connection
     * @author Yan Ren
     */
    public WebSocket searchSocket() {
        return WebSocket.Json.accept(request -> ActorFlow.actorRef(out -> SearchActor.props(out, apiService, readabilityService), actorSystem, materializer));
    }

    /**
     * Renders the skill page of the application
     *
     * @param skill An skill id used for request query
     * @return Render of the skill page
     * @author Yan Ren
     */
    public Result skill(String skill) {
        return ok(views.html.skill.render(skill));
    }

    /**
     * Creates the websocket connections for the skills search feature
     *
     * @return websocket connection
     * @author Yan Ren
     */
    public WebSocket skillSocket() {
        return WebSocket.Json.accept(request -> ActorFlow.actorRef(out -> SkillActor.props(out, apiService), actorSystem, materializer));
    }

    /**
     * Renders the readability page of application
     *
     * @param projectId The ID of the project to analyze
     * @return Render of the readability page
     * @author Wenshu Li
     */
    public Result readability(long projectId) {
        return ok(views.html.readability.render(projectId));
    }

    /**
     * Creates the websocket connections for the readability request
     *
     * @return WebsocketConnection
     * @author Wenshu Li
     */
    public WebSocket readabilitySocket() {
        return WebSocket.Json.accept(request -> ActorFlow.actorRef(out -> ReadabilityActor.props(out, apiService, readabilityService), actorSystem, materializer));
    }

    /**
     * Renders the global words statistics page of the application (for a given
     * query)
     *
     * @param encodedKeywords The keywords used for the query being analyzed (URI
     *                        encoded)
     * @return Play response and render of the global words statistics page
     * @author Vincent Marechal
     */
    public Result searchStats(String encodedKeywords) {
        String keywords = URLDecoder.decode(encodedKeywords, StandardCharsets.UTF_8);
        return ok(views.html.globalwordstats.render(keywords));
    }

    /**
     * Renders the words statistics page of the application (for a given project)
     *
     * @param projectId The ID of the project to analyze
     * @return Play response and render of the project words statistics page
     * @author Vincent Marechal
     */
    public Result stats(long projectId) {
        return ok(views.html.projectwordstats.render(projectId));
    }

    /**
     * Creates the websocket connections for the words statistics request
     *
     * @return websocket connection
     * @author Vincent Marechal
     */
    public WebSocket statsSocket() {
        return WebSocket.Json.accept(request -> ActorFlow.actorRef(out -> StatsActor.props(out, apiService), actorSystem, materializer));
    }

    /**
     * Creates the websocket connections for the employer profile request
     *
     * @return profileSocket connection
     * @author Haoyue Zhang
     */
    public WebSocket profileSocket() {
        return WebSocket.Json.accept(request -> ActorFlow.actorRef(out -> ProfileActor.props(out, apiService), actorSystem, materializer));

    }

    /**
     * Renders the employer page of the application
     *
     * @param ownerId The employer id to be linked
     * @return Play response and render of the employer page
     * @author Haoyue Zhang
     */
    public Result employer(String ownerId) {
        return ok(views.html.employer.render(ownerId));
    }
}
