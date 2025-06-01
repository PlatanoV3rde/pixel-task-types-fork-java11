package dev.spaxter.pixeltasktypes.validation;

import com.pixelmonmod.pixelmon.api.battles.attack.AttackRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBallRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.stats.evolution.Evolution;
import com.pixelmonmod.pixelmon.api.registries.PixelmonPalettes;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.enums.items.EnumFossils;
import com.pixelmonmod.pixelmon.enums.items.EnumRodType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Static constants used for config validation.
 */
public class ValidationConstants {
    /**
     * List of valid Pokémon palettes as lowercase strings.
     */
    public static final List<String> PALETTES = PixelmonPalettes.getAll()
            .stream()
            .filter(palette -> palette != null)
            .map(palette -> palette.getName().toLowerCase())
            .collect(Collectors.toList());

    /**
     * List of valid Pokémon types as lowercase strings.
     */
    public static final List<String> POKEMON_TYPES = Arrays.asList(Element.getTypeNames(false))
            .stream()
            .filter(element -> element != null)
            .map(element -> element.toLowerCase())
            .collect(Collectors.toList());

    /**
     * List of valid Pokémon species as lowercase strings.
     */
    public static final List<String> SPECIES = PixelmonSpecies.getAll()
            .stream()
            .filter(species -> species != null)
            .map(species -> species.getName().toLowerCase())
            .collect(Collectors.toList());

    /**
     * List of valid Pokéball names.
     */
    public static final List<String> POKE_BALLS = PokeBallRegistry.getAll()
            .stream()
            .filter(pokeball -> pokeball != null)
            .map(pokeball -> pokeball.getName().toLowerCase())
            .collect(Collectors.toList());

    /**
     * List of valid evolution types.
     */
    public static final List<String> EVOLUTION_TYPES = Evolution.evolutionTypes.keySet()
            .stream()
            .filter(evoType -> evoType != null)
            .map(evoType -> evoType.toLowerCase())
            .collect(Collectors.toList());

    /**
     * List of valid fishing rod types.
     */
    public static final List<String> ROD_TYPES = Arrays.stream(EnumRodType.values())
            .filter(rodType -> rodType != null)
            .map(rodType -> rodType.name().toLowerCase())
            .collect(Collectors.toList());

    /**
     * List of valid fossil types.
     */
    public static final List<String> FOSSIL_TYPES = Arrays.stream(EnumFossils.values())
            .filter(fossilType -> fossilType != null)
            .map(fossilType -> fossilType.name().toLowerCase())
            .collect(Collectors.toList());

    /**
     * List of valid move names.
     */
    public static final List<String> MOVE_NAMES = AttackRegistry.getAllAttackNames()
            .stream()
            .filter(attackName -> attackName != null)
            .map(attackName -> attackName.toLowerCase().replaceAll(" ", "_"))
            .collect(Collectors.toList());
}
