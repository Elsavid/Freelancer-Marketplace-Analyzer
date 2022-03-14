package controllers;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.inject.Singleton;

import actors.SearchActor;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import models.Project;
import models.WordStatsProcessor;
import play.cache.AsyncCacheApi;
import play.libs.streams.ActorFlow;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;
import services.ApiService;
import services.ReadabilityService;

@Singleton
public class HomeController extends Controller {
    private AsyncCacheApi cache;
    private final ActorSystem actorSystem;
    private final Materializer materializer;
    @Inject
    ApiService apiService;
    @Inject
    ReadabilityService readabilityService;
    @Inject
    WSClient ws;

    @Inject
    public HomeController(
            ActorSystem actorSystem, Materializer materializer) {
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

    public WebSocket searchSocket() {
        return WebSocket.Json.accept(request -> ActorFlow.actorRef(out -> SearchActor.props(out, apiService, readabilityService),
                actorSystem, materializer));
    }

    /**
     * @param skill
     * @return
     * @author Yan Ren
     */
    public CompletionStage<Result> skill(String skill) {
        CompletionStage<List<Project>> projectList = apiService.getSkill(skill);
        return projectList.toCompletableFuture().thenApplyAsync(projects -> {
            if (!projects.isEmpty()) {
                return ok(views.html.skill.render(projects));
            }
            return ok("not found");
        });
    }

    public Result readability(Http.Request request, String input) {
        return ok(views.html.readability.render(readabilityService.getReadability(input), request));
    }

    /**
     * Renders the global words statistics page of the application (for a given query)
     *
     * @param encodedKeywords The keywords used for the query being analyzed
     * @return Play response and render of the global words statistics page
     *
     * @author Vincent Marechal
     */
    public CompletionStage<Result> globalStats(String encodedKeywords) {
        // Decode keywords
        String keywords = URLDecoder.decode(encodedKeywords, StandardCharsets.UTF_8);
        return apiService.getProjects(keywords, 250).thenApplyAsync(projects -> ok(views.html.globalwordstats.render(keywords, projects)));
    }

    /**
     * Renders the words statistics page of the application (for a given project)
     * @param id The ID of the project to analyze
     * @return Play response and render of the project words statistics page
     *
     * @author Vincent Marechal
     */
    public CompletionStage<Result> stats(long id) {
        return apiService.getSingleProject(id).thenApplyAsync(project -> ok(views.html.projectwordstats.render(project)));
    }
}
