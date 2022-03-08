package services;

import models.AverageReadability;
import models.Project;
import models.Readability;
import org.checkerframework.checker.units.qual.A;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReadabilityService {

    public static final double BASE = 206.835;
    public static final double WORD_LENGTH_PENALTY = 84.6;
    public static final double SENTENCE_LENGTH_PENALTY = 1.015;
    public static final double COEFFICIENT_1 = 0.39;
    public static final double COEFFICIENT_2 = 11.8;
    public static final double COEFFICIENT_3 = 15.59;

    public AverageReadability getAvgReadability(List<Project> projects){
        double fleschIndex = projects.stream().mapToDouble(project -> {
            return getReadability(project.getTitle()).getFleschIndex();
        }).average().getAsDouble();
        double FKGL = projects.stream().mapToDouble(project ->{
            return getReadability(project.getTitle()).getFKGL();
        }).average().getAsDouble();
        AverageReadability averageReadability = new AverageReadability(fleschIndex,FKGL);
        return averageReadability;
    }
    public Readability getReadability(String input){
        int totalSentences = 0, totalWords = 0, totalSyllables = 0;
        totalSentences = countTotalSentences(input);
        totalWords = countTotalWords(input);
        totalSyllables = countTotalSyllables(input);
        long fleschIndex = Math.round(BASE - WORD_LENGTH_PENALTY * (totalSyllables / totalWords)- SENTENCE_LENGTH_PENALTY * (totalWords / totalSentences));
        long FKGL = Math.round(COEFFICIENT_1 * (totalWords / totalSentences) + COEFFICIENT_2 * (totalSyllables/totalWords) - COEFFICIENT_3);
        String educationLevel = getEducationLevel(fleschIndex);
        Readability readability = new Readability(fleschIndex, FKGL, educationLevel, input);
        return readability;
    }

    public int countTotalSentences(String input){
        if (input == null || input.isEmpty()){
            return 0;
        }
        Locale currentLocale = new Locale("en","US");
        int count=0;
        BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(currentLocale);
        sentenceIterator.setText(input);
        int boundary=sentenceIterator.first();
        while(boundary!=BreakIterator.DONE){
            ++count;
            boundary = sentenceIterator.next();
        }
        return (count-1);
    }
    public int countTotalWords(String input){
        if (input == null || input.isEmpty()){
            return 0;
        }
        String[] words = input.split("\\s");
        return words.length;
    }

    public int countTotalSyllables(String input){
        int count = 0;
        input = input.toLowerCase(Locale.ROOT);
        for (int i = 0; i < input.length(); i++) { // traversing till length of string
            if (input.charAt(i) == '\"' || input.charAt(i) == '\'' || input.charAt(i) == '-' || input.charAt(i) == ',' || input.charAt(i) == ')' || input.charAt(i) == '(') {
                // if at any point, we encounter any such expression, we substring the string from start till that point and further.
                input = input.substring(0,i) + input.substring(i+1, input.length());
            }
        }
        boolean isPrevVowel = false;
        for (int j = 0; j < input.length(); j++) {
            if (input.contains("a") || input.contains("e") || input.contains("i") || input.contains("o") || input.contains("u")) {
                // checking if character is a vowel and if the last letter of the word is 'e' or not
                if (isVowel(input.charAt(j)) && !((input.charAt(j) == 'e') && (j == input.length()-1))) {
                    if (isPrevVowel == false) {
                        count++;
                        isPrevVowel = true;
                    }
                } else {
                    isPrevVowel = false;
                }
            } else {
                count++;
                break;
            }
        }
        return count;
    }

    public boolean isVowel(char c){
        if(c == 'a'|| c == 'e' || c == 'o' || c == 'u'){
            return true;
        }
        else{
            return false;
        }
    }

    public String getEducationLevel(double fleschIndex){
        String educationLevel="";
        if(fleschIndex>100){
            educationLevel = "Early";
        }
        else if(91 < fleschIndex && fleschIndex <= 100){
            educationLevel = "5th grade";
        }
        else if(81 < fleschIndex && fleschIndex <= 91){
            educationLevel = "6th grade";
        }
        else if(71 < fleschIndex && fleschIndex <= 81){
            educationLevel = "7th grade";
        }
        else if(61 < fleschIndex && fleschIndex <= 71){
            educationLevel = "8th grade";
        }
        else if(51 < fleschIndex && fleschIndex <= 61){
            educationLevel = "9th grade";
        }
        else if(41 < fleschIndex && fleschIndex <= 51){
            educationLevel = "High School";
        }
        else if(31 < fleschIndex && fleschIndex <= 41){
            educationLevel = "Some College";
        }
        else if(0 < fleschIndex && fleschIndex <= 31){
            educationLevel = "College Graduate";
        }
        return educationLevel;
    }





}
