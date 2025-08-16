package dev.spaxter.pixeltasktypes;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.tasktype.TaskTypeManager;
import dev.spaxter.pixeltasktypes.tasks.*;
import dev.spaxter.pixeltasktypes.util.Resources;
import dev.spaxter.pixeltasktypes.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main del plugin. Gestiona registro de task types en dos grupos:
 *  - base (no dependen de Pixelmon) -> se registran siempre si Quests está.
 *  - pixelmon-dependent -> se registran únicamente cuando Pixelmon está listo.
 *
 * También delega integración Pixelmon a PixelmonIntegrationReflection (reflection).
 */
public final class PixelTaskTypes extends JavaPlugin {
    public static String ART;
    public static Logger logger;

    // Instancia de la API de Quests (puede ser null si Quests no está presente)
    private BukkitQuestsPlugin questsApi;

    @Override
    public void onEnable() {
        logger = this.getLogger();

        // Guardar config por defecto si no existe
        saveDefaultConfig();

        // Cargar arte ASCII (opcional)
        loadArt();

        // Registrar watcher para reintentar cuando Quests o Pixelmon se habiliten
        Bukkit.getPluginManager().registerEvents(new PluginWatcher(this), this);

        // Comprobaciones informativas (no abortan la carga)
        if (!checkArclight()) {
            logger.warning("This server does not seem to be running Arclight Forge. PixelTaskTypes may not work fully.");
        }
        if (!checkPixelmon()) {
            logger.warning("Pixelmon mod not detected. PixelTaskTypes' Pixelmon features will be disabled until Pixelmon is present.");
        }

        // Intentamos inicializar Quests si ya está presente (no aborta si no lo está)
        initQuestsAPI();

        // Intentar registrar TaskTypes base ahora (si Quests está listo)
        Bukkit.getScheduler().runTask(this, this::attemptRegisterQuests);

        // Intentar integración con Pixelmon (si está presente)
        Bukkit.getScheduler().runTask(this, this::enablePixelmonIntegrationIfAvailable);
    }

    private void loadArt() {
        try {
            ART = Resources.readAsString(this.getResource("art.txt"));
            if (ART != null && !ART.isEmpty()) {
                logger.info("\n" + ART);
            }
        } catch (Exception e) {
            ART = "";
            logger.log(Level.WARNING, "Failed to load ASCII art", e);
        }
    }

    /**
     * Intenta inicializar la referencia a Quests (no aborta si no está).
     */
    private void initQuestsAPI() {
        try {
            Plugin p = getServer().getPluginManager().getPlugin("Quests");
            if (p instanceof BukkitQuestsPlugin) {
                this.questsApi = (BukkitQuestsPlugin) p;
                logger.info("Quests detected and API cached.");
            } else {
                this.questsApi = null;
                logger.info("Quests not present (will register base task types when available).");
            }
        } catch (Throwable e) {
            this.questsApi = null;
            logger.log(Level.WARNING, "Failed to detect Quests plugin at startup (will wait).", e);
        }
    }

    /**
     * Método público para que otras clases (task types) obtengan la API de Quests.
     * Puede devolver null si Quests no está presente en este momento.
     */
    public BukkitQuestsPlugin getQuestsApi() {
        return this.questsApi;
    }

    /**
     * Intenta registrar las task types base (no dependientes de Pixelmon).
     * Si Quests no está aún, sale y el PluginWatcher reintentará.
     */
    public void attemptRegisterQuests() {
        // Si no hay instancia cached, intentar obtenerla de nuevo
        if (this.questsApi == null) {
            Plugin p = getServer().getPluginManager().getPlugin("Quests");
            if (p instanceof BukkitQuestsPlugin) {
                this.questsApi = (BukkitQuestsPlugin) p;
            }
        }

        if (this.questsApi == null) {
            logger.info("[PixelTaskTypes] Quests no activo todavía — esperar a que se habilite.");
            return;
        }

        // Registrar únicamente las tasks base (no-Pixelmon)
        registerBaseTaskTypes();
    }

    /**
     * Registra task types que NO dependen de Pixelmon.
     */
    public void registerBaseTaskTypes() {
        try {
            if (this.questsApi == null) return;
            TaskTypeManager taskTypeManager = this.questsApi.getTaskTypeManager();

            // Ejemplo: registrar MoveTaskType que no depende de Pixelmon
            // Ajusta la lista a tus task types neutrales reales
            taskTypeManager.registerTaskType(new MoveTaskType(this));

            logger.info("[PixelTaskTypes] Registered base (non-Pixelmon) task types.");
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Error registering base task types", t);
        }
    }

    /**
     * Registra las task types que DEPENDEN de Pixelmon.
     * Este método lo invoca PixelmonIntegrationReflection.init() después de inicializar el bridge.
     */
    public void registerPixelmonDependentTaskTypes() {
        try {
            if (this.questsApi == null) {
                logger.warning("[PixelTaskTypes] Quests API missing when trying to register Pixelmon-dependent tasks.");
                return;
            }
            TaskTypeManager taskTypeManager = this.questsApi.getTaskTypeManager();

            // Registrar task types que requieren Pixelmon (ejemplos)
            taskTypeManager.registerTaskType(new CatchTaskType(this));
            taskTypeManager.registerTaskType(new CleanFossilTaskType(this));
            taskTypeManager.registerTaskType(new DefeatTaskType(this));
            taskTypeManager.registerTaskType(new EvolveTaskType(this));
            taskTypeManager.registerTaskType(new FishingTaskType(this));
            taskTypeManager.registerTaskType(new HatchEggTaskType(this));

            logger.info("[PixelTaskTypes] Registered Pixelmon-dependent task types.");
        } catch (IllegalStateException ex) {
            logger.warning("[PixelTaskTypes] Fuera de la ventana de registro; reintentando en el siguiente tick.");
            Bukkit.getScheduler().runTask(this, this::registerPixelmonDependentTaskTypes);
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Failed to register Pixelmon-dependent task types", t);
        }
    }

    /**
     * Integración con Pixelmon: SOLO se activa si la clase principal existe.
     * Toda la lógica concreta debe ir en PixelmonIntegrationReflection (reflection).
     */
    public void enablePixelmonIntegrationIfAvailable() {
        final String pixelmonFQCN = "com.pixelmonmod.pixelmon.Pixelmon";
        if (!ReflectionUtils.classExists(pixelmonFQCN)) {
            logger.info("[PixelTaskTypes] Pixelmon not detected at startup; pixelmon integration skipped for now.");
            return;
        }

        try {
            logger.info("[PixelTaskTypes] Pixelmon detected — initializing integration via reflection.");
            dev.spaxter.pixeltasktypes.pixelmon.PixelmonIntegrationReflection.init(this);
        } catch (Throwable t) {
            logger.log(Level.WARNING, "Error initializing Pixelmon integration (will not disable plugin)", t);
        }
    }

    /**
     * Comprueba Arclight (solo informativo)
     */
    private boolean checkArclight() {
        try {
            Class.forName("net.minecraftforge.common.MinecraftForge");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Comprueba Pixelmon (solo informativo)
     */
    private boolean checkPixelmon() {
        try {
            Class.forName("com.pixelmonmod.pixelmon.Pixelmon");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
