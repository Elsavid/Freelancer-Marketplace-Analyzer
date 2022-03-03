package models;

public class Search {

	private final String[] keyWords;
	
	public Search(String[] keyWords) {
		this.keyWords = keyWords;
	}
	
	public String[] getKeyWords() {
		return this.keyWords;
	}
}
