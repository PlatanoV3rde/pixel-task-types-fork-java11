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

import java.io.InputStream;
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

        // Leer recurso "art.txt" desde el JAR
        InputStream artStream = this.getResource("art.txt");
        if (artStream != null) {
            ART = Resources.readAsString(artStream);
        } else {
            ART = "";
            logger.warning("No se encontró el recurso art.txt.");
        }

        if (!checkArclight()) {
            logger.warning(
                "Este servidor no parece estar ejecutando Arclight Forge. PixelTaskTypes podría no funcionar correctamente.");
        }
        if (!checkPixelmon()) {
            logger.warning(
                "Este servidor no parece tener instalado el mod Pixelmon. PixelTaskTypes no funcionará sin él.");
        }

        logger.info("\n" + ART);

        // Obtener la instancia de Quests
        if (this.getServer().getPluginManager().getPlugin("Quests") instanceof BukkitQuestsPlugin) {
            this.questsApi = (BukkitQuestsPlugin) this.getServer().getPluginManager().getPlugin("Quests");
        } else {
            this.questsApi = null;
            logger.warning("No se encontró el plugin Quests o no es compatible. Las tareas no se registrarán.");
        }

        registerEvents();
    }

    private void registerEvents() {
        if (this.questsApi == null) {
            // No hay Quests: no registrar tipos de tarea
            return;
        }

        TaskTypeManager taskTypeManager = this.questsApi.getTaskTypeManager();
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
