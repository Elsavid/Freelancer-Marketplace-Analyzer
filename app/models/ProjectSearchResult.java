package models;

import java.util.List;

public class ProjectSearchResult {

	/** A single String containing all keywords used in the search */
	private String keyWords;
	/** The list of Project objects created from the Freelancer API response (after parsing) */
	private List<Project> projects;
	
	public ProjectSearchResult(String keyWords, List<Project> projects) {
		keyWords = keyWords;
		projects = projects;
	}

	public String getKeyWords() {
		return keyWords;
	}

	public List<Project> getProjects() {
		return projects;
	}
}
