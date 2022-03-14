package actors;

import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import models.*;
import services.ApiService;
import services.ReadabilityService;

public class SearchActor extends AbstractActor {

    private LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef out;
    ApiService apiService;
    ReadabilityService readabilityService;

    public static Props props(ActorRef out, ApiService apiService, ReadabilityService readabilityService) {
        return Props.create(SearchActor.class, out, apiService, readabilityService);
    }

    @Inject
    public SearchActor(ActorRef out, ApiService apiService, ReadabilityService readabilityService) {
        this.out = out;
        this.apiService = apiService;
        this.readabilityService = readabilityService;
        logger.info("New Search Actor for WebSocket {}", out);
    }

    /**
     * Sends an HTTP request to the API and extracts a list of Project objects out of it, then notifies the front end
     *
     * @param request The json data containing the keywords to use for the GET request
     *
     * @author Whole group
     */
    private void onSendMessage(JsonNode request) {

        CompletionStage<List<Project>> projectsPromise = apiService.getProjects(request.get("keywords").asText(), 10);
        projectsPromise.thenApply(projectList -> {
                    // Readability feature, conversion to Json
                    AverageReadability averageReadability = readabilityService.getAvgReadability(projectList);
                    ObjectNode response = ProjectToJsonParser.convertToJson(projectList);
                    response.put("keywords", request.get("keywords").asText());
                    response.put("flesch_index", averageReadability.getFleschIndex());
                    response.put("FKGL", averageReadability.getFKGL());
                    return response;
                })
                .thenAcceptAsync(response -> out.tell(response, self()));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, this::onSendMessage)
                .matchAny(o -> logger.error("Received unknown message: {}", o.getClass()))
                .build();
    }
}
