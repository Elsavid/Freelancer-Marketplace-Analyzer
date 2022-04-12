package actors;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.Config;
import models.*;
import services.ApiServiceInterface;
import services.ReadabilityService;

import static models.ProjectToJsonParser.convertToJson;

/**
 * The Profile actor is used to display employer profile
 *
 * @author Haoyue Zhang
 */
public class ProfileActor extends AbstractActor {

    private LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);
    private final ActorRef out;
    ApiServiceInterface apiService;
    /**
     * Props creates the Actor and return Actor protocal
     *
     * @param out                ActorRef of Actor
     * @param apiService         ApiServiceInterface
     * @return Props
     */
    public static Props props(ActorRef out, ApiServiceInterface apiService) {
        return Props.create(ProfileActor.class, out, apiService);
    }

    /**
     * ProfileActor constructor
     *
     * @param out                ActorRef of Actor
     * @param apiService         ApiServiceInterface
     */
    @Inject
    public ProfileActor(ActorRef out, ApiServiceInterface apiService) {
        this.out = out;
        this.apiService = apiService;
        logger.info("New Profile Actor for WebSocket {}", out);
    }
    /**
     * Sends an HTTP request to the API and extracts a list of Project objects and user info out
     * of it. Convert owner to jsonNode, then notifies the front end.
     *
     * @param request The json data containing the keywords to use for the GET
     *                request
     */
    private void onSendMessage(JsonNode request) {
        if(request.has("ownerId")){
            CompletionStage<Owner> owner = apiService.getUserInfo(request.get("ownerId").asText());
            owner.thenAcceptAsync((o)->{
                ObjectNode profile = convertToJson(o.projects);
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode userInfo = mapper.valueToTree(o.userInfnormation);
                profile.set("userInfo",userInfo);
                out.tell(profile, self());
            });
        }
    }   

    /**
     * Method called when Actor receives message
     * @return Receive
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, this::onSendMessage)
                .matchAny(o -> logger.error("Received unknown message: {}", o.getClass()))
                .build();
    }

    /**
     * Method Call before Actor is created and it registers with Supervisor Actor
     */
    @Override
    public void preStart() {
        context().actorSelection("/user/supervisorActor/")
                .tell(new SupervisorActor.RegisterMsg(), self());
    }
}