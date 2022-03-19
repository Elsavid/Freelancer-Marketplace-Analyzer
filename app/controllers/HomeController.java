package controllers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.inject.Singleton;

import actors.SearchActor;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import models.Project;
import play.libs.streams.ActorFlow;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;
import services.ApiServiceInterface;
import services.ReadabilityService;

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

    @Inject
    public HomeController(ActorSystem actorSystem, Materializer materializer) {
        this.actorSystem = actorSystem;
        this.materializer = materializer;
    }

    /**
     * Renders the home page of the application (with the search bar)
     *
     * @param request The received HTTP request
     * @return Play response and render of the home page
     *
     * @author Whole group
     */
    public Result index(Http.Request request) {
        return ok(views.html.index.render(request));
    }

    /**
     * Creates the websocket connections fo the keywords search request
     * 
     * @return websocket connection
     * 
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
     * 
     * @author Yan Ren
     */
    public CompletionStage<Result> skill(String skill) {
        CompletionStage<List<Project>> projectList = apiService.getSkill(skill);
        return projectList.toCompletableFuture().thenApplyAsync(projects -> {
            return ok(views.html.skill.render(projects));
        });
    }

    /**
     * Renders the readability page of application
     * 
     * @param input The pre_description field text
     * @return Render of the readability page
     *
     * @author Wenshu Li
     */
    public CompletionStage<Result> readability(String input) {
        return CompletableFuture.supplyAsync(() -> ok(views.html.readability.render(readabilityService.getReadability(input))));
    }

    /**
     * Renders the global words statistics page of the application (for a given query)
     *
     * @param encodedKeywords The keywords used for the query being analyzed
     * @return Play response and render of the global words statistics page
     *
     * @author Vincent Marechal
     */
    public CompletionStage<Result> searchStats(String encodedKeywords) {
        // Decode keywords
        String keywords = URLDecoder.decode(encodedKeywords, StandardCharsets.UTF_8);
        return apiService.getProjects(keywords, 250).thenApplyAsync(projects -> ok(views.html.globalwordstats.render(keywords, projects)));
    }

    /**
     * Renders the words statistics page of the application (for a given project)
     * 
     * @param id The ID of the project to analyze
     * @return Play response and render of the project words statistics page
     *
     * @author Vincent Marechal
     */
    public CompletionStage<Result> stats(long id) {
        return apiService.getSingleProject(id).thenApplyAsync(project -> ok(views.html.projectwordstats.render(project)));
    }

    /**
     * Renders the employer page of the application
     *
     * @param owner_id The employer id to be linked
     * @return Play response and render of the employer page
     *
     * @author Haoyue Zhang
     */
    public CompletionStage<Result> employer(String owner_id) {
        return apiService.getUserInfo(owner_id).thenApplyAsync(owner -> ok(views.html.employer.render(owner, owner.projects)));
    }
}
