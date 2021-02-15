package lee.code.chunks.menusystem.menus;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.database.SQLite;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.menusystem.Menu;
import lee.code.chunks.menusystem.PlayerMU;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public class ChunkManager extends Menu {

    public ChunkManager(PlayerMU pmu) {
        super(pmu);
    }

    @Override
    public String getMenuName() {
        return Lang.MENU_CHUNK_MANAGER_TITLE.getString(null);
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();

        Player player = pmu.getOwner();

        if (plugin.getData().getPlayerClickDelay(pmu.getOwner().getUniqueId())) return;
        else plugin.getPU().addPlayerClickDelay(pmu.getOwner().getUniqueId());
        if (e.getClickedInventory() == player.getInventory()) return;

        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunk(chunk);
        UUID uuid = player.getUniqueId();
        SQLite SQL = plugin.getSqLite();

        switch (e.getSlot()) {
            case 11:
                if (SQL.isChunkOwner(chunkCord, uuid)) {
                    new TrustedChunkSettings(pmu).open();
                } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_MANAGE_NOT_CHUNK_OWNER.getString(null));
                break;
            case 13:
                if (SQL.isChunkOwner(chunkCord, uuid)) {
                    new GeneralChunkSettings(pmu).open();
                } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_MANAGE_NOT_CHUNK_OWNER.getString(null));
                break;
            case 15:
                new TrustedGlobalSettings(pmu).open();
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
