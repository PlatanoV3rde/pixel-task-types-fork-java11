package dev.spaxter.pixeltasktypes.tasks;

import java.util.List;
import java.util.stream.Collectors;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.quest.Task;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;

import dev.spaxter.pixeltasktypes.PixelTaskTypes;
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
        super.addConfigValidator(PixelmonTaskConfigValidator.usePokemonTypesValidator(this, "pokemon_types"));
        super.addConfigValidator(PixelmonTaskConfigValidator.usePokemonPalettesValidator(this, "palettes"));
    }

    /**
     * Check if a Pokémon matches the configured task requirements.
     *
     * @param pokemon The pokemon entity to check.
     * @param task The task to check the configuration for.
     * @return {@code true} if the Pokémon passes all checks, otherwise {@code false}
     */
    public boolean checkPokemon(final PixelmonEntity pokemon, final Task task) {
        final List<String> requiredTypes = TaskUtils.getConfigStringList(task, "pokemon_types");
        final List<String> requiredSpecies = TaskUtils.getConfigStringList(task, "species");
        final List<String> requiredPalettes = TaskUtils.getConfigStringList(task, "palettes");

        PixelTaskTypes.logger.info("Checking for required types " + requiredTypes + ".");
        if (requiredTypes != null && !this.checkType(pokemon, requiredTypes)) {
            return false;
        }

        PixelTaskTypes.logger.info("Checking for required species " + requiredSpecies + ".");
        if (requiredSpecies != null && !this.checkSpecies(pokemon, requiredSpecies)) {
            return false;
        }

        PixelTaskTypes.logger.info("Checking for required palettes " + requiredPalettes + ".");
        if (requiredPalettes != null && !this.checkPalettes(pokemon, requiredPalettes)) {
            return false;
        }

        return true;
    }

    private boolean checkType(final PixelmonEntity pokemon, final List<String> requiredTypes) {
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

    private boolean checkSpecies(final PixelmonEntity pokemon, final List<String> requiredSpecies) {
        String species = pokemon.getSpecies().getName().toLowerCase();
        PixelTaskTypes.logger.info("Species checked: " + species);
        return requiredSpecies.contains(species);
    }

    private boolean checkPalettes(final PixelmonEntity pokemon, final List<String> requiredPalettes) {
        String palette = pokemon.getPalette().getName().toLowerCase();
        PixelTaskTypes.logger.info("Palette checked: " + palette);
        return requiredPalettes.contains(palette);
    }
}
