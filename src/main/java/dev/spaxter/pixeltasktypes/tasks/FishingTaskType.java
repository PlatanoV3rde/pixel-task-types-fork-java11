package dev.spaxter.pixeltasktypes.tasks;

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

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FishingTaskType extends PixelmonTaskType {
    public FishingTaskType(PixelTaskTypes plugin) {
        super(plugin, "fish_pokemon", "Fish for Pokémon");

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useStringListValidator(ValidationConstants.ROD_TYPES, this, "rods"));
    }

    @SubscribeEvent
    public void onFishingReel(final FishingEvent.Reel event) {
        final ServerPlayerEntity player = event.player;
        final Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        final QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());

        if (event.optEntity.isEmpty()) {
            return;
        }

        if (event.optEntity.get() instanceof PixelmonEntity pokemonEntity) {
            Pokemon pokemon = pokemonEntity.getPokemon();

            for (final TaskUtils.PendingTask pendingTask :
                 TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {
                final Task task = pendingTask.task();
                final String rodType = event.getRodType().name().toLowerCase();
                final List<String> requiredRodTypes = QuestHelper.getConfigStringListAsLowercase(task, "rods");

                if (requiredRodTypes != null && requiredRodTypes.contains(rodType)) {
                    continue;
                }

                if (this.checkPokemon(pokemon, task)) {
                    QuestHelper.incrementNumericProgress(pendingTask);
                }
            }
        }
    }
}
