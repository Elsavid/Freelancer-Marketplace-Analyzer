package actors;

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
import models.Project;
import models.SearchBox;
import play.libs.Json;
import services.ApiService;

public class SearchActor extends AbstractActor {

    private LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef out;
    String query;
    ApiService apiService;

    public static Props props(ActorRef out, ApiService apiService) {
        return Props.create(SearchActor.class, out, apiService);
    }

    @Inject
    public SearchActor(ActorRef out, ApiService apiService) {
        this.out = out;
        this.apiService = apiService;
        logger.info("New Search Actor for WebSocket {}", out);
    }

    private void onSendMessage(JsonNode request) {
        ObjectMapper mapper = new ObjectMapper();
        SearchBox searchBox = mapper.convertValue(request, SearchBox.class);
        logger.debug("onSendMessage - received message: " + searchBox.getKeywords());

        // api service sends http request
        CompletionStage<List<Project>> projectList = apiService.getProjects(searchBox.getKeywords());

        // when list of project is received, convert to json and return
        projectList.thenAcceptAsync(res -> {
            if (!res.isEmpty()) {
                res.forEach(r -> {
                    ObjectNode response = Json.newObject();
                    response.put("owner_id", r.getOwnerId());
                    response.put("title", r.getTitle());
                    response.put("submitdate", r.getSubmitDate());
                    ArrayNode skillArray = response.putArray("skills");
                    for (String skill : r.getSkills()) {
                        skillArray.add(skill);
                    }
                    out.tell(response, self());
                });
            }
        });
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, this::onSendMessage)
                .matchAny(o -> logger.error("Received unknown message: {}", o.getClass()))
                .build();
    }
}
