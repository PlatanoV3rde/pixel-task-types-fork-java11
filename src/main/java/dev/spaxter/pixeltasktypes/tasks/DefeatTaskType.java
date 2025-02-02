package dev.spaxter.pixeltasktypes.tasks;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Task;
import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.PixelmonKnockoutEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import dev.spaxter.pixeltasktypes.util.ArclightUtils;
import dev.spaxter.pixeltasktypes.util.QuestHelper;

import org.bukkit.entity.Player;

/**
 * Defeat Pokémon task type.
 */
public class DefeatTaskType extends PixelmonTaskType {
    public DefeatTaskType(PixelTaskTypes plugin) {
        super(plugin, "defeat_pokemon", "Defeat Pokémon in battle");

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "wild_only"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "pvp_only"));
    }

    /**
     * Runs when a Pokémon is defeated in battle.
     * Will exit early if one of the participants is not a player since we only care about PvP battles in this event.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPokemonDefeat(final PixelmonKnockoutEvent event) {
        final ServerPlayerEntity player = event.source.getPlayerOwner();
        final ServerPlayerEntity opponent = event.pokemon.getPlayerOwner();

        if (player == null || opponent == null) {
            return;
        }

        final Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        final QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());

        final Pokemon pokemon = event.pokemon.pokemon;

        for (final TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {
            Task task = pendingTask.task();
            if (TaskUtils.getConfigBoolean(task, "wild_only")) {
                continue;
            }
            if (this.checkPokemon(pokemon, task)) {
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }

    /**
     * Runs when a wild Pokémon is defeated in battle.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWildPokemonDefeat(final BeatWildPixelmonEvent event) {
        final ServerPlayerEntity player = event.player;
        final Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        final QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());

        final Pokemon pokemon = event.wpp.getFaintedPokemon().pokemon;

        for (final TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {
            Task task = pendingTask.task();
            if (TaskUtils.getConfigBoolean(task, "pvp_only")) {
                continue;
            }
            if (this.checkPokemon(pokemon, task)) {
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }
}
