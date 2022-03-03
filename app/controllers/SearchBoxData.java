package controllers;
import play.data.validation.Constraints;
public class SearchBoxData {
    @Constraints.Required
    private String terms;

    public SearchBoxData(String terms) {
        this.terms = terms;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }
}
