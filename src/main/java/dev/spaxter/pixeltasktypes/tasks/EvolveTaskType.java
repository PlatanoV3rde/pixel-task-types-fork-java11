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
        super.addConfigValidator(PixelmonTaskConfigValidator.useStringListValidator(
            ValidationConstants.EVOLUTION_TYPES, this, "evolution_types"));
    }

    /**
     * Runs when a Pokémon evolves.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEvolve(final EvolveEvent.Post event) {
        final ServerPlayerEntity player = event.getPlayer();
        final Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        final QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());

        String evolutionType = event.getEvolution().evoType;
        Pokemon pokemon = event.getPokemon();

        for (final TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {
            final Task task = pendingTask.task();

            List<String> requiredEvolutionTypes = QuestHelper.getConfigStringListAsLowercase(task, "evolution_types");

            if (requiredEvolutionTypes != null && !requiredEvolutionTypes.contains(evolutionType)) {
                continue;
            }

            if (this.checkPokemon(pokemon, task)) {
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }
}