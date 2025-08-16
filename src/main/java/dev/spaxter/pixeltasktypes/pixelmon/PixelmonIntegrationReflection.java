package dev.spaxter.pixeltasktypes.pixelmon;

import org.bukkit.plugin.java.JavaPlugin;
import dev.spaxter.pixeltasktypes.PixelTaskTypes;

/**
 * Clase encargada de inicializar la integración Pixelmon via reflection.
 * - Inicializa PixelmonBridge
 * - Registra listeners/handlers que traduzcan eventos Pixelmon en llamadas
 *   accionables para los task types (sin exponer tipos Pixelmon en las firmas)
 * - Finalmente solicita al Main registrar las task types que dependen de Pixelmon
 */
public final class PixelmonIntegrationReflection {
    private PixelmonIntegrationReflection() {}

    public static void init(JavaPlugin plugin) {
        try {
            // Inicializar bridge (localiza clases Pixelmon via reflection)
            PixelmonBridge.init(plugin);

            if (!PixelmonBridge.isAvailable()) {
                plugin.getLogger().warning("[PixelmonIntegrationReflection] PixelmonBridge no disponible tras init.");
                return;
            }

            // TODO: aquí puedes registrar listeners Pixelmon específicos que conviertan
            // eventos Pixelmon a llamadas sobre tus task types. Ejemplo:
            // - Registrar un listener que capture el evento "EntityCaught" de Pixelmon,
            //   extraiga UUID/species/level y llame a una API neutral de tu TaskType.

            // Registrar task types que dependen de Pixelmon (ahora que bridge está listo)
            if (plugin instanceof PixelTaskTypes) {
                ((PixelTaskTypes) plugin).registerPixelmonDependentTaskTypes();
            }

            plugin.getLogger().info("[PixelmonIntegrationReflection] Pixelmon integration complete.");
        } catch (Throwable t) {
            plugin.getLogger().warning("[PixelmonIntegrationReflection] Error inicializando integración: " + t.getMessage());
            t.printStackTrace();
        }
    }
}
