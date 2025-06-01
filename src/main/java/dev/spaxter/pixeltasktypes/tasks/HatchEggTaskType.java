package dev.spaxter.pixeltasktypes.tasks;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Task;
import com.pixelmonmod.pixelmon.api.events.EggHatchEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import dev.spaxter.pixeltasktypes.util.ArclightUtils;
import dev.spaxter.pixeltasktypes.util.QuestHelper;

import org.bukkit.entity.Player;

/**
 * Hatch Pokémon eggs task type.
 */
public class HatchEggTaskType extends PixelmonTaskType {
    public HatchEggTaskType(PixelTaskTypes plugin) {
        super(plugin, "hatch_egg", "Hatch Pokémon eggs");

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
    }

    /**
     * Runs after a Pokémon egg is hatched.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEggHatch(final EggHatchEvent.Post event) {
        final ServerPlayerEntity player = event.getPlayer();
        final Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        final QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());
        Pokemon pokemon = event.getPokemon();

        for (final TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {
            final Task task = pendingTask.task();

            if (this.checkPokemon(pokemon, task)) {
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }
}