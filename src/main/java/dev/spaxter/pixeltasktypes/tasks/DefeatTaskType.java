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

import java.util.List;

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
     * Runs when a Pokémon is defeated in a PvP battle.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPokemonDefeat(final PixelmonKnockoutEvent event) {
        ServerPlayerEntity sourcePlayer = event.source.getPlayerOwner();
        ServerPlayerEntity opponentPlayer = event.pokemon.getPlayerOwner();

        // Salir si alguno no es un jugador válido
        if (sourcePlayer == null || opponentPlayer == null) {
            return;
        }

        Player bukkitPlayer = ArclightUtils.getBukkitPlayer(sourcePlayer.getUUID());
        QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(sourcePlayer.getUUID());

        // Evitar crash si no se puede obtener el jugador de Bukkit o de Quests
        if (bukkitPlayer == null || questPlayer == null) {
            return;
        }

        Pokemon pokemon = event.pokemon.pokemon;

        List<TaskUtils.PendingTask> pendingTasks = TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this);
        for (int i = 0; i < pendingTasks.size(); i++) {
            TaskUtils.PendingTask pendingTask = pendingTasks.get(i);
            Task task = pendingTask.task();

            // Si la tarea está configurada como "wild_only", saltarla en PvP
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
        ServerPlayerEntity player = event.player;

        Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());

        // Evitar crash si no se puede obtener el jugador de Bukkit o de Quests
        if (bukkitPlayer == null || questPlayer == null) {
            return;
        }

        Pokemon pokemon = event.wpp.getFaintedPokemon().pokemon;

        List<TaskUtils.PendingTask> pendingTasks = TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this);
        for (int i = 0; i < pendingTasks.size(); i++) {
            TaskUtils.PendingTask pendingTask = pendingTasks.get(i);
            Task task = pendingTask.task();

            // Si la tarea está configurada como "pvp_only", saltarla en combates salvajes
            if (TaskUtils.getConfigBoolean(task, "pvp_only")) {
                continue;
            }

            if (this.checkPokemon(pokemon, task)) {
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }
}
