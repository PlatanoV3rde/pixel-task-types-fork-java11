package dev.spaxter.pixeltasktypes.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.leonardobishop.quests.common.config.ConfigProblem;
import com.leonardobishop.quests.common.config.ConfigProblem.ConfigProblemType;
import com.leonardobishop.quests.common.tasktype.TaskType;
import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.api.registries.PixelmonPalettes;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;

public class PixelmonTaskConfigValidator {
    /**
     * List of valid Pokémon palettes as lowercase strings.
     */
    private final static List<String> validPalettes = PixelmonPalettes.getAll().stream()
            .map((palette) -> palette.getName().toLowerCase()).collect(Collectors.toList());

    /**
     * List of valid Pokémon types as lowercase strings.
     */
    private final static List<String> validTypes = Arrays.asList(Element.getTypeNames(false)).stream()
            .map((element) -> element.toLowerCase()).collect(Collectors.toList());

    /**
     * List of valid Pokémon species as lowercase strings.
     */
    private final static List<String> validSpecies = PixelmonSpecies.getAll().stream()
            .map((species) -> species.getName().toLowerCase()).collect(Collectors.toList());

    /**
     * Validate Pokémon types in task configurations.
     *
     * @param type Task type instance
     * @return Validator
     */
    public static TaskType.ConfigValidator usePokemonTypesValidator(final TaskType type, final String... paths) {
        return (config, problems) -> {
            for (String path : paths) {
                final Object configList = config.get(path);

                if (configList == null)
                    continue;

                final List<String> typeNames = new ArrayList<>();

                if (configList instanceof List<?> objectList) {
                    for (Object object : objectList) {
                        typeNames.add(String.valueOf(object));
                    }
                } else {
                    typeNames.add(String.valueOf(configList));
                }

                for (final String typeName : typeNames) {
                    if (!validTypes.contains(typeName.toLowerCase())) {
                        ConfigProblem problem = new ConfigProblem(ConfigProblemType.ERROR,
                                "Invalid pokémon type: " + typeName + ".\nMust be one of: " + validTypes.toString(),
                                null,
                                path);
                        problems.add(problem);
                    }
                }
            }
        };
    }

    /**
     * Validate Pokémon palettes in task configurations.
     *
     * @param type Task type instance
     * @return Validator
     */
    public static TaskType.ConfigValidator usePokemonPalettesValidator(final TaskType type, final String... paths) {
        return (config, problems) -> {
            for (String path : paths) {
                final Object configList = config.get(path);

                if (configList == null)
                    continue;

                final List<String> paletteNames = new ArrayList<>();

                if (configList instanceof List<?> objectList) {
                    for (Object object : objectList) {
                        paletteNames.add(String.valueOf(object));
                    }
                } else {
                    paletteNames.add(String.valueOf(configList));
                }

                for (final String paletteName : paletteNames) {
                    if (!validPalettes.contains(paletteName.toLowerCase())) {
                        ConfigProblem problem = new ConfigProblem(ConfigProblemType.ERROR,
                                "Invalid palette: " + paletteName + ".\nMust be one of: " + validPalettes.toString(),
                                null,
                                path);
                        problems.add(problem);
                    }
                }
            }
        };
    }

    public static TaskType.ConfigValidator usePokemonSpeciesValidator(final TaskType type, final String... paths) {
        return (config, problems) -> {
            for (String path : paths) {
                final Object configList = config.get(path);

                if (configList == null)
                    continue;

                final List<String> speciesNames = new ArrayList<>();

                if (configList instanceof List<?> objectList) {
                    for (Object object : objectList) {
                        speciesNames.add(String.valueOf(object));
                    }
                } else {
                    speciesNames.add(String.valueOf(configList));
                }

                for (final String species : speciesNames) {
                    if (!validSpecies.contains(species.toLowerCase())) {
                        ConfigProblem problem = new ConfigProblem(ConfigProblemType.ERROR,
                                "Invalid species: " + species,
                                null,
                                path);
                        problems.add(problem);
                    }
                }
            }
        };
    }

}
