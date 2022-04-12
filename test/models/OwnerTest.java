package models;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OwnerTest {
    public HashMap<String,String> userInfnormation = new HashMap<>();
    public List<Project> projects = new LinkedList<>();
    public String Id;
    public String jsonString_userInfo;
    public String jsonString_projectLists;
    public String unCorrectJsonString;

    /**
     * Instantiates tests fixture before each test (each class attributes)
     */
    @Before
    public void InitialOwner(){
        Id = "56575218";
        unCorrectJsonString = "{make:true";
        jsonString_userInfo="{\n" +
                "  \"status\": \"success\",\n" +
                "  \"result\": \n" +
                "  {\n" +
                "    \"id\": 56575218,\n" +
                "    \"suspended\": null,\n" +
                "    \"closed\": false,\n" +
                "    \"location\": \n" +
                "    {\n" +
                "      \"country\": \n" +
                "      {\n" +
                "        \"name\": \"Brazil\",\n" +
                "        \"flag_url\": \"\"\n" +
                "      },\n" +
                "     \"empty_lists\": [],\n" +
                "     \"username\": \"VitorOliveira20\"\n" +
                "     }\n" +
                "    },\n" +
                "      \"avatar\": null\n" +
                "}";
        jsonString_projectLists="{\"status\":\"success\",\"result\":{\"projects\":[{\"id\":33220606,\"owner_id\":56575218,\"title\":\"API Gateway\",\"status\":\"active\",\"sub_status\":null,\"seo_url\":\"restful/API-Gateway\",\"currency\":{\"id\":1,\"code\":\"USD\",\"sign\":\"$\",\"name\":\"US Dollar\",\"exchange_rate\":1.0,\"country\":\"US\",\"is_external\":false,\"is_escrowcom_supported\":true},\"description\":null,\"jobs\":[{\"id\":9,\"name\":\"JavaScript\",\"category\":{\"id\":1,\"name\":\"Websites, IT & Software\"},\"active_project_count\":null,\"seo_url\":\"javascript\",\"seo_info\":null,\"local\":false},{\"id\":500,\"name\":\"Node.js\",\"category\":{\"id\":1,\"name\":\"Websites, IT & Software\"},\"active_project_count\":null,\"seo_url\":\"nodejs\",\"seo_info\":null,\"local\":false},{\"id\":697,\"name\":\"RESTful\",\"category\":{\"id\":1,\"name\":\"Websites, IT & Software\"},\"active_project_count\":null,\"seo_url\":\"restful\",\"seo_info\":null,\"local\":false}],\"submitdate\":1647436689,\"preview_description\":\"Build a RESTful API that receives a simple call and perform the list of users of the MockApi service\",\"deleted\":false,\"nonpublic\":false,\"hidebids\":false,\"type\":\"fixed\",\"bidperiod\":7,\"budget\":{\"minimum\":10.0,\"maximum\":20.0,\"name\":null,\"project_type\":null,\"currency_id\":null},\"hourly_project_info\":null,\"featured\":false,\"urgent\":false,\"assisted\":null,\"active_prepaid_milestone\":null,\"bid_stats\":{\"bid_count\":4,\"bid_avg\":27.5},\"time_submitted\":1647436689,\"time_updated\":null,\"upgrades\":{\"featured\":false,\"sealed\":false,\"nonpublic\":false,\"fulltime\":false,\"urgent\":false,\"qualified\":false,\"NDA\":false,\"assisted\":null,\"active_prepaid_milestone\":null,\"ip_contract\":false,\"success_bundle\":null,\"non_compete\":false,\"project_management\":false,\"pf_only\":false,\"recruiter\":null,\"listed\":null,\"extend\":null,\"unpaid_recruiter\":null},\"qualifications\":null,\"language\":\"pt\",\"attachments\":null,\"hireme\":false,\"hireme_initial_bid\":null,\"invited_freelancers\":null,\"recommended_freelancers\":null,\"frontend_project_status\":\"open\",\"nda_signatures\":null,\"location\":{\"country\":{\"name\":null,\"flag_url\":null,\"code\":null,\"highres_flag_url\":null,\"flag_url_cdn\":null,\"highres_flag_url_cdn\":null,\"iso3\":null,\"region_id\":null,\"phone_code\":null,\"demonym\":null,\"person\":null,\"seo_url\":null,\"sanction\":null,\"language_code\":null,\"language_id\":null},\"city\":null,\"latitude\":null,\"longitude\":null,\"vicinity\":null,\"administrative_area\":null,\"full_address\":null,\"administrative_area_code\":null,\"postal_code\":null},\"true_location\":null,\"local\":false,\"negotiated\":false,\"negotiated_bid\":null,\"time_free_bids_expire\":1647433089,\"can_post_review\":null,\"files\":null,\"user_distance\":null,\"from_user_location\":null,\"project_collaborations\":null,\"support_sessions\":null,\"track_ids\":null,\"drive_files\":null,\"nda_details\":null,\"pool_ids\":[\"freelancer\"],\"enterprise_ids\":[],\"timeframe\":null,\"deloitte_details\":null,\"is_escrow_project\":false,\"is_seller_kyc_required\":false,\"is_buyer_kyc_required\":false,\"local_details\":null,\"equipment\":null,\"nda_signatures_new\":null,\"billing_code\":null,\"enterprise_metadata_values\":null,\"project_reject_reason\":{\"description\":null,\"message\":null},\"repost_id\":null,\"client_engagement\":null,\"contract_signatures\":null,\"quotation_id\":null,\"quotation_version_id\":null,\"enterprise_linked_projects_details\":null,\"equipment_groups\":null,\"project_source\":null,\"project_source_reference\":null},{\"id\":32856714,\"owner_id\":56575218,\"title\":\"Front-End investment simulator\",\"status\":\"closed\",\"sub_status\":\"closed_awarded\",\"seo_url\":\"nodejs/Front-End-investment-simulator\",\"currency\":{\"id\":1,\"code\":\"USD\",\"sign\":\"$\",\"name\":\"US Dollar\",\"exchange_rate\":1.0,\"country\":\"US\",\"is_external\":false,\"is_escrowcom_supported\":true},\"description\":null,\"jobs\":[{\"id\":9,\"name\":\"JavaScript\",\"category\":{\"id\":1,\"name\":\"Websites, IT & Software\"},\"active_project_count\":null,\"seo_url\":\"javascript\",\"seo_info\":null,\"local\":false},{\"id\":323,\"name\":\"HTML5\",\"category\":{\"id\":1,\"name\":\"Websites, IT & Software\"},\"active_project_count\":null,\"seo_url\":\"html5\",\"seo_info\":null,\"local\":false},{\"id\":500,\"name\":\"Node.js\",\"category\":{\"id\":1,\"name\":\"Websites, IT & Software\"},\"active_project_count\":null,\"seo_url\":\"nodejs\",\"seo_info\":null,\"local\":false},{\"id\":1042,\"name\":\"CSS3\",\"category\":{\"id\":1,\"name\":\"Websites, IT & Software\"},\"active_project_count\":null,\"seo_url\":\"css3\",\"seo_info\":null,\"local\":false}],\"submitdate\":1644228068,\"preview_description\":\"I need to create a front for a ready-made API.  Front-End just for testing but well done.  It's a fa\",\"deleted\":false,\"nonpublic\":false,\"hidebids\":false,\"type\":\"fixed\",\"bidperiod\":7,\"budget\":{\"minimum\":10.0,\"maximum\":30.0,\"name\":null,\"project_type\":null,\"currency_id\":null},\"hourly_project_info\":null,\"featured\":false,\"urgent\":false,\"assisted\":null,\"active_prepaid_milestone\":null,\"bid_stats\":{\"bid_count\":1,\"bid_avg\":20.0},\"time_submitted\":1644228068,\"time_updated\":null,\"upgrades\":{\"featured\":false,\"sealed\":false,\"nonpublic\":false,\"fulltime\":false,\"urgent\":false,\"qualified\":false,\"NDA\":false,\"assisted\":null,\"active_prepaid_milestone\":null,\"ip_contract\":false,\"success_bundle\":null,\"non_compete\":false,\"project_management\":false,\"pf_only\":false,\"recruiter\":null,\"listed\":null,\"extend\":null,\"unpaid_recruiter\":null},\"qualifications\":null,\"language\":\"pt\",\"attachments\":null,\"hireme\":false,\"hireme_initial_bid\":null,\"invited_freelancers\":null,\"recommended_freelancers\":null,\"frontend_project_status\":\"complete\",\"nda_signatures\":null,\"location\":{\"country\":{\"name\":null,\"flag_url\":null,\"code\":null,\"highres_flag_url\":null,\"flag_url_cdn\":null,\"highres_flag_url_cdn\":null,\"iso3\":null,\"region_id\":null,\"phone_code\":null,\"demonym\":null,\"person\":null,\"seo_url\":null,\"sanction\":null,\"language_code\":null,\"language_id\":null},\"city\":null,\"latitude\":null,\"longitude\":null,\"vicinity\":null,\"administrative_area\":null,\"full_address\":null,\"administrative_area_code\":null,\"postal_code\":null},\"true_location\":null,\"local\":false,\"negotiated\":false,\"negotiated_bid\":null,\"time_free_bids_expire\":1644224468,\"can_post_review\":null,\"files\":null,\"user_distance\":null,\"from_user_location\":null,\"project_collaborations\":null,\"support_sessions\":null,\"track_ids\":null,\"drive_files\":null,\"nda_details\":null,\"pool_ids\":[\"freelancer\"],\"enterprise_ids\":[],\"timeframe\":null,\"deloitte_details\":null,\"is_escrow_project\":false,\"is_seller_kyc_required\":false,\"is_buyer_kyc_required\":false,\"local_details\":null,\"equipment\":null,\"nda_signatures_new\":null,\"billing_code\":null,\"enterprise_metadata_values\":null,\"project_reject_reason\":{\"description\":null,\"message\":null},\"repost_id\":null,\"client_engagement\":null,\"contract_signatures\":null,\"quotation_id\":null,\"quotation_version_id\":null,\"enterprise_linked_projects_details\":null,\"equipment_groups\":null,\"project_source\":null,\"project_source_reference\":null}],\"users\":null,\"selected_bids\":null,\"total_count\":null},\"request_id\":\"9761a54f7e21c8853b865d9b3556c04f\"}";
        userInfnormation.put("id","56575218");
        userInfnormation.put("closed","false");
        userInfnormation.put("name","\"Brazil\"");
        userInfnormation.put("username","\"VitorOliveira20\"");
        Skill skillTemp11 = new Skill(9,"\"JavaScript\"");
        Skill skillTemp12 = new Skill(500,"\"Node.js\"");
        Skill skillTemp13 = new Skill(697,"\"RESTful\"");
        ArrayList<Skill> skills1 = new ArrayList<>();
        skills1.add(skillTemp11);
        skills1.add(skillTemp12);
        skills1.add(skillTemp13);
        Project projectTemp1 = new Project(0,"56575218","2022-03-16","\"API Gateway\"","\"fixed\"",skills1,"");
        Skill skillTemp21 = new Skill(9,"\"JavaScript\"");
        Skill skillTemp22 = new Skill(323,"\"HTML5\"");
        Skill skillTemp23= new Skill(500,"\"Node.js\"");
        Skill skillTemp24 = new Skill(1042,"\"CSS3\"");
        ArrayList<Skill> skills2 = new ArrayList<>();
        skills2.add(skillTemp21);
        skills2.add(skillTemp22);
        skills2.add(skillTemp23);
        skills2.add(skillTemp24);
        Project projectTemp2 = new Project(0,"56575218","2022-02-07","\"Front-End investment simulator\"","\"fixed\"",skills2,"");
        projects.add(projectTemp1);
        projects.add(projectTemp2);
    }

    /**
     * Tests the parseUserInformation
     */
    @Test
    public void parseUserInformation() {
        Owner testOnwer = new Owner();
        try{
            testOnwer.parseUserInfo(jsonString_userInfo);
        }
        catch (JsonProcessingException e){
            System.out.println("can not parse into json for test");
        }
        assertTrue(userInfnormation.size()==testOnwer.userInfnormation.size());
        assertTrue(userInfnormation.equals(testOnwer.userInfnormation));
    }

    /**
     * Tests the parseProjectLists
     */
    @Test
    public void parseProjectLists() {
        Owner testOnwer = new Owner();
        try{
            testOnwer.parseProjectLists(jsonString_projectLists);
        }
        catch (JsonProcessingException e){
            System.out.println("can not parse into json for test2");
        }

        assertTrue(projects.size()==testOnwer.projects.size());
        ListIterator<Project> expected = projects.listIterator();
        ListIterator<Project> actual = testOnwer.projects.listIterator();
        while(expected.hasNext() && actual.hasNext()){
            Project expectedProject = expected.next();
            Project actualProject = actual.next();
//            assertTrue(expectedProject.getId() == actualProject.getId());
            assertTrue(expectedProject.getOwnerId().equals(actualProject.getOwnerId()));
            assertTrue(expectedProject.getSubmitDate().equals(actualProject.getSubmitDate()));
            assertTrue(expectedProject.getTitle().equals(actualProject.getTitle()));
            assertTrue(expectedProject.getType().equals(actualProject.getType()));
            assertTrue(expectedProject.getSkills().size() == actualProject.getSkills().size());
            for(int i =0;i<expectedProject.getSkills().size();i++){
                Skill expectedSkill = expectedProject.getSkills().get(i);
                Skill actualSkill = actualProject.getSkills().get(i);
                assertTrue(expectedSkill.getId()==actualSkill.getId());
                assertTrue(expectedSkill.getId()==actualSkill.getId());
            }
        }
    }

    /**
     * Tests the parseRecursively
     */
    @Test
    public void parseRecursively(){
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsInfo = null;
        try {
            jsInfo = mapper.readTree(jsonString_userInfo).get("result");
        }catch (Exception e){

        }
        Owner ownerTest = new Owner();
        ownerTest.parseRecursively(jsInfo);
        assertTrue(userInfnormation.equals(ownerTest.userInfnormation));

    }

    /**
     * Tests the Parameterized constructor of Onwer class
     */
    @Test
    public void OwnerCorrectParseTest(){
        Owner testOnwer = new Owner(jsonString_userInfo,jsonString_projectLists);
        assertTrue(projects.size()==testOnwer.projects.size());
        ListIterator<Project> expected = projects.listIterator();
        ListIterator<Project> actual = testOnwer.projects.listIterator();
        while(expected.hasNext() && actual.hasNext()){
            Project expectedProject = expected.next();
            Project actualProject = actual.next();
//            assertTrue(expectedProject.getId() == actualProject.getId());
            assertTrue(expectedProject.getOwnerId().equals(actualProject.getOwnerId()));
            assertTrue(expectedProject.getSubmitDate().equals(actualProject.getSubmitDate()));
            assertTrue(expectedProject.getTitle().equals(actualProject.getTitle()));
            assertTrue(expectedProject.getType().equals(actualProject.getType()));
            assertTrue(expectedProject.getSkills().size() == actualProject.getSkills().size());
            for(int i =0;i<expectedProject.getSkills().size();i++){
                Skill expectedSkill = expectedProject.getSkills().get(i);
                Skill actualSkill = actualProject.getSkills().get(i);
                assertTrue(expectedSkill.getId()==actualSkill.getId());
                assertTrue(expectedSkill.getId()==actualSkill.getId());
            }
        }

        assertTrue(projects.size()==testOnwer.projects.size());
        ListIterator<Project> expectedProjects = projects.listIterator();
        ListIterator<Project> actualProjects = testOnwer.projects.listIterator();
        while(expectedProjects.hasNext() && actualProjects.hasNext()) {
            Project expectedProject = expectedProjects.next();
            Project actualProject = actualProjects.next();
//            assertTrue(expectedProject.getId() == actualProject.getId());
            assertTrue(expectedProject.getOwnerId().equals(actualProject.getOwnerId()));
            assertTrue(expectedProject.getSubmitDate().equals(actualProject.getSubmitDate()));
            assertTrue(expectedProject.getTitle().equals(actualProject.getTitle()));
            assertTrue(expectedProject.getType().equals(actualProject.getType()));
            assertTrue(expectedProject.getSkills().size() == actualProject.getSkills().size());
            for (int i = 0; i < expectedProject.getSkills().size(); i++) {
                Skill expectedSkill = expectedProject.getSkills().get(i);
                Skill actualSkill = actualProject.getSkills().get(i);
                assertTrue(expectedSkill.getId() == actualSkill.getId());
                assertTrue(expectedSkill.getId() == actualSkill.getId());
            }
        }
        Owner testOwner2 = new Owner(unCorrectJsonString,jsonString_projectLists);
        assertTrue(testOwner2.isParseSuccess == false);
    }
}
