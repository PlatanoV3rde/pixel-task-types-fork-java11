package dev.spaxter.pixeltasktypes.validation;

import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblem.ConfigProblemType;
import com.leonardobishop.quests.common.tasktype.TaskType;

import java.util.ArrayList;
import java.util.List;

import org.jline.utils.Levenshtein;

/**
 * Custom validation methods for Pixelmon task types.
 */
public class PixelmonTaskConfigValidator {
    /**
     * Validates a config field containing a list of strings using an input list of
     * valid values.
     *
     * @param validValues List of valid values (all lowercase)
     * @param type        TaskType instance
     * @param paths       Path(s) to the config field
     * @return {@code TaskType.ConfigValidator} validator instance
     */
    public static TaskType.ConfigValidator useStringListValidator(final List<String> validValues,
                                                                  final TaskType type,
                                                                  final String... paths) {
        return new TaskType.ConfigValidator() {
            @Override
            public void validate(java.util.Map<String, Object> config, List<ConfigProblem> problems) {
                for (String path : paths) {
                    Object configValue = config.get(path);

                    if (configValue == null) {
                        continue;
                    }

                    List<String> valuesToCheck = new ArrayList<String>();

                    if (configValue instanceof List<?>) {
                        @SuppressWarnings("unchecked")
                        List<Object> objectList = (List<Object>) configValue;
                        for (Object obj : objectList) {
                            valuesToCheck.add(String.valueOf(obj));
                        }
                    } else {
                        valuesToCheck.add(String.valueOf(configValue));
                    }

                    for (String value : valuesToCheck) {
                        String lowered = value.toLowerCase();
                        if (!validValues.contains(lowered)) {
                            String closestMatch = findClosestMatch(lowered, validValues);
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
     * Find the closest matching string from a list of candidate strings.
     *
     * @param input      Input string.
     * @param candidates Possible candidates to match against (all lowercase).
     * @return The closest matching string from candidates.
     */
    private static String findClosestMatch(final String input, final List<String> candidates) {
        String closestMatch = null;
        int minDistance = Integer.MAX_VALUE;

        for (String candidate : candidates) {
            int distance = Levenshtein.distance(input, candidate);
            if (distance < minDistance) {
                minDistance = distance;
                closestMatch = candidate;
            }
        }

        return closestMatch;
    }
}
