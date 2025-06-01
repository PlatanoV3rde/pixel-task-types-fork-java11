package dev.spaxter.pixeltasktypes;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.tasktype.TaskTypeManager;
import dev.spaxter.pixeltasktypes.tasks.*;
import dev.spaxter.pixeltasktypes.util.Resources;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class PixelTaskTypes extends JavaPlugin {
    public static String ART;
    public static Logger logger;

    private BukkitQuestsPlugin questsApi;

    @Override
    public void onEnable() {
        logger = this.getLogger();
        
        // Cargar arte ASCII
        loadArt();
        
        // Verificar dependencias críticas
        if (!checkDependencies()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Inicializar API de Quests
        if (!initQuestsAPI()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Registrar todos los tipos de tareas
        registerTaskTypes();
    }

    private void loadArt() {
        try {
            ART = Resources.readAsString(this.getResource("art.txt"));
            if (!ART.isEmpty()) {
                logger.info("\n" + ART);
            }
        } catch (Exception e) {
            ART = "";
            logger.log(Level.WARNING, "Failed to load ASCII art", e);
        }
    }

    private boolean checkDependencies() {
        boolean allOk = true;
        
        if (!checkArclight()) {
            logger.warning("This server does not seem to be running Arclight Forge. PixelTaskTypes will most likely not work.");
            allOk = false;
        }
        
        if (!checkPixelmon()) {
            logger.warning("This server does not seem to have the Pixelmon Mod installed. PixelTaskTypes will not work without it.");
            allOk = false;
        }
        
        return allOk;
    }

    private boolean initQuestsAPI() {
        try {
            this.questsApi = (BukkitQuestsPlugin) getServer().getPluginManager().getPlugin("Quests");
            if (this.questsApi == null) {
                logger.severe("Quests plugin not found! This plugin will not function without it.");
                return false;
            }
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize Quests API", e);
            return false;
        }
    }

    private void registerTaskTypes() {
        try {
            TaskTypeManager taskTypeManager = this.questsApi.getTaskTypeManager();
            
            taskTypeManager.registerTaskType(new CatchTaskType(this));
            taskTypeManager.registerTaskType(new CleanFossilTaskType(this));
            taskTypeManager.registerTaskType(new DefeatTaskType(this));
            taskTypeManager.registerTaskType(new EvolveTaskType(this));
            taskTypeManager.registerTaskType(new FishingTaskType(this));
            taskTypeManager.registerTaskType(new HatchEggTaskType(this));
            taskTypeManager.registerTaskType(new MoveTaskType(this));
            
            logger.info("Successfully registered all task types!");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to register task types", e);
        }
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