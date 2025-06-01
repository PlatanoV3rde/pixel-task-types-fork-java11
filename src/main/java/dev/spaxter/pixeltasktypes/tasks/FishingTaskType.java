package dev.spaxter.pixeltasktypes.tasks;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Task;
import com.pixelmonmod.pixelmon.api.events.FishingEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;

import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import dev.spaxter.pixeltasktypes.util.ArclightUtils;
import dev.spaxter.pixeltasktypes.util.QuestHelper;
import dev.spaxter.pixeltasktypes.validation.PixelmonTaskConfigValidator;
import dev.spaxter.pixeltasktypes.validation.ValidationConstants;

import java.util.List;

import org.bukkit.entity.Player;

/**
 * Pokémon fishing task type.
 */
public class FishingTaskType extends PixelmonTaskType {
    public FishingTaskType(PixelTaskTypes plugin) {
        super(plugin, "fish_pokemon", "Fish for Pokémon");

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useStringListValidator(ValidationConstants.ROD_TYPES, this, "rods"));
    }

    /**
     * Runs when a Pokémon fishing rod is reeled in.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onFishingReel(final FishingEvent.Reel event) {
        ServerPlayerEntity player = event.player;
        Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());

        // Evitar crash si no se puede obtener el jugador de Bukkit o de Quests
        if (bukkitPlayer == null || questPlayer == null) {
            return;
        }

        // Si no hay entidad pescada, salir
        if (event.optEntity == null || !event.optEntity.isPresent()) {
            return;
        }

        Object caught = event.optEntity.get();
        if (caught instanceof PixelmonEntity) {
            PixelmonEntity pokemonEntity = (PixelmonEntity) caught;
            Pokemon pokemon = pokemonEntity.getPokemon();

            List<TaskUtils.PendingTask> pendingTasks = TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this);
            for (int i = 0; i < pendingTasks.size(); i++) {
                TaskUtils.PendingTask pendingTask = pendingTasks.get(i);
                Task task = pendingTask.task();

                String rodType = event.getRodType().name().toLowerCase();
                List<String> requiredRodTypes = QuestHelper.getConfigStringListAsLowercase(task, "rods");

                if (requiredRodTypes != null && !requiredRodTypes.contains(rodType)) {
                    continue;
                }

                if (this.checkPokemon(pokemon, task)) {
                    QuestHelper.incrementNumericProgress(pendingTask);
                }
            }
        }
    }
}
