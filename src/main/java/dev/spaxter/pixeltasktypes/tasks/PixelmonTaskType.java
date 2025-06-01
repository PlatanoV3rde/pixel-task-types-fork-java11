package dev.spaxter.pixeltasktypes.tasks;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.quest.Task;
import com.leonardobishop.quests.common.tasktype.TaskType;
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
 * Custom {@link TaskType} for Pixelmon‐based tasks.
 */
public abstract class PixelmonTaskType extends BukkitTaskType {
    public final PixelTaskTypes plugin;
    public final BukkitQuestsPlugin questsApi;

    public PixelmonTaskType(PixelTaskTypes plugin, String name, String description) {
        super(name, "PixelTaskTypes", description);
        this.plugin = plugin;
        this.questsApi = this.plugin.getQuestsApi();
        Arclight.registerForgeEvent(null, Pixelmon.EVENT_BUS, this);

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
     * Check if a Pokémon matches the configured task requirements.
     *
     * @param pokemon The Pokémon entity to check.
     * @param task    The task to check the configuration for.
     * @return {@code true} if the Pokémon passes all checks, otherwise {@code false}.
     */
    public boolean checkPokemon(final Pokemon pokemon, final Task task) {
        List<String> requiredTypes = QuestHelper.getConfigStringListAsLowercase(task, "pokemon_types");
        List<String> requiredSpecies = QuestHelper.getConfigStringListAsLowercase(task, "species");
        List<String> notSpecies = QuestHelper.getConfigStringListAsLowercase(task, "not_species");
        List<String> requiredPalettes = QuestHelper.getConfigStringListAsLowercase(task, "palettes");
        boolean legendaryOnly = TaskUtils.getConfigBoolean(task, "legendary_only");
        Integer requiredLevel = (Integer) task.getConfigValue("pokemon_level");

        // Check for required level
        if (requiredLevel != null && pokemon.getPokemonLevel() < requiredLevel) {
            return false;
        }

        // Check for required types
        if (requiredTypes != null && !checkType(pokemon, requiredTypes)) {
            return false;
        }

        // Check for required species
        if (requiredSpecies != null && !checkSpecies(pokemon, requiredSpecies)) {
            return false;
        }

        // Check for species to not count
        if (notSpecies != null && checkSpecies(pokemon, notSpecies)) {
            return false;
        }

        // Check for required palettes
        if (requiredPalettes != null && !checkPalettes(pokemon, requiredPalettes)) {
            return false;
        }

        // Check if only legendaries should count
        if (legendaryOnly && !checkLegendary(pokemon)) {
            return false;
        }

        return true;
    }

    private boolean checkType(final Pokemon pokemon, final List<String> requiredTypes) {
        // Construir lista manualmente en lugar de usar streams
        List<String> types = new ArrayList<String>();
        for (com.pixelmonmod.pixelmon.api.pokemon.Pokemon formPokemon : new Pokemon[] { pokemon }) {
            // Extraer tipos desde getForm().getTypes()
            for (net.minecraft.util.registry.RegistryKey<net.minecraft.util.registry.Registry<com.pixelmonmod.pixelmon.api.pokemon.Pokemon>> typeKey : pokemon.getForm().getTypes()) {
                // Aquí asumimos que getTypes() devuelve objetos con getName(), pero si devuelve algo distinto,
                // hay que ajustarlo a como Pixelmon expone los tipos en esa versión.
                // Por ejemplo, si getTypes() devuelve List<Type>, entonces sería:
                // for (Type t : pokemon.getForm().getTypes()) { types.add(t.getName().toLowerCase()); }
                // Ajusta según la API real. 
            }
        }
        // En muchas versiones, getForm().getTypes() devuelve List<Pokemon.Type>, así:
        types.clear();
        for (com.pixelmonmod.pixelmon.entities.pixelmon.stats.FormData.Type type : pokemon.getForm().getTypes()) {
            types.add(type.getName().toLowerCase());
        }

        // Verificar si alguno coincide con requiredTypes
        for (String type : types) {
            if (requiredTypes.contains(type)) {
                PixelTaskTypes.logger.info("Type passed: " + type);
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
