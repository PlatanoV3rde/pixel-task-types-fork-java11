package dev.spaxter.pixeltasktypes.tasks;

import java.util.List;

import org.bukkit.entity.Player;

import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Task;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import dev.spaxter.pixeltasktypes.util.ArclightUtils;
import dev.spaxter.pixeltasktypes.util.QuestHelper;
import dev.spaxter.pixeltasktypes.validation.PixelmonTaskConfigValidator;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CatchTaskType extends PixelmonTaskType {

    public CatchTaskType(PixelTaskTypes plugin) {
        super(plugin, "catch_pokemon", "Catch a set number of Pokémon");

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(PixelmonTaskConfigValidator.usePokeBallValidator(this, "poke_balls"));
    }

    @SubscribeEvent
    public void onPokemonCatch(final CaptureEvent.SuccessfulCapture event) {
        final ServerPlayerEntity player = event.getPlayer();
        final Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        final QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());
        Pokemon pokemon = event.getPokemon().getPokemon();

        for (final TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {
            final Task task = pendingTask.task();

            List<String> requiredPokeballs = QuestHelper.getConfigStringListAsLowercase(task, "poke_balls");
            String pokeball = event.getPokeBall().getBallType().getName().toLowerCase();

            if (requiredPokeballs != null && !requiredPokeballs.contains(pokeball)) {
                continue;
            }

            if (this.checkPokemon(pokemon, task)) {
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }

}
