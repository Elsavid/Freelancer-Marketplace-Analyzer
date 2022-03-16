package models;
import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import scala.Console;
import java.util.HashMap;

public class Owner {
    public HashMap<String,String> userInfnormation = new HashMap<>();
    public List<Project> projects = new LinkedList<>();
    public String Id;
    public Owner(String userInfo,String proLists) {
        String a = "\"suspended\": null";
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsInfo = mapper.readTree(a);
            System.out.println(jsInfo.isEmpty());
            System.out.println(jsInfo.getNodeType());

        }
        catch (Exception e){
            System.out.println("can not");
        }
        try{
            parseUserInfo(userInfo);
            parseProjectLists(proLists);
        }
        catch (JsonProcessingException e){
            System.out.println("can not parse into json");
        }
    }


    public void parseUserInfo(String userInfo) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsInfo = mapper.readTree(userInfo).get("result");
        Id = jsInfo.get("id").toString();
        System.out.println(jsInfo.getNodeType());
        System.out.println(jsInfo.get("username").getNodeType());
        parseRecursively(jsInfo);
    }
    public void parseRecursively(JsonNode js){
        if(js.isObject()){
            js.fields().forEachRemaining(inner-> {
                if (inner.getValue().isObject())
                {
                    parseRecursively(inner.getValue());
                }
                else if(!inner.getValue().toString().equals("null") && !inner.getValue().toString().equals("[]") && !inner.getValue().toString().equals("\"\"")){
                    userInfnormation.put(inner.getKey(),inner.getValue().toString());
                }
            });
        }
    }

    public void parseProjectLists(String proLists) throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsInfo = mapper.readTree(proLists).get("result").get("projects");
            jsInfo.forEach(p->{
                ArrayList<Skill> skillTemp = new ArrayList<>();
                JsonNode skillInfoList = p.get("jobs");
                skillInfoList.forEach(s->
                    skillTemp.add(new Skill(Integer.parseInt(s.get("id").toString()), s.get("name").toString()))
                );
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                String time = s.format(Long.parseLong(p.get("time_submitted").toString())*1000);
                Project projectTemp = new Project(0, p.get("owner_id").toString(), time, p.get("title").toString(), p.get("type").toString(), skillTemp, "");
                projects.add(projectTemp);
            });
    }
}
