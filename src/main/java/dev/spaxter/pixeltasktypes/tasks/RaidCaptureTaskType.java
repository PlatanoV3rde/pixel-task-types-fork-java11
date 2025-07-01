package dev.spaxter.pixeltasktypes.tasks;

import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RaidCaptureTaskType extends AbstractTaskType implements Listener {

    public static final String ID = "raid_capture";

    public RaidCaptureTaskType(PixelTaskTypes plugin) {
        super(ID);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onRaidCapture(CaptureEvent.SuccessfulRaidCapture event) {
        Player player = (Player) event.getPlayer();
        increment(player);
    }
}
