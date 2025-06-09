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

        // Normalizamos nombre de ataque: trim → lowercase → espacios a '_'
        String rawAttack = event.attack.getAttackName();
        String attackName = rawAttack == null
            ? ""
            : rawAttack.trim()
                       .toLowerCase()
                       .replaceAll("\\s+", "_");

        Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        QPlayer questPlayer = this.plugin.getQuestsApi()
            .getPlayerManager().getPlayer(player.getUUID());
        Pokemon pokemon = event.target.pokemon;

        // Recorremos todas las tareas que puedan aplicarse
        for (TaskUtils.PendingTask pendingTask :
                TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {

            Task task = pendingTask.task();
            List<String> movs = QuestHelper.getConfigStringListAsLowercase(task, "moves");

            // Filtrado: si hay lista y no contiene el ataque, saltamos
            if (movs != null && !movs.contains(attackName)) {
                continue;
            }

            // Incrementamos progreso si el Pokémon cumple el criterio
            if (this.checkPokemon(pokemon, task)) {
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }
}
