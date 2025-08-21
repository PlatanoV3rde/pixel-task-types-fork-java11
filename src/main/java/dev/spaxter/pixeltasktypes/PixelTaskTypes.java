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
import dev.spaxter.pixeltasktypes.validation.ValidationConstants;

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

    @Override
    public void onEnable() {
        logger = this.getLogger();

        try {
            // Defensive: getResource may return null or Resources.readAsString may throw
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

        // Attempt to retrieve the Quests plugin and fail-fast with a clear log if missing
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

        this.registerEvents();
    }

    private void registerEvents() {
        // Ensure questsApi is present (defensive check)
        Objects.requireNonNull(this.questsApi, "questsApi must not be null");

        final TaskTypeManager taskTypeManager = Objects.requireNonNull(this.questsApi.getTaskTypeManager(),
            "TaskTypeManager must not be null");

        taskTypeManager.registerTaskType(new CatchTaskType(this));
        taskTypeManager.registerTaskType(new CleanFossilTaskType(this));
        taskTypeManager.registerTaskType(new DefeatTaskType(this));
        taskTypeManager.registerTaskType(new EvolveTaskType(this));
        taskTypeManager.registerTaskType(new FishingTaskType(this));
        taskTypeManager.registerTaskType(new HatchEggTaskType(this));
        taskTypeManager.registerTaskType(new MoveTaskType(this));
    }

    public BukkitQuestsPlugin getQuestsApi() {
        return this.questsApi;
    }

    private boolean checkArclight() {
        try {
            Class.forName("net.minecraftforge.common.MinecraftForge");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private boolean checkPixelmon() {
        try {
            Class.forName("com.pixelmonmod.pixelmon.Pixelmon");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
