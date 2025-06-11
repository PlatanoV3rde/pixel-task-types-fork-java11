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
     * Runs when a Pokémon is defeated in PvP battle.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPokemonDefeat(final PixelmonKnockoutEvent event) {
        // Null checks to avoid NPEs
        if (event == null
            || event.source == null
            || event.source.getPlayerOwner() == null
            || event.pokemon == null
            || event.pokemon.getPlayerOwner() == null) {
            return;
        }

        ServerPlayerEntity player = event.source.getPlayerOwner();

        Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        if (bukkitPlayer == null) {
            this.plugin.getLogger().warning("onPokemonDefeat: Bukkit player is null for UUID " + player.getUUID());
            return;
        }
        QPlayer questPlayer = this.plugin.getQuestsApi()
                                       .getPlayerManager()
                                       .getPlayer(player.getUUID());
        if (questPlayer == null) {
            this.plugin.getLogger().warning("onPokemonDefeat: QPlayer is null for UUID " + player.getUUID());
            return;
        }

        Pokemon pokemon = event.pokemon.pokemon;
        if (pokemon == null) {
            this.plugin.getLogger().warning("onPokemonDefeat: defeated Pokémon is null for player "
                                            + player.getName().getString());
            return;
        }

        for (TaskUtils.PendingTask pendingTask :
                TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {
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
     * Runs when a wild Pokémon is defeated.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWildPokemonDefeat(final BeatWildPixelmonEvent event) {
        // Null checks
        if (event == null
            || event.player == null
            || event.wpp == null
            || event.wpp.getFaintedPokemon() == null
            || event.wpp.getFaintedPokemon().pokemon == null) {
            return;
        }

        ServerPlayerEntity player = event.player;

        Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        if (bukkitPlayer == null) {
            this.plugin.getLogger().warning("onWildPokemonDefeat: Bukkit player is null for UUID " + player.getUUID());
            return;
        }
        QPlayer questPlayer = this.plugin.getQuestsApi()
                                       .getPlayerManager()
                                       .getPlayer(player.getUUID());
        if (questPlayer == null) {
            this.plugin.getLogger().warning("onWildPokemonDefeat: QPlayer is null for UUID " + player.getUUID());
            return;
        }

        Pokemon pokemon = event.wpp.getFaintedPokemon().pokemon;
        if (pokemon == null) {
            this.plugin.getLogger().warning("onWildPokemonDefeat: defeated wild Pokémon is null for player "
                                            + player.getName().getString());
            return;
        }

        for (TaskUtils.PendingTask pendingTask :
                TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {
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
