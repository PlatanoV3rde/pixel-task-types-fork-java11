package dev.spaxter.pixeltasktypes.validation;

import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblem.ConfigProblemType;
import com.leonardobishop.quests.common.tasktype.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jline.utils.Levenshtein;

/**
 * Custom validation methods for Pixelmon tasks types.
 */
public class PixelmonTaskConfigValidator {
    /**
     * Validates a config field containing a list of strings using an input list for
     * valid values.
     *
     * @param list  List of valid values (may be null = validation unavailable)
     * @param type  TaskType instance
     * @param paths Path list to config field
     * @return {@code TaskType.ConfigValidator} validator instance
     */
    public static TaskType.ConfigValidator useStringListValidator(final List<String> list,
                                                                  final TaskType type,
                                                                  final String... paths) {
        // Validate top-level parameters that we require for the validator to function:
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(paths, "paths must not be null");

        return (config, problems) -> {
            // Defensive checks for lambda inputs
            Objects.requireNonNull(config, "config must not be null");
            Objects.requireNonNull(problems, "problems must not be null");

            // If the list of valid values is not available, skip validation entirely.
            if (list == null) {
                // No Pixelmon data available — do not validate these fields here.
                return;
            }

            for (final String path : paths) {
                if (path == null) {
                    // skip null paths to preserve behavior and avoid NPE
                    continue;
                }

                final Object configList = config.get(path);

                if (configList == null) {
                    continue;
                }

                final List<String> values = new ArrayList<>();

                if (configList instanceof List) {
                    List<?> objectList = (List<?>) configList;
                    for (Object object : objectList) {
                        values.add(String.valueOf(object));
                    }
                } else {
                    values.add(String.valueOf(configList));
                }

                for (final String value : values) {
                    if (value == null) {
                        String closestMatch = findClosestMatch(null, list);
                        ConfigProblem problem = new ConfigProblem(ConfigProblemType.ERROR,
                                                                  "Invalid value for field '" + path + "': 'null'. Did you mean '" + closestMatch + "'?",
                                                                  null,
                                                                  path);
                        problems.add(problem);
                        continue;
                    }

                    if (!list.contains(value.toLowerCase())) {
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

    private static String findClosestMatch(final String input, final List<String> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        String closestMatch = null;
        int minDistance = Integer.MAX_VALUE;

        for (final String candidate : candidates) {
            if (candidate == null) {
                continue;
            }
            int distance;
            if (input == null) {
                distance = Integer.MAX_VALUE - 1;
            } else {
                distance = Levenshtein.distance(input, candidate);
            }
            if (distance < minDistance) {
                minDistance = distance;
                closestMatch = candidate;
            }
        }

        return closestMatch;
    }
}
