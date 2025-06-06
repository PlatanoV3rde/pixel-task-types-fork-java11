package dev.spaxter.pixeltasktypes.util;

import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Task;

import java.util.List;
import java.util.stream.Collectors;
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
        final Task task = pendingTask.task();
        final TaskProgress progress = pendingTask.taskProgress();

        int requiredAmount = (int) task.getConfigValue("amount");
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
        final List<String> configList = TaskUtils.getConfigStringList(task, path);

        if (configList == null) {
            return null;
        }

        // Modificado para ser compatible con Java 8/11
        return configList.stream()
               .map(String::toLowerCase)
               .collect(Collectors.toList());
    }
}