package models;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility methods to compute and format words statistics on a Project object or list of Project objects
 *
 * @author Vincent Marechal
 */
public class WordStatsProcessor {

    /**
     * Computes the global words statistics of a list of Project objects
     *
     * @param projectList The list of Project objects to analyze
     * @return A Map object mapping words to their frequency
     */
    public static Map<String, Long> getGlobalWordStats(List<Project> projectList) {

        return projectList.stream()
                .map(WordStatsProcessor::processProjectWordStats)
                .map(Project::getWordStats)
                .reduce(new LinkedHashMap<>(),
                        (m1, m2) -> Stream.concat(m1.entrySet().stream(), m2.entrySet().stream())
                                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum, LinkedHashMap::new))
                );
    }

    /**
     * Computes the words statistics for a Project object
     *
     * @param p The Project object to analyze
     * @return The given Project, updated
     */
    public static Project processProjectWordStats(Project p) {
        if (p == null) {
            return null;
        }
        Map<String, Long> stats = Arrays.stream(p.getPreviewDescription()
                        .replaceAll("[^a-zA-Z0-9\\s]", " ")
                        .split("\\s+"))
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        p.setWordStats(stats.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)));
        return p;
    }

    /**
     * Format a words statistics Map into an HTML table String containing statistics data
     * (because twirl templates do not keep the Map ordering)
     *
     * @param wordStats The Map to format
     * @return A String containing HTML code
     */
    public static String mapToHtmlTable(Map<String, Long> wordStats) {
        if (wordStats == null) {
            return "";
        }
        String table = "<table class=\"wordStatsTable\"><thead><tr><th>Word</th><th>Number of appearances</th></tr></thead>";
        for (Map.Entry e : wordStats.entrySet()) {
            table += "<tr><td>" + e.getKey() + "</td><td>" + e.getValue() + "</td></tr>";
        }
        table += "</table>";
        return table;
    }
}
