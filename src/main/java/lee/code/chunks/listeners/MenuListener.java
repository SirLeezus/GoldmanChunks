package lee.code.chunks.listeners;

import lee.code.chunks.Data;
import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.menusystem.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class MenuListener implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {

        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof Menu menu) {
            e.setCancelled(true);

            GoldmanChunks plugin = GoldmanChunks.getPlugin();
            Data data = plugin.getData();
            PU pu = plugin.getPU();
            Player player = (Player) e.getWhoClicked();
            UUID uuid = player.getUniqueId();

            if (data.hasPlayerClickDelay(uuid)) return;
            else pu.addPlayerClickDelay(uuid);

            menu.handleMenu(e);
        }
    }
}