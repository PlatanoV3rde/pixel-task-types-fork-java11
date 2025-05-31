package dev.spaxter.pixeltasktypes.validation;

import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblem.ConfigProblemType;
import com.leonardobishop.quests.common.tasktype.TaskType;
import org.jline.utils.Levenshtein;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom validation methods for Pixelmon task types.
 */
public class PixelmonTaskConfigValidator {
    /**
     * Validates a config field containing a list of strings using an input list for
     * valid values.
     *
     * @param list  List of valid values (all lowercase).
     * @param type  TaskType instance.
     * @param paths Path(s) to config field(s).
     * @return {@code TaskType.ConfigValidator} instance.
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
                    String lower = value.toLowerCase();
                    if (!list.contains(lower)) {
                        String closestMatch = findClosestMatch(lower, list);
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
        };
    }

    /**
     * Validates a config field expected to be a boolean.
     *
     * @param type  TaskType instance.
     * @param paths Path(s) to config field(s).
     * @return {@code TaskType.ConfigValidator} instance.
     */
    public static TaskType.ConfigValidator useBooleanConfigValidator(final TaskType type,
                                                                     final String... paths) {
        return (config, problems) -> {
            for (final String path : paths) {
                if (!config.contains(path)) {
                    continue;
                }
                final Object raw = config.get(path);
                if (!(raw instanceof Boolean)) {
                    ConfigProblem problem = new ConfigProblem(
                        ConfigProblemType.ERROR,
                        "Invalid value for field '" + path + "': expected a boolean (true/false).",
                        null,
                        path
                    );
                    problems.add(problem);
                }
            }
        };
    }

    /**
     * Validates a config field expected to be a list of strings (no predefined whitelist).
     *
     * @param type  TaskType instance.
     * @param paths Path(s) to config field(s).
     * @return {@code TaskType.ConfigValidator} instance.
     */
    public static TaskType.ConfigValidator useStringListConfigValidator(final TaskType type,
                                                                         final String... paths) {
        return (config, problems) -> {
            for (final String path : paths) {
                if (!config.contains(path)) {
                    continue;
                }
                final Object raw = config.get(path);
                if (!(raw instanceof List<?>)) {
                    ConfigProblem problem = new ConfigProblem(
                        ConfigProblemType.ERROR,
                        "Invalid value for field '" + path + "': expected a list of strings.",
                        null,
                        path
                    );
                    problems.add(problem);
                    continue;
                }
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) raw;
                for (Object elem : list) {
                    if (!(elem instanceof String)) {
                        ConfigProblem problem = new ConfigProblem(
                            ConfigProblemType.ERROR,
                            "Invalid element in '" + path + "': each element must be a string.",
                            null,
                            path
                        );
                        problems.add(problem);
                        break;
                    }
                }
            }
        };
    }

    /**
     * Find the closest matching string from a list of strings using Levenshtein distance.
     *
     * @param input      Input string.
     * @param candidates Possible candidates to match against.
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
