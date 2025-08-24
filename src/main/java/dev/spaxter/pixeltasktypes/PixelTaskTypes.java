package dev.spaxter.pixeltasktypes;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.tasktype.TaskTypeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Pixel Task Types main class — versión segura para evitar linking prematuro a Pixelmon.
 */
public final class PixelTaskTypes extends JavaPlugin {
    public static String ART;
    public static Logger logger;
    public static PixelTaskTypes plugin;

    private BukkitQuestsPlugin questsApi;

    /**
     * Lista para almacenar instancias de task types que requieren integración con Pixelmon.
     * Las instancias se mantienen como Object para evitar vincular tipos de Pixelmon en tiempo de compilación.
     */
    private final List<Object> pixelmonTaskTypes = new ArrayList<>();

    @Override
    public void onEnable() {
        logger = this.getLogger();
        plugin = this;

        // Detect presence of Arclight/Pixelmon early for logging (avoids IDE 'unused' warnings)
        if (checkArclight()) {
            logger.info("Arclight / Forge environment detected.");
        } else {
            logger.info("No Arclight / Forge environment detected.");
        }

        if (checkPixelmon()) {
            logger.info("Pixelmon appears to be present at startup (note: this does not guarantee full initialization).");
        } else {
            logger.info("Pixelmon not present at startup.");
        }

        // Intenta obtener la API de Quests (si está presente)
        try {
            final org.bukkit.plugin.Plugin raw = Bukkit.getPluginManager().getPlugin("Quests");
            if (raw instanceof BukkitQuestsPlugin) {
                this.questsApi = (BukkitQuestsPlugin) raw;
            } else {
                this.getLogger().warning("BukkitQuestsPlugin not found or not instance of BukkitQuestsPlugin.");
            }
        } catch (Throwable t) {
            this.getLogger().warning("Error while fetching Quests plugin: " + t);
        }

        // Registrar eventos / tipos (no dependientes de Pixelmon)
        this.registerEvents();

        // Runnable que intentará registrar integraciones de Pixelmon después (si Pixelmon está presente)
        final Runnable tryRegister = new Runnable() {
            private int attempts = 0;

            @Override
            public void run() {
                attempts++;
                try {
                    // Detectamos Pixelmon sin forzar su inicializador (<clinit>)
                    Class.forName("com.pixelmonmod.pixelmon.Pixelmon", false, getClassLoader());
                } catch (ClassNotFoundException e) {
                    plugin.getLogger().warning("Pixelmon is not present (attempt " + attempts + "); skipping Pixelmon integrations.");
                    return;
                } catch (Throwable t) {
                    plugin.getLogger().warning("Unexpected error while detecting Pixelmon (attempt " + attempts + "): " + t);
                    return;
                }

                // Llamar al hook de integración en cada task type guardado (método opcional por reflexión)
                for (Object t : pixelmonTaskTypes) {
                    try {
                        java.lang.reflect.Method hook = t.getClass().getMethod("registerPixelmonIntegration");
                        hook.invoke(t);
                    } catch (NoSuchMethodException ns) {
                        // No todas las tareas implementan el hook: ignorar.
                    } catch (Throwable tt) {
                        plugin.getLogger().warning("Failed to register Pixelmon integration for task: " + t.getClass().getName() + " -> " + tt);
                    }
                }

                plugin.getLogger().info("PixelTaskTypes: Pixelmon integration attempts finished.");
            }
        };

        // Programamos el intento inmediato y dos reintentos espaciados (usa 'plugin' que es JavaPlugin)
        Bukkit.getScheduler().runTask(plugin, tryRegister);
        Bukkit.getScheduler().runTaskLater(plugin, tryRegister, 20L); // +1s
        Bukkit.getScheduler().runTaskLater(plugin, tryRegister, 60L); // +3s
    }

    /**
     * Registra los task types. Para evitar enlazar Pixelmon en el arranque, usamos reflexión.
     */
    private void registerEvents() {
        Objects.requireNonNull(this.questsApi, "questsApi must not be null");

        final TaskTypeManager taskTypeManager = Objects.requireNonNull(
                this.questsApi.getTaskTypeManager(),
                "TaskTypeManager must not be null"
        );

        // Lista de clases FQCN que implementan task types dependientes de Pixelmon
        final String[] taskClassNames = new String[] {
                "dev.spaxter.pixeltasktypes.tasks.CatchTaskType",
                "dev.spaxter.pixeltasktypes.tasks.CleanFossilTaskType",
                "dev.spaxter.pixeltasktypes.tasks.DefeatTaskType",
                "dev.spaxter.pixeltasktypes.tasks.EvolveTaskType",
                "dev.spaxter.pixeltasktypes.tasks.FishingTaskType",
                "dev.spaxter.pixeltasktypes.tasks.HatchEggTaskType",
                "dev.spaxter.pixeltasktypes.tasks.MoveTaskType"
        };

        for (final String clsName : taskClassNames) {
            try {
                // Carga la clase sin ejecutar su <clinit>
                final Class<?> cls = Class.forName(clsName, false, getClassLoader());
                final java.lang.reflect.Constructor<?> ctor = cls.getConstructor(PixelTaskTypes.class);
                final Object instance = ctor.newInstance(this);
                // Guardar la instancia para posteriores integraciones con Pixelmon
                this.pixelmonTaskTypes.add(instance);
                // Registrar en TaskTypeManager — casteamos a la clase de Quests que se usa en tiempo de ejecución
                taskTypeManager.registerTaskType((com.leonardobishop.quests.bukkit.tasktype.BukkitTaskType) instance);
            } catch (ClassNotFoundException cnf) {
                logger.info("Task class not present (skipping): " + clsName);
            } catch (Throwable t) {
                logger.warning("Failed to register task type: " + clsName + " -> " + t);
            }
        }
        // Fin de registro de tasks.
    }

    public BukkitQuestsPlugin getQuestsApi() {
        return this.questsApi;
    }

    private boolean checkArclight() {
        try {
            Class.forName("net.minecraftforge.common.MinecraftForge", false, getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private boolean checkPixelmon() {
        try {
            Class.forName("com.pixelmonmod.pixelmon.Pixelmon", false, getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static PixelTaskTypes getInstance() {
        return plugin;
    }
}
