package lee.code.mychunks.listeners;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.files.defaults.Lang;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ChunkCheckerListener implements Listener {

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        MyChunks plugin = MyChunks.getPlugin();

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            ItemStack handItem = player.getInventory().getItemInMainHand();

            if (handItem.getType().equals(Material.STICK)) {

                if (plugin.getData().getPlayerClickDelay(uuid)) return;
                else plugin.getUtility().addPlayerClickDelay(uuid);

                Chunk chunk = e.getClickedBlock().getLocation().getChunk();
                String chunkCord = plugin.getUtility().formatChunk(chunk);
                String owner = plugin.getSqLite().getChunkOwner(chunkCord);

                player.sendMessage(Lang.COMMAND_INFO_HEADER.getConfigValue(null));
                player.sendMessage("");
                player.sendMessage(Lang.COMMAND_INFO_LINE_1.getConfigValue(new String[] { owner }));
                player.sendMessage(Lang.COMMAND_INFO_LINE_2.getConfigValue(new String[] { chunkCord }));
                player.sendMessage("");
                player.sendMessage(Lang.COMMAND_INFO_FOOTER.getConfigValue(null));

                plugin.getUtility().renderChunkBorder(player, chunk, "info");
            }
        }
    }
}
