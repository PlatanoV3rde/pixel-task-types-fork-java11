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
import com.pixelmonmod.pixelmon.enums.EnumAbilitySlot;

import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import dev.spaxter.pixeltasktypes.util.ArclightUtils;
import dev.spaxter.pixeltasktypes.util.QuestHelper;

import java.util.List;
import java.util.Locale;

import org.bukkit.entity.Player;

/**
 * Defeat Pokémon task type.
 * Incluye validaciones de:
 *   • ub (Ultra Beast)
 *   • ha (Hidden Ability)
 *   • ability (lista blanca de habilidades)
 */
public class DefeatTaskType extends PixelmonTaskType {

    public DefeatTaskType(PixelTaskTypes plugin) {
        super(plugin, "defeat_pokemon", "Defeat Pokémon in battle");

        // Validación obligatoria de "amount"
        super.addConfigValidator(TaskUtils.useRequiredConfigValidator(this, "amount"));

        // Validación opcional de "wild_only" y "pvp_only" (ya existían)
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "wild_only"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "pvp_only"));

        // Nuevas validaciones opcionales para ub, ha y ability:
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useBooleanConfigValidator(/* validador boolean genérico */, this, "ub"));
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useBooleanConfigValidator(/* validador boolean genérico */, this, "ha"));
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useStringListValidator(/* validador lista de strings */, this, "ability"));
    }

    /**
     * Se ejecuta cuando un jugador derrota un Pokémon que pertenece a otro jugador (PvP).
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPokemonDefeat(final PixelmonKnockoutEvent event) {
        final ServerPlayerEntity player = event.source.getPlayerOwner();
        final ServerPlayerEntity opponent = event.pokemon.getPlayerOwner();

        // Si uno de los dos no es jugador, salimos (solo importa PvP aquí)
        if (player == null || opponent == null) {
            return;
        }

        final Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        final QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());
        final Pokemon defeated = event.pokemon.pokemon;

        for (final TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {
            Task task = pendingTask.task();

            // Si la misión exige solo wild (wild_only: true), saltamos
            if (QuestHelper.getConfigBoolean(task, "wild_only", false)) {
                continue;
            }

            // Validación básica previa (nivel, tipo, etc.) que ya tienes en PixelmonTaskType
            if (!this.checkPokemon(defeated, task)) {
                continue;
            }

            // ------ A PARTIR DE AQUÍ, verificamos ub, ha y ability ------

            // 1) Leer config de "ub" (Ultra Beast)
            boolean requireUB = QuestHelper.getConfigBoolean(task, "ub", false);
            if (requireUB && !defeated.isUltraBeast()) {
                continue;
            }

            // 2) Leer config de "ha" (Hidden Ability)
            boolean requireHA = QuestHelper.getConfigBoolean(task, "ha", false);
            if (requireHA && defeated.getAbilitySlot() != EnumAbilitySlot.HIDDEN) {
                continue;
            }

            // 3) Leer config de "ability" (lista blanca de habilidades)
            List<String> abilityWhitelist = QuestHelper.getConfigStringListAsLowercase(task, "ability");
            if (abilityWhitelist != null && !abilityWhitelist.isEmpty()) {
                String pokeAbilityName = defeated.getAbility().getName().toLowerCase(Locale.ROOT);
                if (!abilityWhitelist.contains(pokeAbilityName)) {
                    continue;
                }
            }

            // ------ Si pasó todas las validaciones, se incrementa el progreso ------

            QuestHelper.incrementNumericProgress(pendingTask);
        }
    }

    /**
     * Se ejecuta cuando un jugador derrota un Pokémon salvaje.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onWildPokemonDefeat(final BeatWildPixelmonEvent event) {
        final ServerPlayerEntity player = event.player;
        final Player bukkitPlayer = ArclightUtils.getBukkitPlayer(player.getUUID());
        final QPlayer questPlayer = this.plugin.getQuestsApi().getPlayerManager().getPlayer(player.getUUID());
        final Pokemon defeated = event.wpp.getFaintedPokemon().pokemon;

        for (final TaskUtils.PendingTask pendingTask : TaskUtils.getApplicableTasks(bukkitPlayer, questPlayer, this)) {
            Task task = pendingTask.task();

            // Si la misión exige solo PvP (pvp_only: true), saltamos
            if (QuestHelper.getConfigBoolean(task, "pvp_only", false)) {
                continue;
            }

            // Validación básica previa (nivel, tipo, etc.)
            if (!this.checkPokemon(defeated, task)) {
                continue;
            }

            // ------ A PARTIR DE AQUÍ, verificamos ub, ha y ability ------

            // 1) Leer config de "ub" (Ultra Beast)
            boolean requireUB = QuestHelper.getConfigBoolean(task, "ub", false);
            if (requireUB && !defeated.isUltraBeast()) {
                continue;
            }

            // 2) Leer config de "ha" (Hidden Ability)
            boolean requireHA = QuestHelper.getConfigBoolean(task, "ha", false);
            if (requireHA && defeated.getAbilitySlot() != EnumAbilitySlot.HIDDEN) {
                continue;
            }

            // 3) Leer config de "ability" (lista blanca de habilidades)
            List<String> abilityWhitelist = QuestHelper.getConfigStringListAsLowercase(task, "ability");
            if (abilityWhitelist != null && !abilityWhitelist.isEmpty()) {
                String pokeAbilityName = defeated.getAbility().getName().toLowerCase(Locale.ROOT);
                if (!abilityWhitelist.contains(pokeAbilityName)) {
                    continue;
                }
            }

            // ------ Si pasó todas las validaciones, se incrementa el progreso ------

            QuestHelper.incrementNumericProgress(pendingTask);
        }
    }
}
