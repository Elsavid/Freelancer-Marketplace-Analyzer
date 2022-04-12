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
import models.Project;
import services.ApiServiceInterface;

import static models.ProjectToJsonParser.convertToJson;

/**
 * The search actor is used to display 10 results for provided query keywords
 * 
 * @author Yan Ren
 */
public class SkillActor extends AbstractActor {

    ApiServiceInterface apiService;
    private LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef out;

    /**
     * Props creates the Actor and return Actor protocol
     *
     * @param out        ActorRef of Actor
     * @param apiService ApiServiceInterface
     * @return Props
     */
    public static Props props(ActorRef out, ApiServiceInterface apiService) {
        return Props.create(SkillActor.class, out, apiService);
    }

    /**
     * Method Call before Actor is created and it registers with Supervisor Actor
     */
    @Override
    public void preStart() {
        context().actorSelection("/user/supervisorActor/")
                .tell(new SupervisorActor.RegisterMsg(), self());
    }

    /**
     * SkillActor constructor
     *
     * @param out        ActorRef of Actor
     * @param apiService ApiServiceInterface
     */
    @Inject
    public SkillActor(ActorRef out, ApiServiceInterface apiService) {
        this.out = out;
        this.apiService = apiService;
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
        CompletionStage<List<Project>> projects = apiService.getSkill(request.get("skill").asText());
        projects.thenApply(projectList -> {
            return convertToJson(projectList);
        }).thenAcceptAsync(response -> out.tell(response, self()));
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
                .matchAny(o -> logger.error("Received unknown message: {}", o.getClass()))
                .build();
    }
}
