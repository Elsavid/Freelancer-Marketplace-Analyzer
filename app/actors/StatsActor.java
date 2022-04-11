package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import models.Project;
import models.WordStatsProcessor;
import play.libs.Json;
import services.ApiServiceInterface;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * The Actor responsible for the words statistics computation
 *
 * @author Vincent Marechal
 */
public class StatsActor extends AbstractActor {

    private final ActorRef out;
    private LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);
    private ApiServiceInterface apiService;

    /**
     * Props creates the Actor and returns Actor protocol
     *
     * @param out        ActorRef of Actor (WebSocket actor here)
     * @param apiService ApiServiceInterface for API calls
     * @return The Props for this Actor
     */
    public static Props props(ActorRef out, ApiServiceInterface apiService) {
        return Props.create(StatsActor.class, out, apiService);
    }

    /**
     * Actor constructor
     *
     * @param out        The ActorRef of the WebSocket to send data to
     * @param apiService the ApiService object for API calls
     */
    @Inject
    private StatsActor(ActorRef out, ApiServiceInterface apiService) {
        this.out = out;
        this.apiService = apiService;
        logger.info("New Stats Actor for WebSocket {}", out);
    }

    /**
     * When Actor is created, registers it with the Supervisor actor
     */
    @Override
    public void preStart() {
        context().actorSelection("/user/supervisorActor/")
                .tell(new SupervisorActor.RegisterMsg(), self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, this::onReceiveJson)
                .matchAny(o -> logger.error("Received unknown message: {}", o.getClass()))
                .build();
    }

    /**
     * Handles the reception of a request (json data)
     *
     * @param request A json object containing a statistics request
     */
    private void onReceiveJson(JsonNode request) {

        // Check that the received request is correct by checking the "global" attribute
        // in the json
        JsonNode globalStatus = Optional.ofNullable(request.get("global"))
                .orElse(Json.newObject().put("global", "absent").get("global"));
        String isGlobal = globalStatus.asText();

        if (isGlobal.equals("true")) {
            getGlobalStats(request.get("keywords").asText());
        } else if (isGlobal.equals("false")) {
            getProjectStats(request.get("projectId").asLong());
        } else {
            logger.error("Received incorrect json request");
        }
    }

    /**
     * Calls the API to fetch a single project data and compute its words statistics
     *
     * @param projectId The project ID to look for
     */
    private void getProjectStats(long projectId) {
        apiService.getSingleProject(projectId)
                .thenApply(WordStatsProcessor::processProjectWordStats)
                .thenApply(Project::getWordStats)
                .thenAccept(this::convertAndSend);
    }

    /**
     * Calls the API to fetch projects data based on keywords and computes the global words statistics
     *
     * @param keywords The keywords for the search query
     */
    private void getGlobalStats(String keywords) {
        apiService.getProjects(keywords, 250, false)
                .thenApply(WordStatsProcessor::getGlobalWordStats)
                .thenAccept(this::convertAndSend);
    }

    /**
     * Converts a words statistics Map object into a json object for easy sending to the front-end
     *
     * @param wordStats The statistics Map to convert
     */
    private void convertAndSend(Map<String, Long> wordStats) {
        String htmlTable = WordStatsProcessor.mapToHtmlTable(wordStats);
        ObjectNode jsonResponse = Json.newObject().put("result", htmlTable);
        out.tell(jsonResponse, self());
    }
}
