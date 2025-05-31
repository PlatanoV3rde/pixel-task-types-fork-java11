package com.pixelmonmod.pixelutilities.tasks.conditions;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import java.util.List;
import java.util.ArrayList;

/**
 * Clase para manejar condiciones avanzadas de Pokémon
 * Compatible con Java 11 y el sistema de misiones existente
 */
public class PokemonCondition {
    private Boolean isUltraBeast = null;
    private Boolean hasHiddenAbility = null;
    private List<String> requiredAbilities = new ArrayList<>();
    private List<String> requiredPokeBalls = new ArrayList<>();

    // Constructor vacío para serialización
    public PokemonCondition() {}

    // Métodos para Ultraente (UB)
    public Boolean isUltraBeast() {
        return isUltraBeast;
    }

    public void setUltraBeast(Boolean ultraBeast) {
        this.isUltraBeast = ultraBeast;
    }

    // Métodos para Hidden Ability (HA)
    public Boolean hasHiddenAbility() {
        return hasHiddenAbility;
    }

    public void setHiddenAbility(Boolean hiddenAbility) {
        this.hasHiddenAbility = hiddenAbility;
    }

    // Métodos para lista de habilidades
    public List<String> getRequiredAbilities() {
        return new ArrayList<>(requiredAbilities); // Devuelve copia para inmutabilidad
    }

    public void setRequiredAbilities(List<String> abilities) {
        this.requiredAbilities = new ArrayList<>(abilities);
    }

    public void addRequiredAbility(String ability) {
        if (ability != null && !ability.isEmpty()) {
            this.requiredAbilities.add(ability);
        }
    }

    // Métodos para pokeballs (opcional)
    public List<String> getRequiredPokeBalls() {
        return new ArrayList<>(requiredPokeBalls);
    }

    public void setRequiredPokeBalls(List<String> pokeBalls) {
        this.requiredPokeBalls = new ArrayList<>(pokeBalls);
    }

    /**
     * Verifica si el Pokémon cumple con todas las condiciones especificadas
     * @param pokemon El Pokémon a verificar
     * @return true si cumple todas las condiciones, false de lo contrario
     */
    public boolean matches(Pokemon pokemon) {
        // Verificar condición Ultraente (UB)
        if (isUltraBeast != null && pokemon.isUltraBeast() != isUltraBeast) {
            return false;
        }

        // Verificar condición Hidden Ability (HA)
        if (hasHiddenAbility != null && pokemon.hasHiddenAbility() != hasHiddenAbility) {
            return false;
        }

        // Verificar lista de habilidades (whitelist)
        if (!requiredAbilities.isEmpty()) {
            String pokemonAbility = pokemon.getAbility().getName();
            if (requiredAbilities.stream().noneMatch(ability -> 
                ability.equalsIgnoreCase(pokemonAbility))) {
                return false;
            }
        }

        // Verificar pokeballs (si se especificaron)
        if (!requiredPokeBalls.isEmpty() && pokemon.getBallUsed() != null) {
            String ballUsed = pokemon.getBallUsed().getName();
            if (requiredPokeBalls.stream().noneMatch(ball -> 
                ball.equalsIgnoreCase(ballUsed))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Verifica si hay condiciones definidas
     * @return true si hay al menos una condición activa
     */
    public boolean hasConditions() {
        return isUltraBeast != null || 
               hasHiddenAbility != null || 
               !requiredAbilities.isEmpty() || 
               !requiredPokeBalls.isEmpty();
    }
}
