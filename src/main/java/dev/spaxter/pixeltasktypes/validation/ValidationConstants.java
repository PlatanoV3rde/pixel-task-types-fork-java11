package dev.spaxter.pixeltasktypes.validation;

import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBallRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.stats.evolution.conditions.EvoCondition;
import com.pixelmonmod.pixelmon.api.registries.PixelmonPalettes;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.enums.items.EnumFossils;
import com.pixelmonmod.pixelmon.enums.items.EnumRodType;

import java.util.Arrays;
import java.util.List;

/**
 * Static constants used for config validation.
 */
public class ValidationConstants {
    /**
     * List of valid Pokémon palettes as lowercase strings.
     */
    public static final List<String> PALETTES =
        PixelmonPalettes.getAll().stream().map((palette) -> palette.getName().toLowerCase()).toList();

    /**
     * List of valid Pokémon types as lowercase strings.
     */
    public static final List<String> POKEMON_TYPES =
        Arrays.asList(Element.getTypeNames(false)).stream().map((element) -> element.toLowerCase()).toList();

    /**
     * List of valid Pokémon species as lowercase strings.
     */
    public static final List<String> SPECIES =
        PixelmonSpecies.getAll().stream().map((species) -> species.getName().toLowerCase()).toList();

    /**
     * List of valid Pokeball names.
     */
    public static final List<String> POKE_BALLS =
        PokeBallRegistry.getAll().stream().map((pokeball) -> pokeball.getName().toLowerCase()).toList();

    /**
     * List of valid evolution types.
     */
    public static final List<String> EVOLUTION_TYPES =
        EvoCondition.evoConditionTypes.keySet().stream().map((evolutionType) -> evolutionType.toLowerCase()).toList();

    /**
     * List of valid evolution types.
     */
    public static final List<String> ROD_TYPES =
        Arrays.stream(EnumRodType.values()).map((rodType) -> rodType.name().toLowerCase()).toList();

    /**
     * List of valid fossil types.
     */
    public static final List<String> FOSSIL_TYPES =
        Arrays.stream(EnumFossils.values()).map((fossilType) -> fossilType.name().toLowerCase()).toList();
}
