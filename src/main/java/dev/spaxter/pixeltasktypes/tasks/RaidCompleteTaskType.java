package dev.spaxter.pixeltasktypes.tasks;

import com.pixelmonmod.pixelmon.api.events.BeatWildPixelmonEvent;
import dev.spaxter.pixeltasktypes.PixelTaskTypes;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RaidCompleteTaskType extends AbstractTaskType implements Listener {

    public static final String ID = "raid_complete";

    public RaidCompleteTaskType(PixelTaskTypes plugin) {
        super(ID);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onRaidDefeat(BeatWildPixelmonEvent event) {
        // Solo si es un jefe de raid
        if (!event.pokemon.getTags().contains("pixelmon:raid_boss")) return;

        ServerPlayerEntity serverPlayer = event.player;
        Player player = serverPlayer.getBukkitEntity();
        increment(player);
    }
}
