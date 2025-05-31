package dev.spaxter.pixeltasktypes.validation;

import com.pixelmonmod.pixelmon.api.battles.attack.AttackRegistry;
import com.pixelmonmod.pixelmon.api.events.EvolveEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Element;
import com.pixelmonmod.pixelmon.api.pokemon.item.pokeball.PokeBallRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.stats.evolution.Evolution;
import com.pixelmonmod.pixelmon.api.pokemon.stats.evolution.conditions.EvoCondition;
import com.pixelmonmod.pixelmon.api.registries.PixelmonPalettes;
import com.pixelmonmod.pixelmon.api.registries.PixelmonSpecies;
import com.pixelmonmod.pixelmon.entities.pixelmon.helpers.EvolutionQueryList;
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
    public static final List<String> PALETTES = PixelmonPalettes.getAll()
                                                    .stream()
                                                    .filter(value -> value != null)
                                                    .map((palette) -> palette.getName().toLowerCase())
                                                    .toList();

    /**
     * List of valid Pokémon types as lowercase strings.
     */
    public static final List<String> POKEMON_TYPES = Arrays.asList(Element.getTypeNames(false))
                                                         .stream()
                                                         .filter(value -> value != null)
                                                         .map((element) -> element.toLowerCase())
                                                         .toList();

    /**
     * List of valid Pokémon species as lowercase strings.
     */
    public static final List<String> SPECIES = PixelmonSpecies.getAll()
                                                   .stream()
                                                   .filter(value -> value != null)
                                                   .map((species) -> species.getName().toLowerCase())
                                                   .toList();

    /**
     * List of valid Pokeball names.
     */
    public static final List<String> POKE_BALLS = PokeBallRegistry.getAll()
                                                      .stream()
                                                      .filter(value -> value != null)
                                                      .map((pokeball) -> pokeball.getName().toLowerCase())
                                                      .toList();

    /**
     * List of valid evolution types.
     */
    public static final List<String> EVOLUTION_TYPES = Evolution.evolutionTypes.keySet()
                                                           .stream()
                                                           .filter(value -> value != null)
                                                           .map((evolutionType) -> evolutionType.toLowerCase())
                                                           .toList();

    /**
     * List of valid fishing rod types.
     */
    public static final List<String> ROD_TYPES = Arrays.stream(EnumRodType.values())
                                                     .filter(value -> value != null)
                                                     .map((rodType) -> rodType.name().toLowerCase())
                                                     .toList();

    /**
     * List of valid fossil types.
     */
    public static final List<String> FOSSIL_TYPES = Arrays.stream(EnumFossils.values())
                                                        .filter(value -> value != null)
                                                        .map((fossilType) -> fossilType.name().toLowerCase())
                                                        .toList();

    /**
     * List of valid move names.
     */
    public static final List<String> MOVE_NAMES =
        AttackRegistry.getAllAttackNames()
            .stream()
            .filter(value -> value != null)
            .map((attackName) -> attackName.toLowerCase().replaceAll(" ", "_"))
            .toList();

    // ───────────────────────────────────────────────────────────────────────────
    // Nuevas constantes para las claves de configuración de UB, HA y Ability
    // ───────────────────────────────────────────────────────────────────────────

    /** Clave para indicar “Ultra Beast” (booleano). */
    public static final String UB = "ub";

    /** Clave para indicar “Hidden Ability” (booleano). */
    public static final String HA = "ha";

    /** Clave para la lista blanca de habilidades (lista de strings). */
    public static final String ABILITY = "ability";
}
