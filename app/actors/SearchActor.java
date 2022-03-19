package actors;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

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
import services.ApiServiceInterface;
import services.ReadabilityService;

import static models.ProjectToJsonParser.convertToJson;

public class SearchActor extends AbstractActor {

    private LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef out;
    ApiServiceInterface apiService;
    ReadabilityService readabilityService;

    public static Props props(ActorRef out, ApiServiceInterface apiService, ReadabilityService readabilityService) {
        return Props.create(SearchActor.class, out, apiService, readabilityService);
    }

    @Inject
    public SearchActor(ActorRef out, ApiServiceInterface apiService, ReadabilityService readabilityService) {
        this.out = out;
        this.apiService = apiService;
        this.readabilityService = readabilityService;
        logger.info("New Search Actor for WebSocket {}", out);
    }

    /**
     * Sends an HTTP request to the API and extracts a list of Project objects out of it, then notifies the front end
     *
     * @param request The json data containing the keywords to use for the GET request
     * @author Whole group
     */
    private void onSendMessage(JsonNode request) {

        CompletionStage<List<Project>> projectsPromise = apiService.getProjects(request.get("keywords").asText(), 250);
        projectsPromise.thenApply(projectList -> {
                    // Only need to display 10 projects
                    List<Project> limitedProjectList = projectList.stream().limit(10).collect(Collectors.toList());
                    ObjectNode response = convertToJson(limitedProjectList);
                    response.put("keywords", request.get("keywords").asText());
                    if (!limitedProjectList.isEmpty()) {
                        AverageReadability averageReadability = readabilityService.getAvgReadability(limitedProjectList);
                        response.put("flesch_index", averageReadability.getFleschIndex());
                        response.put("FKGL", averageReadability.getFKGL());
                    }
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
