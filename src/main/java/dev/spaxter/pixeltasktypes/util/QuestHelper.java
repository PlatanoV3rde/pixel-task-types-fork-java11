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
}
