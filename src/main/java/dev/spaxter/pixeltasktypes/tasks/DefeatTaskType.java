package dev.spaxter.pixeltasktypes.tasks;

import org.bukkit.entity.Player;

import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Task;
import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.pixelmonmod.pixelmon.api.events.PixelmonKnockoutEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import dev.spaxter.pixeltasktypes.util.ArclightUtils;
import dev.spaxter.pixeltasktypes.util.QuestHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DefeatTaskType extends PixelmonTaskType {

    public DefeatTaskType(PixelTaskTypes plugin) {
        super(plugin, "defeat_pokemon", "Defeat Pokémon in battle");

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "wild_only"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "pvp_only"));
    }

    @SubscribeEvent
    public void onPokemonDefeat(final PixelmonKnockoutEvent event) {
        final ServerPlayerEntity player = event.source.getPlayerOwner();
        final Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        final QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());

        final Pokemon pokemon = event.pokemon.pokemon;

        for (final TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {
            Task task = pendingTask.task();
            if ((boolean) task.getConfigValue("wild_only")) {
                continue;
            }
            if (this.checkPokemon(pokemon, task)) {
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }

    @SubscribeEvent
    public void onWildPokemonDefeat(final BeatWildPixelmonEvent event) {
        final ServerPlayerEntity player = event.player;
        final Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        final QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());

        final PixelmonEntity pokemon = event.wpp.getFaintedPokemon().entity;

        for (final TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {
            Task task = pendingTask.task();
            if ((boolean) task.getConfigValue("pvp_only")) {
                continue;
            }
            if (this.checkPokemon(pokemon, task)) {
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }
}
