package dev.spaxter.pixeltasktypes.api.condition;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.enums.EnumAbilitySlot;

import java.util.List;

/**
 * Clase que centraliza todas las evaluaciones de condiciones sobre un Pokémon:
 * - si es Ultra Beast (UB)
 * - si tiene habilidad oculta (HA)
 * - si su habilidad está dentro de una whitelist
 */
public class PokemonConditions {

    /** Devuelve true si el Pokémon es Ultra Beast. */
    public static boolean isUltraBeast(Pokemon pokemon) {
        return pokemon.isUltraBeast();
    }

    /** Devuelve true si el Pokémon tiene habilidad oculta (Hidden Ability). */
    public static boolean hasHiddenAbility(Pokemon pokemon) {
        return pokemon.getAbilitySlot() == EnumAbilitySlot.HIDDEN;
    }

    /**
     * Devuelve true si la habilidad del Pokémon está en la lista de allowedAbilities.
     * Si la lista es null o está vacía, se considera que no hay filtro por habilidad.
     */
    public static boolean hasAbility(Pokemon pokemon, List<String> allowedAbilities) {
        if (allowedAbilities == null || allowedAbilities.isEmpty()) {
            return true;
        }
        String pokeAbilityName = pokemon.getAbility().getName();
        return allowedAbilities.contains(pokeAbilityName);
    }

    /**
     * Comprueba todas las condiciones:
     * - UB (si requireUB == true)
     * - HA (si requireHA == true)
     * - Lista de habilidades (si la lista no está vacía)
     *
     * Retorna false en la primera condición que falle.
     */
    public static boolean isValid(Pokemon pokemon, boolean requireUB, boolean requireHA, List<String> allowedAbilities) {
        if (requireUB && !isUltraBeast(pokemon)) {
            return false;
        }
        if (requireHA && !hasHiddenAbility(pokemon)) {
            return false;
        }
        if (!hasAbility(pokemon, allowedAbilities)) {
            return false;
        }
        return true;
    }
}
