package dev.spaxter.pixeltasktypes.tasks;

import java.util.List;
import java.util.stream.Collectors;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.quest.Task;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;

import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import dev.spaxter.pixeltasktypes.util.QuestHelper;
import dev.spaxter.pixeltasktypes.validation.PixelmonTaskConfigValidator;
import io.izzel.arclight.api.Arclight;

public class PixelmonTaskType extends BukkitTaskType {

    public final PixelTaskTypes plugin;
    public final BukkitQuestsPlugin questsApi;

    public PixelmonTaskType(PixelTaskTypes plugin, String name, String description) {
        super(name, "PixelTaskTypes", description);
        this.plugin = plugin;
        this.questsApi = this.plugin.getQuestsApi();
        Arclight.registerForgeEvent(null, Pixelmon.EVENT_BUS, this);

        super.addConfigValidator(PixelmonTaskConfigValidator.usePokemonSpeciesValidator(this, "species"));
        super.addConfigValidator(PixelmonTaskConfigValidator.usePokemonSpeciesValidator(this, "not_species"));
        super.addConfigValidator(PixelmonTaskConfigValidator.usePokemonTypesValidator(this, "pokemon_types"));
        super.addConfigValidator(PixelmonTaskConfigValidator.usePokemonPalettesValidator(this, "palettes"));
        super.addConfigValidator(TaskUtils.useBooleanConfigValidator(this, "legendary_only"));
        super.addConfigValidator(TaskUtils.useIntegerConfigValidator(this, "pokemon_level"));
    }

    /**
     * Check if a Pokémon matches the configured task requirements.
     *
     * @param pokemon The pokemon entity to check.
     * @param task    The task to check the configuration for.
     * @return {@code true} if the Pokémon passes all checks, otherwise
     *         {@code false}
     */
    public boolean checkPokemon(final Pokemon pokemon, final Task task) {
        final List<String> requiredTypes = QuestHelper.getConfigStringListAsLowercase(task, "pokemon_types");
        final List<String> requiredSpecies = QuestHelper.getConfigStringListAsLowercase(task, "species");
        final List<String> notSpecies = QuestHelper.getConfigStringListAsLowercase(task, "not_species");
        final List<String> requiredPalettes = QuestHelper.getConfigStringListAsLowercase(task, "palettes");
        final boolean legendaryOnly = TaskUtils.getConfigBoolean(task, "legendary_only");
        final Integer requiredLevel = (Integer) task.getConfigValue("pokemon_level");

        // Check for required level
        if (requiredLevel != null && pokemon.getPokemonLevel() < requiredLevel) {
            return false;
        }

        // Check for required types
        if (requiredTypes != null && !this.checkType(pokemon, requiredTypes)) {
            return false;
        }

        // Check for required species
        if (requiredSpecies != null && !this.checkSpecies(pokemon, requiredSpecies)) {
            return false;
        }

        // Check for species to not count
        if (notSpecies != null && this.checkSpecies(pokemon, notSpecies)) {
            return false;
        }

        // Check for required palettes
        if (requiredPalettes != null && !this.checkPalettes(pokemon, requiredPalettes)) {
            return false;
        }

        // Check if only legendaries should cound
        if (legendaryOnly == true && !this.checkLegendary(pokemon)) {
            return false;
        }

        return true;
    }

    private boolean checkType(final Pokemon pokemon, final List<String> requiredTypes) {
        List<String> types = pokemon.getForm().getTypes().stream().map((element) -> {
            return element.getName().toLowerCase();
        }).collect(Collectors.toList());

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
        PixelTaskTypes.logger.info("Species checked: " + species);
        return requiredSpecies.contains(species);
    }

    private boolean checkPalettes(final Pokemon pokemon, final List<String> requiredPalettes) {
        String palette = pokemon.getPalette().getName().toLowerCase();
        PixelTaskTypes.logger.info("Palette checked: " + palette);
        return requiredPalettes.contains(palette);
    }
}
