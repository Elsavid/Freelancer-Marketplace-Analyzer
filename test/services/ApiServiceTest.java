package services;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Project;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.routing.RoutingDsl;
import play.server.Server;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.mvc.Results.ok;

public class ApiServiceTest {
    private ApiService apiService;
    private WSClient ws;
    private Server server;

    @Before
    public void setup() {
        server = Server.forRouter(
                (components) -> {
                    RoutingDsl dsl = RoutingDsl.fromComponents(components);
                    dsl.GET("/testSendRequest")
                            .routingTo(
                                    request -> {
                                        ArrayNode repos = Json.newArray();
                                        ObjectNode repo = Json.newObject();
                                        repo.put("testSendRequest", "ok");
                                        repos.add(repo);
                                        return ok(repos);
                                    });
                    dsl.GET("/getGetSkill")
                            .routingTo(
                                    request -> ok().sendResource("getSkill.json"));
                    return dsl.build();
                });
        ws = play.test.WSTestClient.newClient(server.httpPort());
        apiService = new ApiService(ws);
    }

    @After
    public void tearDown() throws IOException {
        try {
            ws.close();
        } finally {
            server.stop();
        }
    }

    @Test
    public void testSendRequest() throws Exception {
        CompletableFuture<Object> resp = apiService.sendRequest("/testSendRequest");
        assertThat(resp.get().toString(), equalTo("[{\"testSendRequest\":\"ok\"}]"));
    }

    @Test
    public void testGetSkill() throws Exception {
        apiService.skillQuery = "/getGetSkill";
        CompletableFuture<List<Project>> resp = apiService.getSkill("").toCompletableFuture();
        assertThat(resp.get().get(0).getTitle(), equalTo("Android application"));
    }
}
