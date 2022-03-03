package controllers;

import models.*;
import models.SearchBox;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Http;
import play.mvc.Controller;
import play.mvc.Result;
import play.libs.concurrent.HttpExecutionContext;
import services.ApiService;
import play.data.Form;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static play.libs.Scala.asScala;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
@Singleton
public class HomeController extends Controller {
    private ApiService apiService;
    private HttpExecutionContext ec;
    private final Form<SearchBoxData> form;
    private MessagesApi messagesApi;
    private final List<Project> projects;


    @Inject
    public HomeController(FormFactory formFactory, MessagesApi messagesApi) {
        this.form = formFactory.form(SearchBoxData.class);
        this.messagesApi = messagesApi;
        this.projects = new ArrayList<>();
    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {

        //List<ProjectSearchResult> searchResultList = new List<ProjectSearchResult>();

        //return ok(views.html.index.render());
    }

    public Result freeLancelot(Http.Request request) {
        return ok(views.html.freelancelot.render(asScala(projects), form, request, messagesApi.preferred(request)));
    }

    public Result listProjects(Http.Request request) {
        return ok(views.html.freelancelot.render(asScala(projects), form, request, messagesApi.preferred(request)));
    }

    public Result search(Http.Request request) {
        final Form<SearchBoxData> boundForm = form.bindFromRequest(request);
        if (boundForm.hasErrors()) {
            return badRequest(views.html.freelancelot.render(asScala(projects), form, request, messagesApi.preferred(request)));
        } else {
            SearchBoxData data = boundForm.get();
            projects.addAll(apiService.find(data));
            return redirect(routes.HomeController.listProjects()).flashing("info", "New Search Results!");
        }
    }

//    public Result javascriptRoutes(Http.Request request) {
//        return ok(JavaScriptReverseRouter.create(
//                "jsRoutes",
//                "jQuery.ajax",
//                request.host(),
//                routes.javascript.HomeController.freeLancelot(),
//                routes.javascript.HomeController.list(),
//                routes.javascript.HomeController.index()))
//                .as(Http.MimeTypes.JAVASCRIPT);
//    }


}
