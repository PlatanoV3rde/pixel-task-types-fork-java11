package dev.spaxter.pixeltasktypes.tasks;

import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Task;
import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import dev.spaxter.pixeltasktypes.util.ArclightUtils;
import dev.spaxter.pixeltasktypes.util.QuestHelper;
import dev.spaxter.pixeltasktypes.validation.PixelmonTaskConfigValidator;
import dev.spaxter.pixeltasktypes.validation.ValidationConstants;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import com.pixelmonmod.pixelmon.api.events.battles.AttackEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Use Pokémon moves in battle task type.
 */
public class MoveTaskType extends PixelmonTaskType {
    public MoveTaskType(PixelTaskTypes plugin) {
        super(plugin, "use_moves", "Use Pokémon moves in battle");

        // Validar que exista "amount" en la configuración
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));
        // Validar lista de nombres de movimientos
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useStringListValidator(ValidationConstants.MOVE_NAMES, this, "moves"));
    }

    /**
     * Se dispara cuando se usa un movimiento en batalla.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onUseMove(final AttackEvent.Use event) {
        ServerPlayerEntity player = event.user.getPlayerOwner();
        if (player == null) {
            return;
        }

        // --- DEBUG: nombre crudo y normalizado del ataque
        String rawAttackName = event.attack.getAttackName();
        String attackName = rawAttackName.toLowerCase();
        plugin.getLogger().info("[MoveTaskType] Jugador " + player.getUUID() +
            " usó ataque: " + rawAttackName);

        Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        QPlayer questPlayer = this.plugin.getQuestsApi()
            .getPlayerManager().getPlayer(player.getUUID());
        Pokemon pokemon = event.target.pokemon;

        // Iterar sobre las tareas pendientes aplicables
        for (TaskUtils.PendingTask pendingTask :
                TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {

            Task task = pendingTask.task();
            // Lista de movimientos configurados, en minúsculas
            List<String> requiredAttacks =
                QuestHelper.getConfigStringListAsLowercase(task, "moves");

            // --- DEBUG: imprimir lista de movimientos de la config
            plugin.getLogger().info("[MoveTaskType] Movimientos configurados para tarea "
                + task.getId() + ": " + requiredAttacks);

            // Si hay movimientos configurados y el actual no está entre ellos, saltamos
            if (requiredAttacks != null
                    && !requiredAttacks.isEmpty()
                    && !requiredAttacks.contains(attackName)) {
                continue;
            }

            // Si el Pokémon cumple el filtro de la tarea, incrementamos progreso
            if (this.checkPokemon(pokemon, task)) {
                plugin.getLogger().info("[MoveTaskType] Incrementando progreso para tarea "
                    + task.getId());
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }
}
