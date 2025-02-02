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

        if (event.getFossil() instanceof FossilItem fossil) {
            String fossilType = fossil.getFossil().name().toLowerCase();
            for (final TaskUtils.PendingTask pendingTask :
                 TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {
                final Task task = pendingTask.task();
                final List<String> requiredFossilTypes = QuestHelper.getConfigStringListAsLowercase(task, "fossils");

                if (requiredFossilTypes != null && !requiredFossilTypes.contains(fossilType)) {
                    continue;
                }
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }
}
