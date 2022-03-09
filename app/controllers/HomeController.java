package controllers;

import javax.inject.Inject;
import javax.inject.Singleton;

import actors.SearchActor;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import play.cache.AsyncCacheApi;
import play.libs.streams.ActorFlow;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;
import services.ApiService;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import services.ReadabilityService;
import static play.libs.Scala.asScala;
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

    public WebSocket ws() {
        return WebSocket.Json.accept(request -> ActorFlow.actorRef(out -> SearchActor.props(out, apiService, readabilityService),
                actorSystem, materializer));
    }

    public Result readability(Http.Request request, String input){
        return ok(views.html.readability.render(readabilityService.getReadability(input), request));
    }

}
