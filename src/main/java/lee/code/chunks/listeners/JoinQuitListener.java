package lee.code.chunks.listeners;

import lee.code.chunks.GoldmanChunks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class JoinQuitListener implements Listener {

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        UUID uuid = e.getPlayer().getUniqueId();
        if (!plugin.getSqLite().hasPlayerData(uuid)) plugin.getSqLite().createPlayerDataTable(uuid);
    }

    @EventHandler
    public void onQuitEvent(PlayerQuitEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        UUID uuid = e.getPlayer().getUniqueId();
        if (plugin.getData().isPlayerAutoClaiming(uuid)) plugin.getData().removePlayerAutoClaim(uuid);
    }
}
