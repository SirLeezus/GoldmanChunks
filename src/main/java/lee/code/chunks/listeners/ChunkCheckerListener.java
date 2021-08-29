package lee.code.chunks.listeners;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.lists.RenderTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkCheckerListener implements Listener {

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            ItemStack handItem = player.getInventory().getItemInMainHand();

            if (handItem.getType().equals(Material.STICK)) {
                if (plugin.getData().hasPlayerClickDelay(uuid)) return;
                else plugin.getPU().addPlayerClickDelay(uuid);

                if (e.getClickedBlock() != null) {
                    Chunk chunk = e.getClickedBlock().getLocation().getChunk();
                    String chunkCord = plugin.getPU().formatChunkLocation(chunk);
                    String owner = "";

                    if (cache.isAdminChunk(chunkCord)) owner = plugin.getPU().format("&4&lAdmin");
                    else if (cache.isChunkClaimed(chunkCord)) owner = cache.getChunkOwnerName(chunkCord);

                    List<Component> lines = new ArrayList<>();

                    lines.add(Lang.COMMAND_INFO_HEADER.getComponent(null));
                    lines.add(Component.text(""));
                    lines.add(Lang.COMMAND_INFO_LINE_1.getComponent(new String[] { owner }));
                    lines.add(Lang.COMMAND_INFO_LINE_2.getComponent(new String[] { chunkCord }));
                    if (cache.isChunkClaimed(chunkCord)) if (cache.isChunkForSale(chunkCord)) lines.add(Lang.COMMAND_INFO_LINE_3.getComponent(new String[] { plugin.getPU().formatAmount(cache.getChunkPrice(chunkCord)) }));
                    lines.add(Component.text(""));
                    lines.add(Lang.COMMAND_INFO_FOOTER.getComponent(null));

                    for (Component line : lines) player.sendMessage(line);
                    plugin.getPU().renderChunkBorder(player, chunk, RenderTypes.INFO);
                }
            }
        }
    }
}
