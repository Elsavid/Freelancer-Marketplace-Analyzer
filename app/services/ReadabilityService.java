package services;

import models.AverageReadability;
import models.Project;
import models.Readability;

import java.text.BreakIterator;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Service methods for computing average readability of projects list and single project
 *
 * @author Wenshu Li
 */
public class ReadabilityService {

    public static final double BASE = 206.835;
    public static final double WORD_LENGTH_PENALTY = 84.6;
    public static final double SENTENCE_LENGTH_PENALTY = 1.015;
    public static final double COEFFICIENT_1 = 0.39;
    public static final double COEFFICIENT_2 = 11.8;
    public static final double COEFFICIENT_3 = 15.59;

    /**
     * Get the average readability of projects list
     *
     * @param projects The list of Project objects to analyze
     * @return An AverageReadability object
     */
    public AverageReadability getAvgReadability(List<Project> projects){
        double fleschIndex = projects.stream()
                                     .mapToDouble(project -> getReadability(project.getPreviewDescription()).getFleschIndex())
                                     .average().getAsDouble();
        double FKGL = projects.stream()
                              .mapToDouble(project -> getReadability(project.getPreviewDescription()).getFKGL())
                              .average().getAsDouble();
        AverageReadability averageReadability = new AverageReadability(fleschIndex,FKGL);
        return averageReadability;
    }
    /**
     * Get the readability for single project
     *
     * @param input The pre_description field text
     * @return A Readability object
     */
    public Readability getReadability(String input){
        Readability readability;
        if(input==null||input.isEmpty()||input.trim().isEmpty()){
            readability = new Readability(999, 999, "can't assess", input);
        }
        else{
            long totalSentences = 0, totalWords = 0, totalSyllables = 0;
            totalSentences = countTotalSentences(input);
            totalWords = countTotalWords(input);
            totalSyllables = countTotalSyllables(input);
            long fleschIndex = Math.round(BASE - WORD_LENGTH_PENALTY * (totalSyllables * 1.0 / totalWords)- SENTENCE_LENGTH_PENALTY * (totalWords * 1.0 / totalSentences));
            long FKGL = Math.round(COEFFICIENT_1 * (totalWords * 1.0 / totalSentences) + COEFFICIENT_2 * (totalSyllables * 1.0 / totalWords) - COEFFICIENT_3);
            String educationLevel = getEducationLevel(fleschIndex);
            readability = new Readability(fleschIndex, FKGL, educationLevel, input);
        }
        return readability;
    }
    /**
     * Get the statistics of sentences number
     *
     * @param input The pre_description field text
     * @return The number of sentences
     */
    public long countTotalSentences(String input){
        Locale currentLocale = new Locale("en","US");
        long count=0;
        BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(currentLocale);
        sentenceIterator.setText(input);
        int boundary = sentenceIterator.first();
        while(boundary != BreakIterator.DONE){
            ++count;
            boundary = sentenceIterator.next();
        }
        return (count-1);
    }

    /**
     * Get the statistics of words number
     *
     * @param input The pre_description field text
     * @return The number of words
     */
    public long countTotalWords(String input){
        long count = Arrays.stream(input.replaceAll("^[,\\s]+","").split("[,\\s]+"))
                            .count();
        return count;
    }
    /**
     * Get the statistics of syllables number
     *
     * @param input The pre_description field text
     * @return The number of syllables
     */
    public long countTotalSyllables(String input){
        long count = Arrays.stream(input.replaceAll("^[,\\s]+","").split("[,\\s]+"))
                      .mapToLong(word-> {
                            word.toLowerCase(Locale.ROOT);
                            return countSyllableInSingleWord(word);})
                      .sum();
        return count;
    }
    /**
     * Get the statistics of syllables number in a word
     *
     * @param word Single word in pre_description
     * @return The number of syllables in a single word
     */
    public long countSyllableInSingleWord(String word){
        long count=0;
        for(int i=0;i<word.length();i++){
            if((i==0 && isVowel(word.charAt(i))) || (isVowel(word.charAt(i)) && !(isVowel(word.charAt(i-1))))){
                count++;
            }
        }
        if(word.length()<=3 && count>0){
            return 1;
        }
        else{
            if(word.endsWith("es")||word.endsWith("ed")||(word.endsWith("e")&&!word.endsWith("el"))){
                count--;
            }
        }
        return count;
    }
    /**
     * Determine if the char is a syllable
     *
     * @param c Single char in a word
     * @return If the char is Vowel return ture, else return false
     */
    public boolean isVowel(char c){
        if(c == 'a'|| c == 'e' || c=='i' || c == 'o' || c == 'u'){
            return true;
        }
        else{
            return false;
        }
    }
    /**
     * Get the education level by given fleschIndex
     *
     * @param fleschIndex the fleschIndex
     * @return The education level
     */
    public String getEducationLevel(double fleschIndex){
        //String[] s = {"Early","5th grade","6th grade","7th grade","8th grade","9th grade","High School","Some College","College Graduate"};
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
        else if(fleschIndex<=0){
            educationLevel = "Law School Graduate";
        }
        return educationLevel;
    }





}
