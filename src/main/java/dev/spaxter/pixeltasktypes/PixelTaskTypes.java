package dev.spaxter.pixeltasktypes;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.tasktype.TaskTypeManager;

import dev.spaxter.pixeltasktypes.tasks.CatchTaskType;
import dev.spaxter.pixeltasktypes.tasks.DefeatTaskType;
import dev.spaxter.pixeltasktypes.util.Resources;

public final class PixelTaskTypes extends JavaPlugin {

    public static String ART;
    public static Logger logger;

    private BukkitQuestsPlugin questsApi;

    @Override
    public void onEnable() {

        this.getLogger().info("\n" + PixelTaskTypes.ART);
        this.questsApi = (BukkitQuestsPlugin) this.getServer().getPluginManager().getPlugin("Quests");

        this.registerEvents();
    }

    @Override
    public void onDisable() {

    }

    private void registerEvents() {
        TaskTypeManager taskTypeManager = this.questsApi.getTaskTypeManager();
        taskTypeManager.registerTaskType(new CatchTaskType(this));
        taskTypeManager.registerTaskType(new DefeatTaskType(this));
    }

    public BukkitQuestsPlugin getQuestsApi() {
        return this.questsApi;
    }
}
