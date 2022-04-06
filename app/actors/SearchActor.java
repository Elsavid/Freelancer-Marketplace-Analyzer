package actors;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;
import scala.concurrent.duration.Duration;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import models.*;
import services.ApiServiceInterface;
import services.ReadabilityService;

import static models.ProjectToJsonParser.convertToJson;
import static models.ProjectToJsonParser.filterProject;

/**
 * The search actor is used to display 10 results for provided query keywords
 * 
 * @author Whole group
 */
public class SearchActor extends AbstractActorWithTimers {

    private LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef out;
    ApiServiceInterface apiService;
    ReadabilityService readabilityService;
    private JsonNode requestCache;
    private Set<Integer> seen = new HashSet<>();

    public static final class Tick {
    }

    /**
     * Method Call before Actor is created and it starts sending Tick message
     * every 10 seconds
     * 
     */
    @Override
    public void preStart() {
        logger.info("TimeActor {} started", self());
        getTimers().startPeriodicTimer("Timer", new Tick(), Duration.create(10, TimeUnit.SECONDS));
    }

    /**
     * Props creates the Actor and return Actor protocal
     *
     * @param out                ActorRef of Actor
     * @param apiService         ApiServiceInterface
     * @param readabilityService ReadabilityService
     * @return Props
     */
    public static Props props(ActorRef out, ApiServiceInterface apiService, ReadabilityService readabilityService) {
        return Props.create(SearchActor.class, out, apiService, readabilityService);
    }

    /**
     * SearchActor constructor
     *
     * @param out                ActorRef of Actor
     * @param apiService         ApiServiceInterface
     * @param readabilityService ReadabilityService
     */
    @Inject
    public SearchActor(ActorRef out, ApiServiceInterface apiService, ReadabilityService readabilityService) {
        this.out = out;
        this.apiService = apiService;
        this.readabilityService = readabilityService;
        logger.info("New Search Actor for WebSocket {}", out);
    }

    /**
     * Sends an HTTP request to the API and extracts a list of Project objects out
     * of it, then notifies the front end
     *
     * @param request The json data containing the keywords to use for the GET
     *                request
     * @author Whole group
     */
    private void onSendMessage(JsonNode request) {
        this.requestCache = request;

        CompletionStage<List<Project>> projectsPromise = apiService.getProjects(request.get("keywords").asText(), 250);
        projectsPromise.thenApply(projectList -> {
            // Only need to display 10 projects
            List<Project> limitedProjectList = projectList.stream().limit(10).collect(Collectors.toList());
            List<Project> filteredProjectList = filterProject(limitedProjectList, seen);
            ObjectNode response = convertToJson(filteredProjectList);
            response.put("keywords", request.get("keywords").asText());
            if (!filteredProjectList.isEmpty()) {
                AverageReadability averageReadability = readabilityService.getAvgReadability(filteredProjectList);
                response.put("flesch_index", averageReadability.getFleschIndex());
                response.put("FKGL", averageReadability.getFKGL());
            }
            return response;
        }).thenAcceptAsync(response -> out.tell(response, self()));
    }

    private void onUpdate(Tick t) {
        if (this.requestCache != null) {
            this.onSendMessage(this.requestCache);
        }
    }

    /**
     * Method called when Actor receives message
     * 
     * @return Receive
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, this::onSendMessage)
                .match(Tick.class, this::onUpdate)
                .matchAny(o -> logger.error("Received unknown message: {}", o.getClass()))
                .build();
    }
}
