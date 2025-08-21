package dev.spaxter.pixeltasktypes;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.tasktype.TaskTypeManager;

import dev.spaxter.pixeltasktypes.tasks.CatchTaskType;
import dev.spaxter.pixeltasktypes.tasks.CleanFossilTaskType;
import dev.spaxter.pixeltasktypes.tasks.DefeatTaskType;
import dev.spaxter.pixeltasktypes.tasks.EvolveTaskType;
import dev.spaxter.pixeltasktypes.tasks.FishingTaskType;
import dev.spaxter.pixeltasktypes.tasks.HatchEggTaskType;
import dev.spaxter.pixeltasktypes.tasks.MoveTaskType;
import dev.spaxter.pixeltasktypes.util.Resources;
import dev.spaxter.pixeltasktypes.tasks.PixelmonTaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Pixel Task Types main class.
 *
 * @author Spaxter
 */
public final class PixelTaskTypes extends JavaPlugin {
    public static String ART;
    public static Logger logger;

    private BukkitQuestsPlugin questsApi;

    // Lista para almacenar las instancias de task types que requieren integración Pixelmon
    private final List<PixelmonTaskType> pixelmonTaskTypes = new ArrayList<>();

    @Override
    public void onEnable() {
        logger = this.getLogger();

        try {
            if (this.getResource("art.txt") != null) {
                PixelTaskTypes.ART = Resources.readAsString(this.getResource("art.txt"));
            } else {
                PixelTaskTypes.ART = "";
                logger.warning("art.txt resource not found");
            }
        } catch (final Exception ex) {
            PixelTaskTypes.ART = "";
            logger.warning("Failed to load art.txt: " + ex.getMessage());
        }

        if (!this.checkArclight()) {
            logger.warning(
                "This server does not seem to be running Arclight Forge. PixelTaskTypes will most likely not work.");
        }
        if (!this.checkPixelmon()) {
            logger.warning(
                "This server does not seem to have the Pixelmon Mod installed. PixelTaskTypes will not work without it.");
        }

        this.getLogger().info("\n" + (PixelTaskTypes.ART == null ? "" : PixelTaskTypes.ART));

        final Object plugin = this.getServer() == null ? null : this.getServer().getPluginManager() == null
            ? null
            : this.getServer().getPluginManager().getPlugin("Quests");

        if (plugin instanceof BukkitQuestsPlugin) {
            this.questsApi = (BukkitQuestsPlugin) plugin;
        } else {
            this.questsApi = null;
        }

        if (this.questsApi == null) {
            logger.warning("Quests plugin not found or not a BukkitQuestsPlugin. PixelTaskTypes will not register task types.");
            return;
        }

        // *** Registrar task types INMEDIATAMENTE (antes de que Quests cierre registros) ***
        this.registerEvents();

        // *** Luego, de forma diferida, intentar la integración con Pixelmon/Arclight ***
        this.getServer().getScheduler().runTaskLater(this, () -> {
            try {
                // Intentamos inicializar Pixelmon (ejecuta <clinit> si está presente)
                try {
                    Class.forName("com.pixelmonmod.pixelmon.Pixelmon", true, getClassLoader());
                } catch (ClassNotFoundException e) {
                    logger.warning("Pixelmon is not present (or failed to initialize). PixelTaskTypes will skip Pixelmon integrations.");
                    return;
                } catch (Throwable t) {
                    logger.warning("Unexpected error while initializing Pixelmon: " + t);
                    return;
                }

                // Llamar al hook de integración en cada task type guardado
                for (PixelmonTaskType t : pixelmonTaskTypes) {
                    try {
                        t.registerPixelmonIntegration();
                    } catch (Throwable tt) {
                        logger.warning("Failed to register Pixelmon integration for task: " + t.getClass().getSimpleName() + " -> " + tt);
                    }
                }
                logger.info("PixelTaskTypes: Pixelmon integration attempts finished.");
            } catch (final Throwable e) {
                logger.log(java.util.logging.Level.SEVERE, "Deferred Pixelmon integration failed", e);
            }
        }, 20L);
    }

    private void registerEvents() {
        Objects.requireNonNull(this.questsApi, "questsApi must not be null");

        final TaskTypeManager taskTypeManager = Objects.requireNonNull(this.questsApi.getTaskTypeManager(),
            "TaskTypeManager must not be null");

        // Crea las instancias y almacenalas en la lista ANTES de registrar
        CatchTaskType catchTask = new CatchTaskType(this);
        pixelmonTaskTypes.add(catchTask);
        taskTypeManager.registerTaskType(catchTask);

        CleanFossilTaskType clean = new CleanFossilTaskType(this);
        pixelmonTaskTypes.add(clean);
        taskTypeManager.registerTaskType(clean);

        DefeatTaskType defeat = new DefeatTaskType(this);
        pixelmonTaskTypes.add(defeat);
        taskTypeManager.registerTaskType(defeat);

        EvolveTaskType evolve = new EvolveTaskType(this);
        pixelmonTaskTypes.add(evolve);
        taskTypeManager.registerTaskType(evolve);

        FishingTaskType fishing = new FishingTaskType(this);
        pixelmonTaskTypes.add(fishing);
        taskTypeManager.registerTaskType(fishing);

        HatchEggTaskType hatch = new HatchEggTaskType(this);
        pixelmonTaskTypes.add(hatch);
        taskTypeManager.registerTaskType(hatch);

        MoveTaskType move = new MoveTaskType(this);
        pixelmonTaskTypes.add(move);
        taskTypeManager.registerTaskType(move);
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
}
