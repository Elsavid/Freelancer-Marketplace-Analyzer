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

public class StatsActor extends AbstractActor {

    private final ActorRef out;
    private LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);
    private ApiServiceInterface apiService;

    public static Props props(ActorRef out, ApiServiceInterface apiService) {
        return Props.create(StatsActor.class, out, apiService);
    }

    @Inject
    private StatsActor(ActorRef out, ApiServiceInterface apiService) {
        this.out = out;
        this.apiService = apiService;
        logger.info("New Stats Actor for WebSocket {}", out);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, this::onReceiveJson)
                .matchAny(o -> logger.error("Received unknown message: {}", o.getClass()))
                .build();
    }

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

    private void getProjectStats(long projectId) {
        apiService.getSingleProject(projectId)
                .thenApply(WordStatsProcessor::processProjectWordStats)
                .thenApply(Project::getWordStats)
                .thenAccept(this::convertAndSend);
    }

    private void getGlobalStats(String keywords) {
        apiService.getProjects(keywords, 250, false)
                .thenApply(WordStatsProcessor::getGlobalWordStats)
                .thenAccept(this::convertAndSend);
    }

    private void convertAndSend(Map<String, Long> wordStats) {
        String htmlTable = WordStatsProcessor.mapToHtmlTable(wordStats);
        ObjectNode jsonResponse = Json.newObject().put("result", htmlTable);
        ;
        out.tell(jsonResponse, self());
    }
}
