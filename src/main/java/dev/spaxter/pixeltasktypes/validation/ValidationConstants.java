package dev.spaxter.pixeltasktypes.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Validation constants that (optionally) depend on Pixelmon registries.
 *
 * All fields are initialized safely: if Pixelmon (o su registries) fail to
 * initialise, the corresponding constant will be left as {@code null} to
 * indicate "validation data not available".
 *
 * Replace the placeholder population code inside the try-blocks with the real
 * Pixelmon API calls if/when you know them.
 */
public final class ValidationConstants {

    public static final List<String> SPECIES;
    public static final List<String> POKEMON_TYPES;
    public static final List<String> PALETTES;
    public static final List<String> POKE_BALLS;
    public static final List<String> MOVE_NAMES;
    public static final List<String> ROD_TYPES;
    public static final List<String> EVOLUTION_TYPES;

    static {
        List<String> speciesTmp = null;
        List<String> pokemonTypesTmp = null;
        List<String> palettesTmp = null;
        List<String> pokeBallsTmp = null;
        List<String> moveNamesTmp = null;
        List<String> rodTypesTmp = null;
        List<String> evolutionTypesTmp = null;

        try {
            /*
             * TODO: replace the following placeholder initializers with real Pixelmon
             * registry reads. They are intentionally commented / left as simple empty lists
             * so that if Pixelmon APIs or registries throw during class init, we don't
             * crash plugin startup.
             *
             * Example (pseudo-code; replace with actual API):
             *
             * speciesTmp = PixelmonSpeciesRegistry.getAll().stream()
             *      .map(s -> s.getName().toLowerCase())
             *      .collect(Collectors.toList());
             *
             * pokeBallsTmp = PixelmonBallRegistry.getAll().stream()
             *      .map(b -> b.getName().toLowerCase())
             *      .collect(Collectors.toList());
             *
             * If you cannot call the Pixelmon APIs safely here, consider lazy-loading
             * via a getter that attempts the registry read when first requested.
             */
            //
            // Placeholder safe initialization (empty lists). If you prefer "no validation"
            // when Pixelmon isn't present, set these to null instead.
            //
            // NOTE: we set them to null here (meaning "not available") to allow the
            // validator code to skip validation when lists are unavailable (recommended).
            //
            speciesTmp = null;
            pokemonTypesTmp = null;
            palettesTmp = null;
            pokeBallsTmp = null;
            moveNamesTmp = null;
            rodTypesTmp = null;
            evolutionTypesTmp = null;

        } catch (final Throwable t) {
            // Pixelmon not available or registry initialization failed — fail softly.
            System.err.println("Warning: could not load Pixelmon validation data: " + t);
            speciesTmp = null;
            pokemonTypesTmp = null;
            palettesTmp = null;
            pokeBallsTmp = null;
            moveNamesTmp = null;
            rodTypesTmp = null;
            evolutionTypesTmp = null;
        }

        // Assign to public finals (keep null if unavailable)
        SPECIES = (speciesTmp == null) ? null : Collections.unmodifiableList(new ArrayList<>(speciesTmp));
        POKEMON_TYPES = (pokemonTypesTmp == null) ? null : Collections.unmodifiableList(new ArrayList<>(pokemonTypesTmp));
        PALETTES = (palettesTmp == null) ? null : Collections.unmodifiableList(new ArrayList<>(palettesTmp));
        POKE_BALLS = (pokeBallsTmp == null) ? null : Collections.unmodifiableList(new ArrayList<>(pokeBallsTmp));
        MOVE_NAMES = (moveNamesTmp == null) ? null : Collections.unmodifiableList(new ArrayList<>(moveNamesTmp));
        ROD_TYPES = (rodTypesTmp == null) ? null : Collections.unmodifiableList(new ArrayList<>(rodTypesTmp));
        EVOLUTION_TYPES = (evolutionTypesTmp == null) ? null : Collections.unmodifiableList(new ArrayList<>(evolutionTypesTmp));
    }

    private ValidationConstants() {
        // no instances
    }

    /* Convenience checks so callers can test availability before using lists. */
    public static boolean hasSpecies() {
        return SPECIES != null && !SPECIES.isEmpty();
    }

    public static boolean hasPokemonTypes() {
        return POKEMON_TYPES != null && !POKEMON_TYPES.isEmpty();
    }

    public static boolean hasPalettes() {
        return PALETTES != null && !PALETTES.isEmpty();
    }

    public static boolean hasPokeBalls() {
        return POKE_BALLS != null && !POKE_BALLS.isEmpty();
    }

    public static boolean hasMoveNames() {
        return MOVE_NAMES != null && !MOVE_NAMES.isEmpty();
    }

    public static boolean hasRodTypes() {
        return ROD_TYPES != null && !ROD_TYPES.isEmpty();
    }

    public static boolean hasEvolutionTypes() {
        return EVOLUTION_TYPES != null && !EVOLUTION_TYPES.isEmpty();
    }
}
