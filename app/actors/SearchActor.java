package actors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.AbstractActor;
import akka.actor.AbstractActor.Receive;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import models.AverageReadability;
import models.Project;
import models.Readability;
import models.SearchBox;
import play.libs.Json;
import services.ApiService;
import services.ReadabilityService;

public class SearchActor extends AbstractActor {

    private LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef out;
    String query;
    ApiService apiService;
    ReadabilityService readabilityService;

    public static Props props(ActorRef out, ApiService apiService,ReadabilityService readabilityService) {
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
        ObjectMapper mapper = new ObjectMapper();
        SearchBox searchBox = mapper.convertValue(request, SearchBox.class);
        logger.debug("onSendMessage - received message: " + searchBox.getKeywords());

        // api service sends http request
        CompletionStage<List<Project>> projectList = apiService.getProjects(searchBox.getKeywords());
        // readability service computes the metrics

        // when list of project is received, convert to json and return
        convertToJson(projectList, searchBox.getKeywords());
    }

    private void convertToJson(CompletionStage<List<Project>> projectList, String keywords) {
        projectList.thenAcceptAsync(res -> {
                    if (!res.isEmpty()) {

                        ObjectNode response = Json.newObject();
                        response.put("keywords", keywords);

                        AverageReadability averageReadability = readabilityService.getAvgReadability(res);
                        response.put("flesch_index", averageReadability.getFleschIndex());
                        response.put("FKGL", averageReadability.getFKGL());

                        ObjectNode projects = Json.newObject();

                        for (int i = 0; i < res.size(); i++) {

                            Project projectObject = res.get(i);
                            ObjectNode projectJson = Json.newObject();

                            projectJson.put("owner_id", projectObject.getOwnerId());
                            projectJson.put("title", projectObject.getTitle());
                            projectJson.put("submitdate", projectObject.getSubmitDate());

                            ArrayNode skillArray = projectJson.putArray("skills");
                            for (String skill : projectObject.getSkills()) {
                                skillArray.add(skill);
                            }

                            projectJson.put("preview_description", projectObject.getPreviewDescription());

                            projects.set(String.valueOf(i), projectJson);
                        }

                        response.set("projects", projects);

                        out.tell(response, self());
                    }
                }
        );
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, this::onSendMessage)
                .matchAny(o -> logger.error("Received unknown message: {}", o.getClass()))
                .build();
    }
}
