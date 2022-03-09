package actors;

import java.util.List;
import java.util.Map;
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

import static java.util.stream.Collectors.toList;

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

    private void onSendMessage(JsonNode request) {

        // Send an HTTP request to the API and extract a list of Project objects out of it
        CompletionStage<List<Project>> projectsPromise = apiService.getProjects(request.get("keywords").asText(), 250);

        // Compute word statistics of each Project object and update the associated attribute
        projectsPromise.thenApply(WordStatsProcessor::processWordStats)
                .thenApply(projectList -> {
                    // Convert the first 10 projects to JSON to send to the front-end once the statistics have been computed

                    // Readability feature
                    AverageReadability averageReadability = readabilityService.getAvgReadability(projectList);
                    // Compute the global query word statistics
                    Map<String, Long> wordStats = WordStatsProcessor.getGlobalWordStats(projectList);

                    List<Project> projects = projectList.stream().limit(10).collect(toList());

                    ObjectNode response = ProjectToJsonParser.convertToJson(projects);

                    response.put("keywords", request.get("keywords").asText());
                    response.put("flesch_index", averageReadability.getFleschIndex());
                    response.put("FKGL", averageReadability.getFKGL());

                    //TODO Add global stats

                    return response;
                })
                // Finally, send the answer
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
