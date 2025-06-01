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

import java.util.List;

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
        ServerPlayerEntity player = event.getPlayer();
        Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());

        // 1) Validación de nulos
        if (bukkitPlayer == null || questPlayer == null) {
            return;
        }

        // 2) Obtener el Pokémon resultante del huevo
        Pokemon pokemon = event.getPokemon();

        // 3) Iterar con for tradicional y comprobar objetivos
        List<TaskUtils.PendingTask> pendingTasks = TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this);
        for (int i = 0; i < pendingTasks.size(); i++) {
            TaskUtils.PendingTask pendingTask = pendingTasks.get(i);
            Task task = pendingTask.task();

            if (this.checkPokemon(pokemon, task)) {
                QuestHelper.incrementNumericProgress(pendingTask);
            }
        }
    }
}
