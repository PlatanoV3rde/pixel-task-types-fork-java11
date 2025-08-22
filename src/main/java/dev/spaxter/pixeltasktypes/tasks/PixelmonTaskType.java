package dev.spaxter.pixeltasktypes.tasks;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType;
import com.leonardobishop.quests.bukkit.util.TaskUtils;
import com.leonardobishop.quests.common.quest.Task;

import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import dev.spaxter.pixeltasktypes.util.QuestHelper;
import dev.spaxter.pixeltasktypes.validation.PixelmonTaskConfigValidator;
import dev.spaxter.pixeltasktypes.validation.ValidationConstants;

import io.izzel.arclight.api.Arclight;

import net.minecraftforge.eventbus.api.IEventBus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom {@link com.leonardobishop.quests.common.tasktype.TaskType} for Pixelmon based tasks.
 *
 * NOTE: this class avoids *compile-time* references to Pixelmon classes to prevent
 * premature classloading. All interactions with Pokémon objects are done via reflection
 * to ensure this class can be loaded even if Pixelmon is not yet present/initialized.
 */
public abstract class PixelmonTaskType extends BukkitTaskType {
    public final PixelTaskTypes plugin;
    public final BukkitQuestsPlugin questsApi;

    public PixelmonTaskType(PixelTaskTypes plugin, String name, String description) {
        super(name, "PixelTaskTypes", description);
        this.plugin = plugin;
        this.questsApi = this.plugin.getQuestsApi();

        // IMPORTANT: do NOT register Pixelmon event handlers here (constructor),
        // because that may trigger Pixelmon class initialization too early.
        // We'll register them in registerPixelmonIntegration() when Pixelmon is ready.

        // Validadores comunes (ValidationConstants may be null; validators handle it)
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
     * Register integration with Pixelmon/Arclight. Call ONLY when Pixelmon is
     * initialized (e.g. from a deferred task after server start).
     *
     * This implementation obtains Pixelmon.EVENT_BUS by reflection, casts it to
     * IEventBus (type-safe) and then calls Arclight.registerForgeEvent.
     */
    public void registerPixelmonIntegration() {
        try {
            // Obtain Pixelmon.EVENT_BUS reflectively so classloading happens only here.
            Class<?> pixelmonClass = Class.forName("com.pixelmonmod.pixelmon.Pixelmon");
            Object rawEventBus = pixelmonClass.getField("EVENT_BUS").get(null);

            if (rawEventBus instanceof IEventBus) {
                IEventBus eventBus = (IEventBus) rawEventBus;
                try {
                    Arclight.registerForgeEvent(null, eventBus, this);
                    plugin.getLogger().info("Registered Pixelmon event handlers for " + this.getClass().getSimpleName());
                } catch (Throwable t) {
                    plugin.getLogger().warning("Failed to register Pixelmon event handlers (Arclight call failed): " + t);
                }
            } else {
                plugin.getLogger().warning("Pixelmon EVENT_BUS is not an IEventBus instance; skipping event registration.");
            }
        } catch (ClassNotFoundException cnfe) {
            plugin.getLogger().warning("Pixelmon not present; skipping Pixelmon integrations.");
        } catch (Throwable t) {
            plugin.getLogger().warning("Failed to initialize Pixelmon event bus: " + t);
        }
    }

    // --- rest of your original logic, converted to use Object (reflection) ---

    /**
     * Validate a Pokemon object (reflection). `pokemonObj` is expected to be an instance
     * of com.pixelmonmod.pixelmon.api.pokemon.Pokemon, but declared as Object to avoid
     * compile-time dependency.
     */
    public boolean checkPokemon(final Object pokemonObj, final Task task) {
        if (pokemonObj == null) return false;

        final List<String> requiredTypes   = QuestHelper.getConfigStringListAsLowercase(task, "pokemon_types");
        final List<String> requiredSpecies = QuestHelper.getConfigStringListAsLowercase(task, "species");
        final List<String> notSpecies      = QuestHelper.getConfigStringListAsLowercase(task, "not_species");
        final List<String> requiredPalettes= QuestHelper.getConfigStringListAsLowercase(task, "palettes");
        final boolean legendaryOnly        = TaskUtils.getConfigBoolean(task, "legendary_only");
        final Integer requiredLevel        = (Integer) task.getConfigValue("pokemon_level");

        // Nivel mínimo
        if (requiredLevel != null) {
            Integer level = safeGetPokemonLevel(pokemonObj);
            if (level == null || level < requiredLevel) return false;
        }

        // Tipos
        if (requiredTypes != null && !this.checkType(pokemonObj, requiredTypes)) {
            return false;
        }
        // Especies
        if (requiredSpecies != null && !this.checkSpecies(pokemonObj, requiredSpecies)) {
            return false;
        }
        // Excluir ciertas especies
        if (notSpecies != null && this.checkSpecies(pokemonObj, notSpecies)) {
            return false;
        }
        // Paletas
        if (requiredPalettes != null && !this.checkPalettes(pokemonObj, requiredPalettes)) {
            return false;
        }
        // Solo legendarios
        if (legendaryOnly && !this.checkLegendary(pokemonObj)) {
            return false;
        }
        return true;
    }

    protected boolean checkPokemon(final String pokemonName, final Task task) {
        if (pokemonName == null || pokemonName.isEmpty()) return false;

        List<String> configSpecies = QuestHelper.getConfigStringListAsLowercase(task, "species");
        if (configSpecies == null || configSpecies.isEmpty()) {
            configSpecies = QuestHelper.getConfigStringListAsLowercase(task, "pokemon");
        }
        if (configSpecies == null || configSpecies.isEmpty()) {
            configSpecies = QuestHelper.getConfigStringListAsLowercase(task, "pokemons");
        }

        // Si no hay restricción de especie en la config -> aceptar cualquier Pokémon
        if (configSpecies == null || configSpecies.isEmpty()) {
            return true;
        }

        final String lowerName = pokemonName.toLowerCase();
        for (String allowed : configSpecies) {
            if (allowed == null) continue;
            final String allowedLower = allowed.toLowerCase();
            if (allowedLower.equals(lowerName)) return true;
        }
        return false;
    }

    private boolean checkType(final Object pokemonObj, final List<String> requiredTypes) {
        try {
            Object form = safeInvoke(pokemonObj, "getForm");
            if (form == null) return false;
            Object typesObj = safeInvoke(form, "getTypes");
            if (!(typesObj instanceof Iterable)) return false;

            List<String> types = new ArrayList<>();
            for (Object typeObj : (Iterable<?>) typesObj) {
                if (typeObj == null) continue;
                Object nameObj = safeInvoke(typeObj, "getName");
                if (nameObj != null) {
                    types.add(String.valueOf(nameObj).toLowerCase());
                }
            }

            for (String type : types) {
                if (requiredTypes.contains(type)) {
                    return true;
                }
            }
        } catch (Throwable e) {
            this.plugin.getLogger().warning("Error in checkType reflection: " + e);
            return false;
        }
        return false;
    }

    private boolean checkSpecies(final Object pokemonObj, final List<String> requiredSpecies) {
        try {
            Object speciesObj = safeInvoke(pokemonObj, "getSpecies");
            if (speciesObj == null) return false;
            Object nameObj = safeInvoke(speciesObj, "getName");
            if (nameObj == null) return false;
            String species = String.valueOf(nameObj).toLowerCase();
            return requiredSpecies.contains(species);
        } catch (Throwable e) {
            this.plugin.getLogger().warning("Error in checkSpecies reflection: " + e);
            return false;
        }
    }

    private boolean checkPalettes(final Object pokemonObj, final List<String> requiredPalettes) {
        try {
            Object paletteObj = safeInvoke(pokemonObj, "getPalette");
            if (paletteObj == null) return false;
            Object nameObj = safeInvoke(paletteObj, "getName");
            if (nameObj == null) return false;
            String palette = String.valueOf(nameObj).toLowerCase();
            return requiredPalettes.contains(palette);
        } catch (Throwable e) {
            this.plugin.getLogger().warning("Error in checkPalettes reflection: " + e);
            return false;
        }
    }

    private boolean checkLegendary(final Object pokemonObj) {
        try {
            Object speciesObj = safeInvoke(pokemonObj, "getSpecies");
            if (speciesObj == null) return false;
            Object isLegendaryObj = safeInvoke(speciesObj, "isLegendary");
            if (isLegendaryObj instanceof Boolean) {
                return (Boolean) isLegendaryObj;
            } else if (isLegendaryObj instanceof Number) {
                return ((Number) isLegendaryObj).intValue() != 0;
            }
            return false;
        } catch (Throwable e) {
            this.plugin.getLogger().warning("Error in checkLegendary reflection: " + e);
            return false;
        }
    }

    // Helper to fetch pokemon level (reflection)
    private Integer safeGetPokemonLevel(final Object pokemonObj) {
        try {
            Object lvlObj = safeInvoke(pokemonObj, "getPokemonLevel");
            if (lvlObj instanceof Number) return ((Number) lvlObj).intValue();
            return null;
        } catch (Throwable e) {
            this.plugin.getLogger().warning("Error reading pokemon level via reflection: " + e);
            return null;
        }
    }

    // Generic reflection utility: invoke no-arg method, return null on failure.
    private Object safeInvoke(final Object target, final String methodName, final Object... args) {
        if (target == null) return null;
        try {
            Class<?> cls = target.getClass();
            Method method;
            if (args == null || args.length == 0) {
                method = cls.getMethod(methodName);
                return method.invoke(target);
            } else {
                // attempt to infer parameter types
                Class<?>[] paramTypes = new Class<?>[args.length];
                for (int i = 0; i < args.length; i++) {
                    paramTypes[i] = args[i] == null ? Object.class : args[i].getClass();
                }
                method = cls.getMethod(methodName, paramTypes);
                return method.invoke(target, args);
            }
        } catch (NoSuchMethodException nsme) {
            // Try to find any method with the same name (more robust)
            for (Method m : target.getClass().getMethods()) {
                if (m.getName().equals(methodName) && m.getParameterCount() == (args == null ? 0 : args.length)) {
                    try {
                        return m.invoke(target, args);
                    } catch (Throwable ignored) { }
                }
            }
            return null;
        } catch (Throwable t) {
            // log and return null
            this.plugin.getLogger().finer("Reflection call failed for " + methodName + " on " +
                                         target.getClass().getName() + ": " + t);
            return null;
        }
    }
}
