package dev.spaxter.pixeltasktypes.validation;

import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblem.ConfigProblemType;
import com.leonardobishop.quests.common.tasktype.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Custom validation methods for Pixelmon tasks types.
 */
public class PixelmonTaskConfigValidator {

    /**
     * Validates a config field containing a list of strings using an input list for
     * valid values.
     *
     * @param validValues List of valid values
     * @param type TaskType instance
     * @param paths Path list to config field
     * @return {@code TaskType.ConfigValidator} validator instance
     */
    public static TaskType.ConfigValidator useStringListValidator(final List<String> validValues,
                                                                final TaskType type,
                                                                final String... paths) {
        return new TaskType.ConfigValidator() {
            @Override
            public void validateConfig(Map<String, Object> config, Set<ConfigProblem> problems) {
                for (final String path : paths) {
                    final Object configList = config.get(path);

                    if (configList == null) {
                        continue;
                    }

                    final List<String> values = new ArrayList<>();

                    if (configList instanceof List) {
                        for (Object object : (List<?>) configList) {
                            values.add(String.valueOf(object));
                        }
                    } else {
                        values.add(String.valueOf(configList));
                    }

                    for (final String value : values) {
                        if (!validValues.contains(value.toLowerCase())) {
                            String closestMatch = findClosestMatch(value, validValues);
                            ConfigProblem problem = new ConfigProblem(
                                ConfigProblemType.ERROR,
                                "Invalid value for field '" + path + "': '" + value + 
                                "'. Did you mean '" + closestMatch + "'?",
                                null,
                                path
                            );
                            problems.add(problem);
                        }
                    }
                }
            }
        };
    }

    /**
     * Find the closest matching string from a list of strings.
     *
     * @param input Input string
     * @param candidates Possible candidates to match for
     * @return The closest matching string
     */
    private static String findClosestMatch(final String input, final List<String> candidates) {
        String closestMatch = null;
        int minDistance = Integer.MAX_VALUE;

        for (final String candidate : candidates) {
            int distance = calculateLevenshteinDistance(input, candidate);
            if (distance < minDistance) {
                minDistance = distance;
                closestMatch = candidate;
            }
        }

        return closestMatch != null ? closestMatch : "";
    }

    /**
     * Calculates Levenshtein distance between two strings
     * (Replacement for org.jline.utils.Levenshtein)
     */
    private static int calculateLevenshteinDistance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        int[] costs = new int[b.length() + 1];
        
        for (int j = 0; j < costs.length; j++) {
            costs[j] = j;
        }
        
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(
                    1 + Math.min(costs[j], costs[j - 1]),
                    a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1
                );
                nw = costs[j];
                costs[j] = cj;
            }
        }
        
        return costs[b.length()];
    }
}