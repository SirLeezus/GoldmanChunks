package lee.code.mychunks.listeners;

import lee.code.mychunks.MyChunks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class JoinQuitListener implements Listener {

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e) {
        MyChunks plugin = MyChunks.getPlugin();
        UUID uuid = e.getPlayer().getUniqueId();
        if (!plugin.getSqLite().hasPlayerData(uuid)) plugin.getSqLite().createPlayerDataTable(uuid);
    }

    @EventHandler
    public void onQuitEvent(PlayerQuitEvent e) {
        MyChunks plugin = MyChunks.getPlugin();
        UUID uuid = e.getPlayer().getUniqueId();
        if (plugin.getData().isPlayerAutoClaiming(uuid)) plugin.getData().removePlayerAutoClaim(uuid);
    }
}
