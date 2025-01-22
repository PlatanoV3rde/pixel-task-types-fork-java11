package dev.spaxter.pixeltasktypes.util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ArclightUtils {
    public static Player getBukkitPlayer(final UUID playerId) {
        return Bukkit.getServer().getPlayer(playerId);
    }
}
