package dev.spaxter.pixeltasktypes.pixelmon;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Aquí pones TODO el código relacionado con Pixelmon, pero usando reflection.
 * No importes com.pixelmon.* en este archivo; usa Class.forName / reflection.
 */
public final class PixelmonIntegrationReflection {
    private PixelmonIntegrationReflection() {}

    public static void init(JavaPlugin plugin) {
        try {
            Class<?> pixelmonMain = Class.forName("com.pixelmonmod.pixelmon.Pixelmon");
            plugin.getLogger().info("[PixelmonIntegrationReflection] Pixelmon encontrada: " + pixelmonMain.getName());

            // EJEMPLO: Si necesitas invocar métodos estáticos de Pixelmon:
            // Method m = pixelmonMain.getMethod("yourStaticMethodName", paramTypes...);
            // m.invoke(null, args...);

            // EJEMPLO: Si tienes listeners específicos que importaban Pixelmon,
            // reescríbelos aquí para que usen reflection o crea listeners que usen
            // Object en firmas y conviertan con reflection internamente.

            plugin.getLogger().info("[PixelmonIntegrationReflection] Integración inicializada via reflection.");
        } catch (ClassNotFoundException e) {
            plugin.getLogger().warning("[PixelmonIntegrationReflection] Pixelmon no encontrada (ClassNotFound).");
        } catch (Throwable t) {
            plugin.getLogger().warning("[PixelmonIntegrationReflection] Error en integración por reflection: " + t.getMessage());
            t.printStackTrace();
        }
    }
}
