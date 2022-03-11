package models;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordStatsProcessor {

    public static List<Project> processWordStats(List<Project> projectList) {
        //TODO Parallel stream?
        projectList.stream().forEach(WordStatsProcessor::getProjectWordStats);
        return projectList;
    }

    public static Map<String, Long> getGlobalWordStats(List<Project> projectList) {
        return projectList.stream()
                .map(Project::getWordStats)
                .reduce(new HashMap<>(), (map1, map2) -> Stream.concat(map1.entrySet().stream(), map2.entrySet().stream())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum)));
    }

    private static void getProjectWordStats(Project p) {
        Map<String, Long> stats = Arrays.stream(p.getPreviewDescription()
                .replaceAll("^[,\\s]+", "")
                .split("[,\\s]+")).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        p.setWordStats(stats);
    }
}
