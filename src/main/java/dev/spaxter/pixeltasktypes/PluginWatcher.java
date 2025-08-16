package dev.spaxter.pixeltasktypes;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Listener para detectar cuando Quests o Pixelmon se habilitan posteriormente.
 */
public class PluginWatcher implements Listener {
    private final JavaPlugin plugin;

    public PluginWatcher(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent e) {
        Plugin p = e.getPlugin();
        String name = p.getName();
        if ("Quests".equalsIgnoreCase(name) || "QuestsPlugin".equalsIgnoreCase(name)) {
            plugin.getLogger().info("[PixelTaskTypes] Quests habilitado — intentando registrar TaskTypes.");
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (plugin instanceof PixelTaskTypes) ((PixelTaskTypes) plugin).attemptRegisterQuests();
            });
        }

        if ("Pixelmon".equalsIgnoreCase(name) || "PixelmonReforged".equalsIgnoreCase(name)) {
            plugin.getLogger().info("[PixelTaskTypes] Pixelmon habilitado — intentando integración Pixelmon.");
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (plugin instanceof PixelTaskTypes) ((PixelTaskTypes) plugin).enablePixelmonIntegrationIfAvailable();
            });
        }
    }
}
