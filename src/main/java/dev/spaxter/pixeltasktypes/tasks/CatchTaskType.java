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

import org.bukkit.entity.Player;

/**
 * Catch Pokémon task type.
 */
public class CatchTaskType extends PixelmonTaskType {

    public CatchTaskType(PixelTaskTypes plugin) {
        super(plugin, "catch_pokemon", "Catch a set number of Pokémon");

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useStringListValidator(ValidationConstants.POKE_BALLS, this, "poke_balls"));
    }

    /**
     * Runs when a Pokémon is successfully captured.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPokemonCatch(final CaptureEvent.SuccessfulCapture event) {
        final ServerPlayerEntity player = event.getPlayer();
        final Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        final QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());

        // Evitar crash si no se puede obtener el jugador de Bukkit o de Quests
        if (bukkitPlayer == null || questPlayer == null) {
            return;
        }

        final List<TaskUtils.PendingTask> pendingTasks = TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this);

        // Verificación para evitar NullPointerException
        if (pendingTasks == null || pendingTasks.isEmpty()) {
            return;
        }

        Pokemon pokemon = event.getPokemon().getPokemon();
        String pokeball = event.getPokeBall().getBallType().getName().toLowerCase();

        for (final TaskUtils.PendingTask pendingTask : pendingTasks) {
            final Task task = pendingTask.task();

            List<String> requiredPokeballs = QuestHelper.getConfigStringListAsLowercase(task, "poke_balls");
            if (requiredPokeballs != null && !requiredPokeballs.contains(pokeball)) {
                continue;
            }

            if (this.checkPokemon(pokemon, task)) {
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }
}
