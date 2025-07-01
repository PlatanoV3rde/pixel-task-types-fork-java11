package dev.spaxter.pixeltasktypes.tasks;

import com.leonardobishop.quests.common.player.QuestPlayer;
import com.leonardobishop.quests.common.quest.Quest;
import com.leonardobishop.quests.common.tasktype.AbstractTask;
import com.leonardobishop.quests.common.tasktype.TaskProgress;
import org.bukkit.entity.Player;

import java.util.Optional;

public abstract class AbstractTaskType extends AbstractTask {

    public AbstractTaskType(String id) {
        super(id);
    }

    public void increment(Player player) {
        Optional<TaskProgress> optional = getProgress(player);
        if (!optional.isPresent()) return;

        TaskProgress progress = optional.get();
        progress.increment(1);

        if (progress.getProgress() >= progress.getRequired()) {
            progress.setComplete(true);
            completeTask(player, progress);
        }
    }
}
