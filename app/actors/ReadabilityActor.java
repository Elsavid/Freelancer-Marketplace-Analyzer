package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Readability;
import play.libs.Json;
import services.ApiServiceInterface;
import services.ReadabilityService;

import javax.inject.Inject;

public class ReadabilityActor extends AbstractActor {

    private final ActorRef out;
    private LoggingAdapter logger = Logging.getLogger(getContext().getSystem(), this);
    private ApiServiceInterface apiService;
    private ReadabilityService readabilityService;

    public static Props props(ActorRef out, ApiServiceInterface apiService, ReadabilityService readabilityService) {
        return Props.create(ReadabilityActor.class, out, apiService, readabilityService);
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
     * Readability actor constructor
     *
     * @param out
     * @param apiService
     * @param readabilityService
     * @author Wenshu Li
     */
    @Inject
    private ReadabilityActor(ActorRef out, ApiServiceInterface apiService, ReadabilityService readabilityService) {
        this.out = out;
        this.apiService = apiService;
        this.readabilityService = readabilityService;
        logger.info("New Readability Actor for WebSocket {}", out);
    }

    /**
     * Method called when Actor receives message
     * 
     * @return Receive
     * @author Wenshu Li
     */

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, this::getProjectReadability)
                .matchAny(o -> logger.error("Received unknown message: {}", o.getClass()))
                .build();
    }

    /**
     * Method for getting project readability
     * 
     * @param request JsonNode
     * @author Wenshu Li
     */
    private void getProjectReadability(JsonNode request) {
        long projectId = request.get("projectId").asLong();
        apiService.getSingleProject(projectId)
                .thenApply(p -> readabilityService.getReadability(p.getPreviewDescription()))
                .thenAccept(this::convertAndSend);
    }

    /**
     * Method called when Actor receives message
     * 
     * @param readability Readability
     * @author Wenshu Li
     */
    private void convertAndSend(Readability readability) {
        double fleschIndex = readability.getFleschIndex();
        String educationLevel = readability.getEducationLevel();
        ObjectNode jsonResponse = Json.newObject();
        String html = "<li>Flesh Reading Ease Index: " + fleschIndex + "</li>";
        html += "<li>Education level: " + educationLevel + "</li>";
        jsonResponse.put("result", html);
        out.tell(jsonResponse, self());
    }

}
