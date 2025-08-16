package dev.spaxter.pixeltasktypes.pixelmon;

import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Bridge que encapsula accesos a la API de Pixelmon mediante reflection.
 * Todas las tareas deben usar este bridge en vez de importar com.pixelmon.*.
 */
public final class PixelmonBridge {
    private static boolean available = false;
    private static Class<?> entityPixelmonClass = null;
    // cache de métodos/fiels si necesitas

    private PixelmonBridge() {}

    public static void init(JavaPlugin plugin) {
        try {
            // Clase ejemplo; adapta si tu versión de Pixelmon usa otro nombre
            entityPixelmonClass = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon");
            // aquí puedes cachear métodos frecuentes, por ejemplo:
            // getSpeciesMethod = entityPixelmonClass.getMethod("getSpecies"); etc.

            available = true;
            plugin.getLogger().info("[PixelmonBridge] Inicializado: EntityPixelmon detectada.");
        } catch (ClassNotFoundException e) {
            available = false;
            plugin.getLogger().warning("[PixelmonBridge] Pixelmon classes not found.");
        } catch (Throwable t) {
            available = false;
            plugin.getLogger().warning("[PixelmonBridge] Error inicializando bridge: " + t.getMessage());
            t.printStackTrace();
        }
    }

    public static boolean isAvailable() {
        return available;
    }

    /**
     * Comprueba si el objeto dado es una instancia de EntityPixelmon (por reflection).
     */
    public static boolean isPixelmonEntity(Object obj) {
        if (!available || obj == null) return false;
        return entityPixelmonClass.isInstance(obj);
    }

    /**
     * Ejemplo: obtener nombre de especie vía reflection.
     * Debes adaptar el nombre del método y su tratamiento a la API exacta de tu Pixelmon.
     */
    public static Optional<String> getSpeciesName(Object pixelmonEntity) {
        if (!available || pixelmonEntity == null) return Optional.empty();
        try {
            // Este método es ilustrativo: ajusta a la API correcta de tu versión
            Method m = entityPixelmonClass.getMethod("getSpecies"); // <<— adaptar
            Object species = m.invoke(pixelmonEntity);
            return species == null ? Optional.empty() : Optional.of(species.toString());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
