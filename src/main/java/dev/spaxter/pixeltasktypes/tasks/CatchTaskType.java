package dev.spaxter.pixeltasktypes.tasks;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.quest.Task;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumAbilitySlot;

import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import dev.spaxter.pixeltasktypes.util.ArclightUtils;
import dev.spaxter.pixeltasktypes.util.QuestHelper;
import dev.spaxter.pixeltasktypes.validation.PixelmonTaskConfigValidator;
import dev.spaxter.pixeltasktypes.validation.ValidationConstants;

import java.util.List;
import java.util.Locale;

import org.bukkit.entity.Player;

/**
 * Catch Pokémon task type.
 */
public class CatchTaskType extends PixelmonTaskType {

    public CatchTaskType(PixelTaskTypes plugin) {
        super(plugin, "catch_pokemon", "Catch a set number of Pokémon");

        // Validación obligatoria de "amount" (número de Pokémon a capturar)
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));

        // Validación opcional de lista de Poké Balls permitidas (poke_balls)
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useStringListValidator(ValidationConstants.POKE_BALLS, this, "poke_balls"));

        // Validación opcional de ub (boolean)
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useBooleanValidator(ValidationConstants.BOOLEAN, this, "ub"));

        // Validación opcional de ha (boolean)
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useBooleanValidator(ValidationConstants.BOOLEAN, this, "ha"));

        // Validación opcional de ability (lista de Strings)
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useStringListValidator(ValidationConstants.ABILITIES, this, "ability"));
    }

    /**
     * Runs when a Pokémon is successfully captured.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPokemonCatch(final CaptureEvent.SuccessfulCapture event) {
        final ServerPlayerEntity player = event.getPlayer();
        final Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        final QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());
        Pokemon pokemon = event.getPokemon().getPokemon();

        // Recorremos todas las tareas activas de tipo catch_pokemon para este jugador
        for (final TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {
            final Task task = pendingTask.task();

            // 1) Validar Poké Balls permitidas (poke_balls)
            List<String> requiredPokeballs = QuestHelper.getConfigStringListAsLowercase(task, "poke_balls");
            String pokeball = event.getPokeBall().getBallType().getName().toLowerCase(Locale.ROOT);
            if (requiredPokeballs != null && !requiredPokeballs.contains(pokeball)) {
                continue;
            }

            // 2) Validación básica que ya tenías en PixelmonTaskType (biomas, nivel, etc.)
            if (!this.checkPokemon(pokemon, task)) {
                continue;
            }

            // 3) Leer nuevas propiedades de configuración de la misión
            boolean requireUB = QuestHelper.getConfigBoolean(task, "ub", false);
            boolean requireHA = QuestHelper.getConfigBoolean(task, "ha", false);
            List<String> abilityWhitelist = QuestHelper.getConfigStringListAsLowercase(task, "ability");

            // 4) Chequear Ultra Beast (UB)
            if (requireUB && !pokemon.isUltraBeast()) {
                continue;
            }

            // 5) Chequear Hidden Ability (HA)
            if (requireHA && pokemon.getAbilitySlot() != EnumAbilitySlot.HIDDEN) {
                continue;
            }

            // 6) Chequear whitelist de habilidades
            if (abilityWhitelist != null && !abilityWhitelist.isEmpty()) {
                String pokeAbilityName = pokemon.getAbility().getName().toLowerCase(Locale.ROOT);
                if (!abilityWhitelist.contains(pokeAbilityName)) {
                    continue;
                }
            }

            // 7) Si llega aquí, cumple todas las condiciones: aumentamos el progreso
            QuestHelper.incrementNumericProgress(pendingTask);
        }
    }
}
