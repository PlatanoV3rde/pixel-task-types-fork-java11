package dev.spaxter.pixeltasktypes.tasks;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Task;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import dev.spaxter.pixeltasktypes.util.ArclightUtils;
import dev.spaxter.pixeltasktypes.util.QuestHelper;
import dev.spaxter.pixeltasktypes.validation.PixelmonTaskConfigValidator;
import dev.spaxter.pixeltasktypes.validation.ValidationConstants;

import java.util.List;
import java.util.Objects;

import org.bukkit.entity.Player;

/**
 * Catch Pokémon task type.
 */
public class CatchTaskType extends PixelmonTaskType {
    public CatchTaskType(final PixelTaskTypes plugin) {
        /*
         * Ensure plugin is not null while preserving the same call order for the superclass.
         * Use Objects.requireNonNull inline so super(...) remains the first statement.
         */
        super(Objects.requireNonNull(plugin, "plugin must not be null"), "catch_pokemon", "Catch a set number of Pokémon");

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useStringListValidator(ValidationConstants.POKE_BALLS, this, "poke_balls"));
    }

    /**
     * Runs when a Pokémon is successfully captured.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPokemonCatch(final CaptureEvent.SuccessfulCapture event) {
        Objects.requireNonNull(event, "event must not be null");

        final ServerPlayerEntity player = event.getPlayer();
        if (player == null) {
            // Nothing we can do without the player
            return;
        }

        final Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        if (bukkitPlayer == null) {
            // Bukkit player mapping failed; can't proceed
            return;
        }

        final QPlayer questPlayer = this.plugin.getQuestsApi() == null
            ? null
            : this.plugin.getQuestsApi().getPlayerManager() == null
                ? null
                : this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());
        if (questPlayer == null) {
            // No quest player available for this UUID; nothing to process
            return;
        }

        if (event.getPokemon() == null) {
            return;
        }

        final Pokemon pokemon = event.getPokemon().getPokemon();
        if (pokemon == null) {
            return;
        }

        final List<TaskUtils.PendingTask> pendingTasks = TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this);
        if (pendingTasks == null) {
            return;
        }

        for (final TaskUtils.PendingTask pendingTask : pendingTasks) {
            if (pendingTask == null) {
                continue;
            }

            final Task task = pendingTask.task();
            if (task == null) {
                continue;
            }

            List<String> requiredPokeballs = QuestHelper.getConfigStringListAsLowercase(task, "poke_balls");

            // Safely obtain the pokéball name (defensive: skip if any part is null)
            String pokeball;
            if (event.getPokeBall() == null
                || event.getPokeBall().getBallType() == null
                || event.getPokeBall().getBallType().getName() == null) {
                // Unknown ball type — skip this pending task (preserve original logic which would have NPE otherwise)
                continue;
            } else {
                pokeball = event.getPokeBall().getBallType().getName().toLowerCase();
            }

            if (requiredPokeballs != null && !requiredPokeballs.contains(pokeball)) {
                continue;
            }

            if (this.checkPokemon(pokemon, task)) {
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }
}
