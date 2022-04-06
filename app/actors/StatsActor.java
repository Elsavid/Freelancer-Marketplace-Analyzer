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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class StatsActor extends AbstractActor {

    public interface Message {
    }

    public static final class GetProjectStats implements Message {
        //final ActorRef replyTo;
        final int projectId;

        public GetProjectStats(int projectId) {
            //this.replyTo = replyTo;
            this.projectId = projectId;
        }
    }

    public static final class RespondProjectStats implements Message {
        final int projectId;
        final CompletableFuture<Map<String, Long>> wordStats;

        public RespondProjectStats(int projectId, CompletableFuture<Map<String, Long>> wordStats) {
            this.projectId = projectId;
            this.wordStats = wordStats;
        }
    }

    public static final class GetGlobalStats implements Message {
        //final ActorRef replyTo;
        final String keywords;

        public GetGlobalStats(String keywords) {
            this.keywords = keywords;
        }
    }

    public static final class RespondGlobalStats implements Message {
        final String keywords;
        final CompletableFuture<Map<String, Long>> wordStats;

        public RespondGlobalStats(String keywords, CompletableFuture<Map<String, Long>> wordStats) {
            this.keywords = keywords;
            this.wordStats = wordStats;
        }
    }


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
                .match(GetProjectStats.class, this::onGetProjectStats)
                .match(GetGlobalStats.class, this::onGetGlobalStats)
                .matchAny(o -> logger.error("Received unknown message: {}", o.getClass()))
                .build();
    }


    private void onReceiveJson(JsonNode request) {
        if (request.get("global").asText().equals("true")) {
            self().tell(new GetGlobalStats(request.get("keywords").asText()), self());
        } else {
            self().tell(new GetProjectStats(request.get("projectId").asInt()), self());
        }
    }

    private void onGetProjectStats(GetProjectStats message) {
        apiService.getSingleProject(message.projectId)
                .thenApply(WordStatsProcessor::processProjectWordStats)
                .thenApply(Project::getWordStats)
                .thenAccept(this::convertAndSend);
    }

    private void onGetGlobalStats(GetGlobalStats message) {
        apiService.getProjects(message.keywords, 250)
                .thenApply(WordStatsProcessor::getGlobalWordStats)
                .thenAccept(this::convertAndSend);
    }

    private void convertAndSend(Map<String, Long> wordStats) {
        String htmlTable = WordStatsProcessor.mapToHtmlTable(wordStats);
        ObjectNode jsonResponse = Json.newObject().put("result", htmlTable);;
        out.tell(jsonResponse, self());
    }
}
