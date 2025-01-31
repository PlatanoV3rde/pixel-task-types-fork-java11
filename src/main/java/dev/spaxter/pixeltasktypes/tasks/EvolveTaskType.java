package dev.spaxter.pixeltasktypes.tasks;

import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Task;
import com.pixelmonmod.pixelmon.api.events.EvolveEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import dev.spaxter.pixeltasktypes.util.ArclightUtils;
import dev.spaxter.pixeltasktypes.util.QuestHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EvolveTaskType extends PixelmonTaskType {

    public EvolveTaskType(PixelTaskTypes plugin) {
        super(plugin, "evolve_pokemon", "Evolve Pokémon");

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
    }

    @SubscribeEvent
    public void onEvolve(final EvolveEvent.Post event) {
        final ServerPlayerEntity player = event.getPlayer();
        final Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        final QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());
        PixelTaskTypes.logger.info(event.getEvolution().evoType);
        Pokemon pokemon = event.getPokemon();

        PixelTaskTypes.logger.info("Evolve: " + pokemon.getSpecies().getName());

        for (final TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {
            final Task task = pendingTask.task();

            if (this.checkPokemon(pokemon, task)) {
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }

}
