package dev.spaxter.pixeltasktypes.tasks;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Task;
import com.pixelmonmod.pixelmon.api.events.EvolveEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import dev.spaxter.pixeltasktypes.util.ArclightUtils;
import dev.spaxter.pixeltasktypes.util.QuestHelper;
import dev.spaxter.pixeltasktypes.validation.PixelmonTaskConfigValidator;
import dev.spaxter.pixeltasktypes.validation.ValidationConstants;

import java.util.List;

import org.bukkit.entity.Player;

/**
 * Evolve Pokémon task type.
 */
public class EvolveTaskType extends PixelmonTaskType {
    public EvolveTaskType(PixelTaskTypes plugin) {
        super(plugin, "evolve_pokemon", "Evolve Pokémon");

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useStringListValidator(
                ValidationConstants.EVOLUTION_TYPES, this, "evolution_types"
            )
        );
    }

    /**
     * Runs when a Pokémon evolves.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEvolve(final EvolveEvent.Post event) {
        ServerPlayerEntity player = event.getPlayer();
        Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());

        // Evitar crash si no se puede obtener el jugador de Bukkit o de Quests
        if (bukkitPlayer == null || questPlayer == null) {
            return;
        }

        String evolutionType = event.getEvolution().evoType;
        Pokemon pokemon = event.getPokemon();

        // Evitar NullPointerException si pokemon es null
        if (pokemon == null) {
            PixelTaskTypes.getLogger()
                .warn("EvolveEvent.Post fired but pokemon is null for player " + player.getName().getString());
            return;
        }

        List<TaskUtils.PendingTask> pendingTasks = TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this);
        for (TaskUtils.PendingTask pendingTask : pendingTasks) {
            Task task = pendingTask.task();

            List<String> requiredEvolutionTypes =
                QuestHelper.getConfigStringListAsLowercase(task, "evolution_types");
            if (requiredEvolutionTypes != null && !requiredEvolutionTypes.contains(evolutionType)) {
                continue;
            }

            if (this.checkPokemon(pokemon, task)) {
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }
}
