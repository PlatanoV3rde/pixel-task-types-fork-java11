package com.platanov3rde.pixeltasktypes.tasktypes;

import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.platanov3rde.pixeltasktypes.api.TaskProgress;
import com.platanov3rde.pixeltasktypes.api.TaskType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RaidCaptureTaskType implements TaskType, Listener {
    public static final String ID = "raid_capture";

    public RaidCaptureTaskType() {
        Bukkit.getPluginManager().registerEvents(this, YourPlugin.getInstance());
    }

    @Override
    public String getId() {
        return ID;
    }

    @EventHandler
    public void onRaidCapture(CaptureEvent.SuccessfulRaidCapture e) {
        Player player = (Player) e.getPlayer();
        TaskProgress prog = TaskProgress.of(player, ID);
        if (prog == null || prog.isComplete()) return;

        prog.increment(1);
        if (prog.getProgress() >= prog.getRequired()) {
            prog.complete();
        }
    }
}
