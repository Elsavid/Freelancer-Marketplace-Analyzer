package controllers;

import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.inject.Singleton;

import actors.SearchActor;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import models.Project;
import play.cache.AsyncCacheApi;
import play.libs.streams.ActorFlow;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;
import services.ApiService;
import services.ReadabilityService;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
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

    public Result index(Http.Request request) {
        return ok(views.html.index.render(request));
    }

    public WebSocket searchSocket() {
        return WebSocket.Json.accept(request -> ActorFlow.actorRef(out -> SearchActor.props(out, apiService, readabilityService),
                actorSystem, materializer));
    }

    /**
     * 
     * @author Yan Ren
     * @param skill
     * @return
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

    /**
     *
     * @author Wenshu Li
     * @param request,input
     * @return
     */
    public Result readability(Http.Request request, String input) {
        return ok(views.html.readability.render(readabilityService.getReadability(input), request));
    }
}
