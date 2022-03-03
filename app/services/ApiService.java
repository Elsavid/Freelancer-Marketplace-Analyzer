package services;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;
import controllers.SearchBoxData;
import models.Project;
import play.libs.concurrent.HttpExecutionContext;
import javax.inject.Inject;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import play.libs.ws.*;



public class ApiService {

    private final HttpExecutionContext ec;
    private final WSClient ws;



    @Inject
    public ApiService(HttpExecutionContext ec, WSClient ws) {
        this.ec = ec;
        this.ws = ws;
    }

//    public CompletionStage<WSResponse> find(Http.Request request, String phrase){
//        //Optional<String> searchTerm = request.queryString("search");
//        WSRequest complexRequest =
//                ws.url("https://www.freelancer.com/api/projects/0.1/projects/active/?compact=&limit=10")
//                        .addHeader("freelancer-oauth-v1", "pJnj6xksLHRoLew53Rpmcy6f191LNT")
//                        .setRequestTimeout(Duration.of(1000, ChronoUnit.MILLIS))
//                        .addQueryParameter("query", phrase);
//        CompletionStage<WSResponse> responsePromise = complexRequest.get();
//        return responsePromise;
//    }

    public List<Project> find(SearchBoxData data){
        String terms=data.getTerms();
        List<Project> projects=new ArrayList<>();
        WSRequest request=ws.url("https://www.freelancer.com/api/projects/0.1/projects/active/?compact=&limit=10")
                .addHeader("freelancer-oauth-v1", "pJnj6xksLHRoLew53Rpmcy6f191LNT")
                .setRequestTimeout(Duration.of(1000, ChronoUnit.MILLIS))
                .addQueryParameter("query", terms);
        CompletionStage<JsonNode> jsonPromise=request.get().thenApply(r->r.getBody(WSBodyReadables.instance.json()));
        //get data from jsonPromise, then inject it to objects





        return projects;
    }
}
