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
import java.util.Date;
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


    public CompletionStage<List<Project>> find(SearchBoxData data){
        String terms=data.getTerms();
        WSRequest request=ws.url("https://www.freelancer.com/api/projects/0.1/projects/active/?compact=&limit=10")
                //.addHeader("freelancer-oauth-v1", "pJnj6xksLHRoLew53Rpmcy6f191LNT")
                //.setRequestTimeout(Duration.of(1000, ChronoUnit.MILLIS))
                .addQueryParameter("query", terms);
        CompletionStage<JsonNode> jsonPromise=request.get().thenApply(r->r.getBody(WSBodyReadables.instance.json()));
        //get data from jsonPromise, then inject it to objects
        return jsonPromise.thenApply(j->{
            List<Project> projects=new ArrayList<>();
            if(j.findPath("status").textValue().equals("success")){
                // here to parse the json j and give it to projects, you can write another function to implement it


                // test case below(you can uncomment it and test it):
                // projects.add(new Project(12232,new Date(System.currentTimeMillis()),j.findPath("status").textValue(),"job",j.findPath("status").textValue()));
            }
            return projects;
        });
    }
}
