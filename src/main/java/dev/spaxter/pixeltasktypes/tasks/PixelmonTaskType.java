package dev.spaxter.pixeltasktypes.tasks;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.quest.Task;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import dev.spaxter.pixeltasktypes.util.QuestHelper;
import dev.spaxter.pixeltasktypes.validation.PixelmonTaskConfigValidator;
import dev.spaxter.pixeltasktypes.validation.ValidationConstants;

import io.izzel.arclight.api.Arclight;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom {@link com.leonardobishop.quests.common.tasktype.TaskType} for Pixelmon based tasks.
 */
public abstract class PixelmonTaskType extends BukkitTaskType {
    public final PixelTaskTypes plugin;
    public final BukkitQuestsPlugin questsApi;

    public PixelmonTaskType(PixelTaskTypes plugin, String name, String description) {
        super(name, "PixelTaskTypes", description);
        this.plugin = plugin;
        this.questsApi = this.plugin.getQuestsApi();
        // Registrar los eventos de Pixelmon
        Arclight.registerForgeEvent(null, Pixelmon.EVENT_BUS, this);

        // Validadores comunes
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useStringListValidator(ValidationConstants.SPECIES, this, "species")
        );
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useStringListValidator(ValidationConstants.SPECIES, this, "not_species")
        );
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useStringListValidator(ValidationConstants.POKEMON_TYPES, this, "pokemon_types")
        );
        super.addConfigValidator(
            PixelmonTaskConfigValidator.useStringListValidator(ValidationConstants.PALETTES, this, "palettes")
        );
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "legendary_only"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "pokemon_level"));
    }

    /**
     * Comprueba si un Pokémon cumple los requisitos de configuración de la tarea.
     *
     * @param pokemon El Pokémon a comprobar.
     * @param task    La tarea con su configuración.
     * @return {@code true} si cumple todos los filtros; {@code false} en caso contrario.
     */
    public boolean checkPokemon(final Pokemon pokemon, final Task task) {
        final List<String> requiredTypes   = QuestHelper.getConfigStringListAsLowercase(task, "pokemon_types");
        final List<String> requiredSpecies = QuestHelper.getConfigStringListAsLowercase(task, "species");
        final List<String> notSpecies      = QuestHelper.getConfigStringListAsLowercase(task, "not_species");
        final List<String> requiredPalettes= QuestHelper.getConfigStringListAsLowercase(task, "palettes");
        final boolean legendaryOnly        = TaskUtils.getConfigBoolean(task, "legendary_only");
        final Integer requiredLevel        = (Integer) task.getConfigValue("pokemon_level");

        // Nivel mínimo
        if (requiredLevel != null && pokemon.getPokemonLevel() < requiredLevel) {
            return false;
        }
        // Tipos
        if (requiredTypes != null && !this.checkType(pokemon, requiredTypes)) {
            return false;
        }
        // Especies
        if (requiredSpecies != null && !this.checkSpecies(pokemon, requiredSpecies)) {
            return false;
        }
        // Excluir ciertas especies
        if (notSpecies != null && this.checkSpecies(pokemon, notSpecies)) {
            return false;
        }
        // Paletas
        if (requiredPalettes != null && !this.checkPalettes(pokemon, requiredPalettes)) {
            return false;
        }
        // Solo legendarios
        if (legendaryOnly && !this.checkLegendary(pokemon)) {
            return false;
        }
        return true;
    }

    private boolean checkType(final Pokemon pokemon, final List<String> requiredTypes) {
        List<String> types = new ArrayList<>();
        for (Object typeObj : pokemon.getForm().getTypes()) {
            try {
                String typeName = (String) typeObj.getClass().getMethod("getName").invoke(typeObj);
                types.add(typeName.toLowerCase());
            } catch (Exception e) {
                this.plugin.getLogger().warning("Error getting type name: " + e.getMessage());
            }
        }
        for (String type : types) {
            if (requiredTypes.contains(type)) {
                this.plugin.getLogger().info("Type passed: " + type);
                return true;
            }
        }
        return false;
    }

    private boolean checkSpecies(final Pokemon pokemon, final List<String> requiredSpecies) {
        String species = pokemon.getSpecies().getName().toLowerCase();
        return requiredSpecies.contains(species);
    }

    private boolean checkPalettes(final Pokemon pokemon, final List<String> requiredPalettes) {
        String palette = pokemon.getPalette().getName().toLowerCase();
        return requiredPalettes.contains(palette);
    }

    private boolean checkLegendary(final Pokemon pokemon) {
        return pokemon.getSpecies().isLegendary();
    }
}
