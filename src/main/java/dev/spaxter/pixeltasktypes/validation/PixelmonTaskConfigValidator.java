package dev.spaxter.pixeltasktypes.validation;

import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblem.ConfigProblemType;
import com.leonardobishop.quests.common.tasktype.TaskType;

import java.util.ArrayList;
import java.util.List;

import org.jline.utils.Levenshtein;

/**
 * Custom validation methods for Pixelmon tasks types.
 */
public class PixelmonTaskConfigValidator {
    /**
     * Validates a config field containing a list of strings using an input list for
     * valid values.
     *
     * @param list  List of valid values
     * @param type  TaskType instance
     * @param paths Path list to config field
     * @return {@code TaskType.ConfigValidator} validator instance
     */
    public static TaskType.ConfigValidator useStringListValidator(final List<String> list,
                                                                  final TaskType type,
                                                                  final String... paths) {
        return (config, problems) -> {
            for (final String path : paths) {
                final Object configList = config.get(path);

                if (configList == null) {
                    continue;
                }

                final List<String> values = new ArrayList<>();

                if (configList instanceof List<?> objectList) {
                    for (Object object : objectList) {
                        values.add(String.valueOf(object));
                    }
                } else {
                    values.add(String.valueOf(configList));
                }

                for (final String value : values) {
                    if (!list.contains(value)) {
                        String closestMatch = findClosestMatch(value, list);
                        ConfigProblem problem = new ConfigProblem(ConfigProblemType.ERROR,
                                                                  "Invalid value for field '" + path + "': '" + value
                                                                      + "'. Did you mean '" + closestMatch + "'?",
                                                                  null,
                                                                  path);
                        problems.add(problem);
                    }
                }
            }
        };
    }

    /**
     * Find the closest matching string from a list of strings.
     *
     * @param input      Input string.
     * @param candidates Possible candidates to match for.
     * @return The closest matching string.
     */
    private static String findClosestMatch(final String input, final List<String> candidates) {
        String closestMatch = null;
        int minDistance = Integer.MAX_VALUE;

        for (final String candidate : candidates) {
            int distance = Levenshtein.distance(input, candidate);
            if (distance < minDistance) {
                minDistance = distance;
                closestMatch = candidate;
            }
        }

        return closestMatch;
    }
}
