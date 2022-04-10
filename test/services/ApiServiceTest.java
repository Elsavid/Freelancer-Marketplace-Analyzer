package services;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Project;
import models.Owner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.routing.RoutingDsl;
import play.server.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Results.ok;

/**
 * Tests the ApiService class
 */
public class ApiServiceTest {
    private ApiService apiService;
    private WSClient ws;
    private Server server;

    /**
     * Sets up test fixture
     */
    @Before
    public void setup() {
        server = Server.forRouter(
                (components) -> {
                    RoutingDsl dsl = RoutingDsl.fromComponents(components);
                    dsl.GET("/testSendRequest")
                            .routingTo(
                                    request -> {
                                        ArrayNode jsonArray = Json.newArray();
                                        ObjectNode jsonObject = Json.newObject();
                                        jsonObject.put("testSendRequest", "ok");
                                        jsonArray.add(jsonObject);
                                        return ok(jsonArray);
                                    });
                    dsl.GET("/getGetSkill")
                            .routingTo(
                                    request -> ok().sendResource("getSkill.json"));

                    dsl.GET("/testGetProjects/active?limit=0&job_details=true&query=\"\"")
                            .routingTo(request -> ok().sendResource("getProjects.json"));

                    dsl.GET("/testGetSingleProject/123")
                            .routingTo(request -> ok().sendResource("singleProject.json"));
                    dsl.GET("/testGetPersonalInformation/123")
                            .routingTo(request -> ok().sendResource("getOwner.json"));
                    dsl.GET("/testGetOwenrProjects/123&limit=10&job_details=true")
                            .routingTo(request -> ok().sendResource("getOwnerProjects.json"));

                    return dsl.build();
                });
        ws = play.test.WSTestClient.newClient(server.httpPort());
        apiService = new ApiService(ws);
    }

    /**
     * Tears down test fixture
     *
     * @throws IOException
     */
    @After
    public void tearDown() throws IOException {
        try {
            ws.close();
        } finally {
            server.stop();
        }
    }

    /**
     * Tests a http request sending, by using mock server
     *
     * @author Yan Ren
     */
    @Test
    public void testSendRequest() throws Exception {
        CompletableFuture<Object> resp = apiService.sendRequest("/testSendRequest");
        assertThat(resp.get().toString(), equalTo("[{\"testSendRequest\":\"ok\"}]"));
    }

    /**
     * Mocks a request for skill data to the API
     *
     * @author Yan Ren
     */
    @Test
    public void testGetSkill() throws Exception {
        apiService.skillQuery = "/getGetSkill";
        CompletableFuture<List<Project>> resp = apiService.getSkill("").toCompletableFuture();
        assertThat(resp.get().get(0).getTitle(), equalTo("Android application"));
    }

    /**
     * Mocks a request for multiple project data to the API
     *
     * @author Vincent Marechal
     */
    @Test
    public void testGetProjects() {
        apiService.projectQuery = "/testGetProjects";
        try {
            List<Project> projectList = apiService.getProjects("", 0, true).toCompletableFuture().get();
            // Checks that all Projects from the json file were parsed and sent back
            Set<String> expectedID = Set.of("33206644", "33206636", "33206716", "33206633", "33206628", "33206712",
                    "33206692", "33206709", "33206706", "33206705");
            assertEquals(expectedID, projectList.stream().map(Project::getId).collect(Collectors.toSet()));
        } catch (Exception e) {
        }
    }

    /**
     * Mocks a request for a single project data to the API
     *
     * @author Vincent Marechal
     */
    @Test
    public void testGetSingleProject() {
        // All IDs used in the getSingleProject method are found after a general
        // getProjects request
        // so a "wrong" ID can never be used in our situation, and thus we don't test
        // this case
        apiService.projectQuery = "/testGetSingleProject/";
        try {
            // What we get and what we expect
            Project p = apiService.getSingleProject(123).toCompletableFuture().get();
            Project expected = new Project(
                    33239791,
                    "11702924",
                    "2022-03-18",
                    "Fullstack android developer  ",
                    "hourly",
                    new ArrayList<>(),
                    "Need a fullstack developer with experience in\n Android Mobile Platform Development |  SQLLite | JAVA");
            assertTrue(p.equals(expected));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Mocks a request for employer data to the API
     *
     * @author Haoyue Zhang
     */
    @Test
    public void testgetUserInfo() throws Exception {
        apiService.userQuery = "/testGetPersonalInformation/";
        apiService.userProjectQuery = "/testGetOwenrProjects/";
        Owner employerInfo = apiService.getUserInfo("123").toCompletableFuture().get();
        assertThat(employerInfo.projects.get(0).getOwnerId(), equalTo("61317541"));
    }
}
