package dev.spaxter.pixeltasktypes.tasks;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Task;
import com.pixelmonmod.pixelmon.api.events.battles.AttackEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import dev.spaxter.pixeltasktypes.util.ArclightUtils;
import dev.spaxter.pixeltasktypes.util.QuestHelper;
import dev.spaxter.pixeltasktypes.validation.PixelmonTaskConfigValidator;
import dev.spaxter.pixeltasktypes.validation.ValidationConstants;

import java.util.List;

import org.bukkit.entity.Player;

/**
 * Hatch Pokémon eggs task type.
 */
public class MoveTaskType extends PixelmonTaskType {
    public MoveTaskType(PixelTaskTypes plugin) {
        super(plugin, "use_moves", "Use Pokémon moves in battle");

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useStringListValidator(ValidationConstants.MOVE_NAMES, this, "moves"));
    }

    /**
     * Runs after a Pokémon egg is hatched.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onUseMove(final AttackEvent.Use event) {
        final ServerPlayerEntity player = event.user.getPlayerOwner();

        if (player == null) {
            return;
        }

        final Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        final QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());
        final Pokemon pokemon = event.target.pokemon;

        final String attackName = event.attack.getAttackName().toLowerCase();

        for (final TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {
            final Task task = pendingTask.task();
            final List<String> requiredAttacks = QuestHelper.getConfigStringListAsLowercase(task, "moves");

            if (requiredAttacks != null && !requiredAttacks.contains(attackName)) {
                continue;
            }

            if (this.checkPokemon(pokemon, task)) {
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }
}