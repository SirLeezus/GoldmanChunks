package lee.code.chunks.listeners;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class JoinQuitListener implements Listener {

    @EventHandler
    public void onJoinEvent(PlayerJoinEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!cache.hasPlayerData(uuid)) cache.createPlayerData(uuid);
        else if (cache.isChunkFlying(uuid)) {
            player.setAllowFlight(true);
            player.setFlying(true);
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_FLY_TOGGLE_SUCCESSFUL.getComponent(new String[] { Lang.ON.getString() })));
        }
    }

    @EventHandler
    public void onQuitEvent(PlayerQuitEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();

        UUID uuid = e.getPlayer().getUniqueId();
        if (plugin.getData().isPlayerAutoClaiming(uuid)) plugin.getData().removePlayerAutoClaim(uuid);
    }
}
