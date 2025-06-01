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
 * Use move task type.
 */
public class MoveTaskType extends PixelmonTaskType {
    public MoveTaskType(PixelTaskTypes plugin) {
        super(plugin, "use_moves", "Use Pokémon moves in battle");

        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useStringListValidator(ValidationConstants.MOVE_NAMES, this, "moves"));
    }

    /**
     * Runs when a Pokémon uses a move in battle.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onUseMove(final AttackEvent.Use event) {
        ServerPlayerEntity player = event.user.getPlayerOwner();

        // Salir si no hay jugador válido
        if (player == null) {
            return;
        }

        Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());

        // Evitar crash si no se puede obtener el jugador de Bukkit o de Quests
        if (bukkitPlayer == null || questPlayer == null) {
            return;
        }

        Pokemon pokemon = event.target.pokemon;
        String attackName = event.attack.getAttackName().toLowerCase();

        List<TaskUtils.PendingTask> pendingTasks = TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this);
        for (int i = 0; i < pendingTasks.size(); i++) {
            TaskUtils.PendingTask pendingTask = pendingTasks.get(i);
            Task task = pendingTask.task();

            List<String> requiredAttacks = QuestHelper.getConfigStringListAsLowercase(task, "moves");
            if (requiredAttacks != null && !requiredAttacks.contains(attackName)) {
                continue;
            }

            if (this.checkPokemon(pokemon, task)) {
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }
}
