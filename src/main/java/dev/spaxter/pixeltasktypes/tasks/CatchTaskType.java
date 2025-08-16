package dev.spaxter.pixeltasktypes.tasks;

import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Task;

import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import dev.spaxter.pixeltasktypes.pixelmon.PixelmonIntegrationReflection;
import dev.spaxter.pixeltasktypes.util.ArclightUtils;
import dev.spaxter.pixeltasktypes.util.QuestHelper;
import dev.spaxter.pixeltasktypes.validation.PixelmonTaskConfigValidator;
import dev.spaxter.pixeltasktypes.validation.ValidationConstants;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * Catch Pokémon task type.
 * 
 * Esta versión usa PixelmonIntegrationReflection para no depender directamente de Pixelmon.
 */
public class CatchTaskType extends PixelmonTaskType {

    public CatchTaskType(PixelTaskTypes plugin) {
        super(plugin, "catch_pokemon", "Catch a set number of Pokémon");

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useStringListValidator(ValidationConstants.POKE_BALLS, this, "poke_balls"));
    }

    /**
     * Registra los listeners de este TaskType en PixelmonIntegrationReflection.
     */
    @Override
    public void registerListeners() {
        PixelmonIntegrationReflection.registerCatchListener((playerUUID, pokemonName, pokeBall) -> {
            final Player bukkitPlayer = ArclightUtils.getBukkitPlayer(playerUUID);
            final QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(playerUUID);

            // Evitar crash si no se puede obtener el jugador de Bukkit o de Quests
            if (bukkitPlayer == null || questPlayer == null) {
                return;
            }

            final List<TaskUtils.PendingTask> pendingTasks =
                TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this);

            if (pendingTasks == null || pendingTasks.isEmpty()) {
                return;
            }

            for (final TaskUtils.PendingTask pendingTask : pendingTasks) {
                final Task task = pendingTask.task();

                List<String> requiredPokeballs =
                    QuestHelper.getConfigStringListAsLowercase(task, "poke_balls");
                if (requiredPokeballs != null && !requiredPokeballs.contains(pokeBall.toLowerCase())) {
                    continue;
                }

                if (this.checkPokemon(pokemonName, task)) {
                    QuestHelper.incrementNumericProgress(pendingTask);
                }
            }
        });
    }
}
