package com.platanov3rde.pixeltasktypes.tasktypes;

import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import com.platanov3rde.pixeltasktypes.api.TaskProgress;
import com.platanov3rde.pixeltasktypes.api.TaskType;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RaidCompleteTaskType implements TaskType, Listener {
    public static final String ID = "raid_complete";

    public RaidCompleteTaskType() {
        // Registrar listener
        Bukkit.getPluginManager().registerEvents(this, YourPlugin.getInstance());
    }

    @Override
    public String getId() {
        return ID;
    }

    @EventHandler
    public void onRaidDefeat(BeatWildPixelmonEvent e) {
        // Solo interesan a los jefes de raid
        if (!e.getPokemon().getContext().tags().contains("pixelmon:raid_boss")) return;
        Player bukkitPlayer = (Player) ((ServerPlayerEntity) e.getPlayer()).getBukkitEntity();
        TaskProgress prog = TaskProgress.of(bukkitPlayer, ID);
        if (prog == null || prog.isComplete()) return;

        prog.increment(1);
        if (prog.getProgress() >= prog.getRequired()) {
            prog.complete();
        }
    }
}
