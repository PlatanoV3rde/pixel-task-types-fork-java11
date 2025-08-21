package dev.spaxter.pixeltasktypes.util;

import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

/**
 * Quest helper functions.
 */
public class QuestHelper {
    /**
     * Increment a numeric task progress and complete the quest if the required amount is reached.
     * This only works for tasks with an "amount" field configured.
     *
     * @param pendingTask Pending task instance
     * @return {@code true} if the quest was completed, otherwise {@code false}
     */
    public static boolean incrementNumericProgress(final TaskUtils.PendingTask pendingTask) {
        Objects.requireNonNull(pendingTask, "pendingTask must not be null");

        final Task task = Objects.requireNonNull(pendingTask.task(), "task must not be null");
        final TaskProgress progress = Objects.requireNonNull(pendingTask.taskProgress(), "taskProgress must not be null");

        final Object amountObj = Objects.requireNonNull(task.getConfigValue("amount"),
                                                        "task config 'amount' must not be null");
        final int requiredAmount = (int) amountObj;

        int current = TaskUtils.incrementIntegerTaskProgress(progress);

        if (current >= requiredAmount) {
            progress.setCompleted(true);
            return true;
        }

        return false;
    }

    /**
     * Get a config string list from a task with all strings being lowercase.
     *
     * @param task The task to get the config for
     * @param path Path to the list in the config
     * @return An all lowercase list of strings, or null if the path was not found in the config
     */
    @Nullable
    public static List<String> getConfigStringListAsLowercase(final Task task, final String path) {
        Objects.requireNonNull(task, "task must not be null");
        Objects.requireNonNull(path, "path must not be null");

        final List<String> configList = TaskUtils.getConfigStringList(task, path);

        if (configList == null) {
            return null;
        }

        final List<String> lowered = new ArrayList<>(configList.size());
        for (final String value : configList) {
            // Preserve original behavior w.r.t. nulls by explicitly checking and failing early with a clear message
            lowered.add(Objects.requireNonNull(value, "config list contains null value").toLowerCase());
        }

        return lowered;
    }
}
