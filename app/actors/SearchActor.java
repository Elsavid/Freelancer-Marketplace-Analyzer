package actors;

import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;
import java.util.Map.Entry;

import play.libs.Json;
import scala.Option;
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
    private Map<String, List<Project>> projectCache;

    /**
     * Tick signal used for updates
     */
    public static final class Tick {
    }

    /**
     * Method called before Actor is created and starts sending Tick message
     * every 10 seconds
     */
    @Override
    public void preStart() {
        logger.info("TimeActor {} started", self());
        getTimers().startPeriodicTimer("Timer", new Tick(), Duration.create(10, TimeUnit.SECONDS));
    }

    /**
     * Props creates the Actor and return Actor protocol
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

        // Cache Map with a size of 10 queries, removes the eldest entry if size > 10
        this.projectCache = new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Entry<String, List<Project>> eldest) {
                return size() > 10;
            }
        };

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

        String keywords = request.get("keywords").asText();

        CompletionStage<List<Project>> projectsPromise = apiService.getProjects(keywords, 250, true);
        projectsPromise.thenApply(projectList -> {
            // Only need to display 10 projects
            List<Project> filteredProjectList = limitAndFilter(keywords, projectList);

            // Add the new projects to the cache
            projectCache.merge(keywords,
                    filteredProjectList,
                    (l1, l2) -> {
                        l1.addAll(l2);
                        return l1;
                    });

            return convertCacheToJson();

        }).thenAcceptAsync(response -> out.tell(response, self()));
    }

    /**
     * Converts the Actor's cache to a json object for easy transfer to the front-end
     *
     * @return A json object version of the Actor's cache
     */
    private ObjectNode convertCacheToJson() {
        ObjectNode response = Json.newObject();
        for (Entry entry : projectCache.entrySet()) {

            List<Project> projectList = (List<Project>) entry.getValue();
            ObjectNode projectsNode = convertToJson(projectList);
            projectsNode.put("keywords", (String) entry.getKey());

            if (!projectList.isEmpty()) {
                AverageReadability averageReadability = readabilityService.getAvgReadability(projectList);
                projectsNode.put("flesch_index", averageReadability.getFleschIndex());
                projectsNode.put("FKGL", averageReadability.getFKGL());
            }
            response.set((String) entry.getKey(), projectsNode);
        }
        return response;
    }

    /**
     * Starts the update process of the current search queries
     *
     * @param t A Tick message sent every 10 seconds
     */
    private void onUpdate(Tick t) {
        // Wait for a first search to be done
        if (!projectCache.isEmpty()) {
            // Refresh the results for all requests in the cache
            for (String keywords : projectCache.keySet()) {
                ObjectNode request = Json.newObject().put("keywords", keywords);
                onSendMessage(request);
            }
        }
    }

    /**
     * Takes a list of Project objects, filters out the ones already processed
     * previously and limits the final list to 10 elements
     * 
     * @param keywords
     * @param projectList The list of projects to filter
     * @return A filtered list of Project objects
     */
    private List<Project> limitAndFilter(String keywords, List<Project> projectList) {
        return projectList.stream()
                .filter(p -> {
                    boolean isDuplicate = false;
                    if (projectCache.keySet().contains(keywords)) {
                        isDuplicate = projectCache.get(keywords).stream().map(Project::getId)
                                .anyMatch(id -> id.equals(p.getId()));
                    }
                    return !isDuplicate;
                })
                .limit(10)
                .collect(Collectors.toList());
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
