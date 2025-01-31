package dev.spaxter.pixeltasktypes;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.tasktype.TaskTypeManager;

import dev.spaxter.pixeltasktypes.tasks.CatchTaskType;
import dev.spaxter.pixeltasktypes.tasks.DefeatTaskType;
import dev.spaxter.pixeltasktypes.tasks.EvolveTaskType;
import dev.spaxter.pixeltasktypes.tasks.FishingTaskType;
import dev.spaxter.pixeltasktypes.tasks.HatchEggTaskType;
import dev.spaxter.pixeltasktypes.util.Resources;

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
        PixelTaskTypes.ART = Resources.readAsString(this.getResource("art.txt"));

        this.getLogger().info("\n" + PixelTaskTypes.ART);
        this.questsApi = (BukkitQuestsPlugin) this.getServer().getPluginManager().getPlugin("Quests");

        this.registerEvents();
    }

    private void registerEvents() {
        TaskTypeManager taskTypeManager = this.questsApi.getTaskTypeManager();
        taskTypeManager.registerTaskType(new CatchTaskType(this));
        taskTypeManager.registerTaskType(new DefeatTaskType(this));
        taskTypeManager.registerTaskType(new EvolveTaskType(this));
        taskTypeManager.registerTaskType(new HatchEggTaskType(this));
        taskTypeManager.registerTaskType(new FishingTaskType(this));
    }

    public BukkitQuestsPlugin getQuestsApi() {
        return this.questsApi;
    }
}
