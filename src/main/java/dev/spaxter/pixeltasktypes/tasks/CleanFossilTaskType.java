package dev.spaxter.pixeltasktypes.tasks;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Task;
import com.pixelmonmod.pixelmon.api.events.FossilCleanerEvent;
import com.pixelmonmod.pixelmon.items.FossilItem;

import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import dev.spaxter.pixeltasktypes.util.ArclightUtils;
import dev.spaxter.pixeltasktypes.util.QuestHelper;

import java.util.List;

import org.bukkit.entity.Player;

/**
 * Clean fossil task type.
 */
public class CleanFossilTaskType extends PixelmonTaskType {

    public CleanFossilTaskType(PixelTaskTypes plugin) {
        super(plugin, "clean_fossils", "Clean fossils in a fossil machine");

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
    }

    /**
     * Runs when a fossil is obtained from the cleaning machine.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onFossilClean(final FossilCleanerEvent.ObtainingCleanFossil event) {
        final ServerPlayerEntity player = event.getPlayer();
        final Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        final QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());

        // ✅ Validación para evitar posibles NullPointerException
        if (bukkitPlayer == null || questPlayer == null) {
            return;
        }

        // ✅ Compatibilidad con Java 8 (sin pattern matching en instanceof)
        if (event.getFossil() instanceof FossilItem) {
            FossilItem fossil = (FossilItem) event.getFossil();
            String fossilType = fossil.getFossil().name().toLowerCase();

            List<TaskUtils.PendingTask> pendingTasks = TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this);
            for (int i = 0; i < pendingTasks.size(); i++) {
                TaskUtils.PendingTask pendingTask = pendingTasks.get(i);
                Task task = pendingTask.task();

                List<String> requiredFossilTypes = QuestHelper.getConfigStringListAsLowercase(task, "fossils");
                if (requiredFossilTypes != null && !requiredFossilTypes.contains(fossilType)) {
                    continue;
                }

                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }
}
