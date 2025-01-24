package dev.spaxter.pixeltasktypes.util;

import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.questprogressfile.TaskProgress;
import com.leonardobishop.quests.common.quest.Task;

public class QuestHelper {
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

        if (configList == null) return null;

        return configList.stream().map((value) -> value.toLowerCase()).toList();
    }
}
