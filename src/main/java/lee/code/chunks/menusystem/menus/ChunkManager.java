package lee.code.chunks.menusystem.menus;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.menusystem.Menu;
import lee.code.chunks.menusystem.PlayerMU;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public class ChunkManager extends Menu {

    public ChunkManager(PlayerMU pmu) {
        super(pmu);
    }

    @Override
    public Component getMenuName() {
        return Lang.MENU_CHUNK_MANAGER_TITLE.getComponent(null);
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        Player player = pmu.getOwner();

        if (plugin.getData().hasPlayerClickDelay(pmu.getOwner().getUniqueId())) return;
        else plugin.getPU().addPlayerClickDelay(pmu.getOwner().getUniqueId());
        if (e.getClickedInventory() == player.getInventory()) return;

        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);
        UUID uuid = player.getUniqueId();

        switch (e.getSlot()) {
            case 11:
                if (cache.isChunkClaimed(chunkCord) && cache.isChunkOwner(chunkCord, uuid)) {
                    new TrustedChunkSettings(pmu).open();
                    playClickSound(player);
                } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_MANAGE_NOT_CHUNK_OWNER.getString(null));
                break;
            case 13:
                if (cache.isChunkClaimed(chunkCord) && cache.isChunkOwner(chunkCord, uuid)) {
                    new GeneralChunkSettings(pmu).open();
                    playClickSound(player);
                } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_MANAGE_NOT_CHUNK_OWNER.getString(null));
                break;
            case 15:
                new TrustedGlobalSettings(pmu).open();
                playClickSound(player);
                break;
        }
    }

    @Override
    public void setMenuItems() {
        setFillerGlass();

        inventory.setItem(11, trustedItem);
        inventory.setItem(13, generalSettingsItem);
        inventory.setItem(15, globalTrustedItem);
    }
}
